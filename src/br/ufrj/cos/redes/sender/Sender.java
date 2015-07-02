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
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.packet.EndPacket;
import br.ufrj.cos.redes.packet.InitPacket;
import br.ufrj.cos.redes.packet.Package;

public class Sender {
	private DatagramSocket serverSocket;
	private int  port;
	private ReceiverInfo receiverInfo;
	
	private final long DELAY = 20; //milliseconds
	private final long INTERVAL = 20; //milliseconds
	
	private int count = 0;
	
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
						chunk.setTransTimeStamp(INTERVAL + (INTERVAL*count++));
						
						objOutputStream.writeObject(pkg);
						DatagramPacket sendPkt = new DatagramPacket(byteArrayOStream.toByteArray(),
													 				byteArrayOStream.size(),
												 					receiverInfo.getAddress(),
												 					receiverInfo.getPort());
						if (serverSocket.isClosed()) {
							serverSocket = new DatagramSocket(port);
						}
						serverSocket.send(sendPkt);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, DELAY, INTERVAL);
	}
	
	public void sendEndMsg() throws IOException {
		EndPacket endPkt = new EndPacket(EndPacket.END_MSG);
		ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
		new ObjectOutputStream(outByteArray).writeObject(endPkt);
		DatagramPacket endDatagram = new DatagramPacket(outByteArray.toByteArray(),
														outByteArray.size(),
														receiverInfo.getAddress(),
														receiverInfo.getPort());
		if (serverSocket.isClosed()) {
			serverSocket = new DatagramSocket(port);
		}
		serverSocket.send(endDatagram);
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

		public int getPort() {
			return port;
		}
	}
	
	public interface SendChunkEndCallback {
		void execute();
	}
}
