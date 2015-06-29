package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import br.ufrj.cos.redes.packet.InitPacket;

public class ReceiverForDelayLossSimulatorExample {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String REQUESTED_FILE_NAME = "C:\\Users\\Pedro Freitas\\Desktop\\mac.pdf";
		InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
		int SERVER_PORT = 29920;
		double F = 0.1;
		double RTT = 600;
		double LAMBDA = 50;
		
		ByteArrayOutputStream byteOstream = new ByteArrayOutputStream();
		ObjectOutputStream objOStream = new ObjectOutputStream(byteOstream);
		
		InitPacket initPkt = new InitPacket(InitPacket.INIT_MSG, REQUESTED_FILE_NAME);
		objOStream.writeObject(initPkt);
		byte[] sendBytes = byteOstream.toByteArray();
		
		DatagramPacket sendPkt = new DatagramPacket(sendBytes, sendBytes.length, SERVER_ADDRESS, SERVER_PORT);
		DatagramSocket clientSocket = new DatagramSocket();
				
		System.out.println("Requesting file " + REQUESTED_FILE_NAME);
		clientSocket.send(sendPkt);
		
		new DelayLossSimulator(F, LAMBDA, RTT).receive(clientSocket, null);
		
	}
}
