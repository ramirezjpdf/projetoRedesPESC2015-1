package br.ufrj.cos.redes.sender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.packet.InitPacket;
import br.ufrj.cos.redes.packet.Package;

public class Sender {
	private DatagramSocket serverSocket;
	private int  port;
	private ReceiverInfo receiverInfo;
	
	private final long DELAY = 20; //milliseconds
	private final long INTERVAL = 20; //milliseconds
	
	public Sender(int port) {
		this.port = port;
	}
	
	public InitPacket initSender() throws IOException {
		try {
			serverSocket = new DatagramSocket(port);
		} catch(SocketException e) {
			throw new IOException("The port number " + port + " is already in use.");
		}
		
		byte[] recvInitPackeByteArray = new byte[1024];
		DatagramPacket pkg = new DatagramPacket(recvInitPackeByteArray, recvInitPackeByteArray.length);
		serverSocket.receive(pkg);
		receiverInfo = new ReceiverInfo(pkg.getPort(), pkg.getAddress());
		ObjectInputStream objInputStream = new ObjectInputStream(new ByteArrayInputStream(pkg.getData()));
		
		try {
			InitPacket initPkt = (InitPacket) objInputStream.readObject();
			return initPkt;
		} catch (Exception e) {
			System.out.println("Error in init object");
			return new InitPacket("ERROR", "");
		}
		
	}
	
	public void sendChunksAtConstantRate(FileChunkRetriever chunkRetriever, int chunkLength, SendChunkEndCallback callback) {
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!chunkRetriever.hasNext()) {
					timer.cancel();
					callback.execute();
					return;
				}
				
				Chunk chunk = new Chunk(chunkLength);
				try {
					if (chunkRetriever.getNextChunk(chunk)) {						
						ByteArrayOutputStream byteArrayOStream = new ByteArrayOutputStream();
						ObjectOutputStream  objOutputStream= new ObjectOutputStream(byteArrayOStream);
						
						Package pkg = new Package(chunk);
						pkg.setFileSize(chunkRetriever.getTotalFileSize());
						pkg.setTimeStamp(Calendar.getInstance().getTimeInMillis());
						
						objOutputStream.writeObject(pkg);
						DatagramPacket sendPkt = new DatagramPacket(byteArrayOStream.toByteArray(),
																	byteArrayOStream.size(),
																	receiverInfo.address,
																	receiverInfo.getPort());
						if (serverSocket.isClosed()) {
							serverSocket = new DatagramSocket(port);
						}
						serverSocket.send(sendPkt);
					}
				} catch (IOException e) {
					//TODO Decide what to do
					e.printStackTrace();
				}
			}
		}, DELAY, INTERVAL);
	}
	
	public void close() {
		if (!serverSocket.isClosed()) {
			serverSocket.close();
		}
	}
	
	private class ReceiverInfo {
		private InetAddress address;
		private int port;

		public ReceiverInfo(int port, InetAddress address) {
			this.port = port;
			this.address = address;
		}

		public InetAddress getAddress() {
			return address;
		}

		public void setAddress(InetAddress address) {
			this.address = address;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}
	
	public interface SendChunkEndCallback {
		void execute();
	}
}
