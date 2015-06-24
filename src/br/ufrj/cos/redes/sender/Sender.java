package br.ufrj.cos.redes.sender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.packet.InitPacket;

public class Sender {
	private DatagramSocket serverSocket;
	private int  port;
	
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
		
		ObjectInputStream objInputStream = new ObjectInputStream(new ByteArrayInputStream(pkg.getData()));
		
		try {
			InitPacket initPkt = (InitPacket) objInputStream.readObject();
			return initPkt;
		} catch (Exception e) {
			System.out.println("Error in init object");
			return new InitPacket("ERROR", "");
		}
		
	}
	
	public void initiateSending(FileChunkRetriever chunkRetriever) {
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!chunkRetriever.hasNext()) {
					timer.cancel();
				}
				
				Chunk chunk = null;
				try {
					if (chunkRetriever.getNextChunk(chunk)) {
						ByteArrayOutputStream byteArrayOStream = new ByteArrayOutputStream();
						ObjectOutputStream  objOutputStream= new ObjectOutputStream(byteArrayOStream);
						objOutputStream.writeObject(chunk);
						DatagramPacket sendPkt = new DatagramPacket(byteArrayOStream.toByteArray(), byteArrayOStream.size());
						serverSocket.send(sendPkt);
					}
				} catch (IOException e) {
					//TODO Decide what to do
					e.printStackTrace();
				}
			}
		}, DELAY, INTERVAL);
	}
}
