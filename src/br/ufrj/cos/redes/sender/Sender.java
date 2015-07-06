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
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.packet.EndAckPacket;
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
						long transmitionLatencyAndTimestamp = INTERVAL + (INTERVAL * count++);
						chunk.getLatencyInfo().setTransmitionLatency(transmitionLatencyAndTimestamp);
						chunk.getTimestampInfo().setTransmitionTimeStamp(transmitionLatencyAndTimestamp);
						
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
		final long SEND_DELAY = 250;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				EndPacket endPkt = new EndPacket(EndPacket.END_MSG);
				try {
					endPkt.setServerAddress(InetAddress.getLocalHost());
				} catch (UnknownHostException e2) {
					System.out.println("Could not resolve local host");
					return;
				}
				endPkt.setServerAckPort(port);
				ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
				DatagramSocket sendServerSocket = null;
				try {
					sendServerSocket = new DatagramSocket();
				} catch (SocketException e1) {
					System.out.println("An Error occurred when creating socket for sending end msg, Trying again after " + SEND_DELAY + "ms.");
					return;
				}
				try {
					new ObjectOutputStream(outByteArray).writeObject(endPkt);
					System.out.println("Sending end msg... ");
					DatagramPacket endDatagram = new DatagramPacket(outByteArray.toByteArray(),
																	outByteArray.size(),
																	receiverInfo.getAddress(),
																	receiverInfo.getPort());
					sendServerSocket.send(endDatagram);
					System.out.println("End msg sended. wating ack...");
				} catch (IOException e) {
					System.out.println("An Error occurred when sending end msg, Trying again after " + SEND_DELAY + "ms.");
					
				} finally {
					sendServerSocket.close();
				}
			}
		}, SEND_DELAY, SEND_DELAY);
		
		
		if (serverSocket.isClosed()) {
			serverSocket = new DatagramSocket(port);
		}
		while (true) {
			byte []ackBytes = new byte[1024];
			DatagramPacket endAckPck = new DatagramPacket(ackBytes, ackBytes.length);
			serverSocket.receive(endAckPck);
			ObjectInputStream objIStream = new ObjectInputStream(new ByteArrayInputStream(ackBytes));
			try {
				EndAckPacket ack = (EndAckPacket) objIStream.readObject();
				if (ack.getMsg().equals(EndAckPacket.END_ACK_MSG)) {
					System.out.println("Received end Ack Msg from Receiver! Exiting program.");
					serverSocket.close();
					objIStream.close();
					timer.cancel();
					break;
				} else {
					System.out.println("The msg received is not an Ack for end msg. Continue to wait ack for end msg.");
				}
			} catch(ClassCastException e) {
				System.out.println("The msg received is not an Ack for end msg. Continue to wait ack for end msg.");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
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
