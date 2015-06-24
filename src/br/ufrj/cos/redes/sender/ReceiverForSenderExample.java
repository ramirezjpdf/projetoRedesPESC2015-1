package br.ufrj.cos.redes.sender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.InitPacket;
import br.ufrj.cos.redes.packet.Package;

public class ReceiverForSenderExample {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String REQUESTED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinput.txt";
		String RECEIVED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinputrecv.txt";
		InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
		int SERVER_PORT = 29920;
		
		ByteArrayOutputStream byteOstream = new ByteArrayOutputStream();
		ObjectOutputStream objOStream = new ObjectOutputStream(byteOstream);
		
		InitPacket initPkt = new InitPacket(InitPacket.INIT_MSG, REQUESTED_FILE_NAME);
		objOStream.writeObject(initPkt);
		byte[] sendBytes = byteOstream.toByteArray();
		
		DatagramPacket sendPkt = new DatagramPacket(sendBytes, sendBytes.length, SERVER_ADDRESS, SERVER_PORT);
		DatagramSocket clientSocket = new DatagramSocket();
		
		List<Chunk> chunks = new ArrayList<Chunk>();
		long totalFileSize = 0;
		long accFileSize = 0;
		FileOutputStream foStream = new FileOutputStream(RECEIVED_FILE_NAME);
		
		System.out.println("Requesting file " + REQUESTED_FILE_NAME);
		clientSocket.send(sendPkt);
		try {
			do {
				System.out.println("Waiting for chunk...");
				byte[] recvBytes = new byte[1024];
				DatagramPacket recvPkt = new DatagramPacket(recvBytes, recvBytes.length);
				clientSocket.receive(recvPkt);
				
				ObjectInputStream objIStream = new ObjectInputStream(new ByteArrayInputStream(recvPkt.getData()));
				Package pkg = (Package) objIStream.readObject();
				totalFileSize = (totalFileSize == 0) ? pkg.getFileSize() : totalFileSize;
				accFileSize += pkg.getChunk().getActualChunkLength();
				chunks.add(pkg.getChunk());
				System.out.println("chunk number " + pkg.getChunk().getSeqNum() + " received");
			} while(accFileSize < totalFileSize);
			
			System.out.println("All chunks received");
			Collections.sort(chunks);
			for (Chunk chunk : chunks) {
				if (chunk.getActualChunkLength() == chunk.getBytes().length) {
					foStream.write(chunk.getBytes());
				} else {
					foStream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
				}
			}
		} finally {
			clientSocket.close();
			foStream.close();
		}
		
		
	}
}
