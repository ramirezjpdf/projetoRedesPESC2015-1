package br.ufrj.cos.redes.receiver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulator;

public class ReceiverBufferPlayerExample {
	public static void main(String[] args) {	
		int B = Integer.parseInt(args[2]);
		double F = Double.parseDouble(args[3]);
		int B_WINDOW = B + (int) Math.ceil(F*B);
		
		double LAMBDA = 5;
		long RTT = Long.parseLong(args[4]);
		
		String REQUESTED_FILE_NAME = args[0];
		String RECEIVED_FILE_NAME = args[1];
		String TIMESTAMP_LOG_FILE_NAME = args[5];
		String LATENCY_LOG_FILE_NAME = args[6];
		
		InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
		int SERVER_PORT = 29920;
		
		Buffer buffer = new Buffer(B, B_WINDOW);
		DelayLossSimulator simulator = new DelayLossSimulator(F, LAMBDA, RTT, buffer);
		Player player = null;
		try {
			player = new Player(new FileOutputStream(RECEIVED_FILE_NAME),
								new FileOutputStream(TIMESTAMP_LOG_FILE_NAME),
								new FileOutputStream(LATENCY_LOG_FILE_NAME));
		} catch (FileNotFoundException e) {
			System.out.println("Error in player, FileNotFoundException"); 
			e.printStackTrace();
		}		
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Error in clientSocket, SocketException"); 
			e.printStackTrace();
		}
		Receiver receiver = new Receiver(buffer, REQUESTED_FILE_NAME, simulator, player);
		try {
			receiver.startReceiver(clientSocket, SERVER_ADDRESS, SERVER_PORT);
		} catch (IOException e) {
			System.out.println("Error in receiver, IOException"); 
			e.printStackTrace();
		}
	}
}
