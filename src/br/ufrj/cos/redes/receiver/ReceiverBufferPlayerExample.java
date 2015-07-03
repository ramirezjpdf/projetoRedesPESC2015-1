package br.ufrj.cos.redes.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulator;
import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulatorDummyTabajara;

public class ReceiverBufferPlayerExample {
	public static void main(String[] args) throws IOException {
		boolean isSender = (args.length == 2);
		
		int N_CHUNKS = 50;
		int B = !isSender ? Integer.parseInt(args[2]) : 10;
		double F = !isSender ? Double.parseDouble(args[3]) : 0.0;
		int B_WINDOW =B + (int) Math.ceil(F*B);
		
		double LAMBDA = 5;
		long RTT = !isSender ? Long.parseLong(args[4]) : Long.parseLong(args[0]);
		
//		String REQUESTED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinput.txt";
//		String RECEIVED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinputrecv.txt";
//		String LOG_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\projetoLog.txt";

		String REQUESTED_FILE_NAME = !isSender ? args[0] : "C:\\Users\\Pedro Freitas\\Desktop\\Declaracao Bras.pdf";
		String RECEIVED_FILE_NAME = !isSender ? args[1] : "C:\\Users\\Pedro Freitas\\Desktop\\Declaracao Bras2.pdf";
		String LOG_FILE_NAME = !isSender ? args[5] : args[1];
		
		InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
		int SERVER_PORT = 29920;
		
		Buffer buffer = new Buffer(B, B);
		DelayLossSimulator simulator = new DelayLossSimulator(F, LAMBDA, RTT, buffer);
		Player player = new Player(new FileOutputStream(RECEIVED_FILE_NAME), new FileOutputStream(LOG_FILE_NAME));
		
		DatagramSocket clientSocket = new DatagramSocket();
		Receiver receiver = new Receiver(buffer, REQUESTED_FILE_NAME, simulator, player);
		receiver.startReceiver(clientSocket, SERVER_ADDRESS, SERVER_PORT);
	}
}
