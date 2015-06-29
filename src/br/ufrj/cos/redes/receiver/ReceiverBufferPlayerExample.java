package br.ufrj.cos.redes.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulator;
import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulatorDummyTabajara;

public class ReceiverBufferPlayerExample {
	public static void main(String[] args) throws IOException {
		int N_CHUNKS = 50;
		int B = 3;
		int B_WINDOW = 6;
		String REQUESTED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinput.txt";
		String RECEIVED_FILE_NAME = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\testinputrecv.txt";
		InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
		int SERVER_PORT = 29920;
		
		Buffer buffer = new Buffer(B, B_WINDOW);
		DelayLossSimulator simulator = new DelayLossSimulatorDummyTabajara(0, 0, 0);
		Player player = new Player(new FileOutputStream(RECEIVED_FILE_NAME));
		
		DatagramSocket clientSocket = new DatagramSocket();
		Receiver receiver = new Receiver(buffer, REQUESTED_FILE_NAME, simulator, player);
		receiver.startReceiver(clientSocket, SERVER_ADDRESS, SERVER_PORT);
	}
}