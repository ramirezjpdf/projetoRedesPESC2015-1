package br.ufrj.cos.redes.receiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.delayLossSimulator.DelayLossSimulator;
import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.InitPacket;

public class Receiver {
	private Buffer buffer;
	private String requestedFilename;
	private Player player;
	private DelayLossSimulator simulator;
	
	public Receiver(Buffer buffer, String requestedFilename, DelayLossSimulator sim, Player player) {
		this.buffer = buffer;
		this.requestedFilename = requestedFilename;
		this.player = player;
		this.simulator = sim;
	}
	
	public void startReceiver(DatagramSocket clientSocket, InetAddress serverAddrress, int serverPort) throws IOException {
		Thread simulatorThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					simulator.receive(clientSocket);
				} catch (ClassNotFoundException e) {
					System.out.println("Error in simulator, ClassNotFoundException");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Error in simulator, IOException");
					e.printStackTrace();
				}
				
			}
		});
		
		simulatorThread.start();
		
		ByteArrayOutputStream byteOstream = new ByteArrayOutputStream();
		new ObjectOutputStream(byteOstream).writeObject(new InitPacket(InitPacket.INIT_MSG, requestedFilename));
		byte[] sendBytes = byteOstream.toByteArray();
		
		DatagramPacket initDatagram = new DatagramPacket(sendBytes, sendBytes.length, serverAddrress, serverPort);
		clientSocket.send(initDatagram);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (buffer.isOver()) {
					System.out.println("Buffer is over.");
					timer.cancel();
					System.exit(0);
					return;
				}
				Chunk chunk = buffer.get();
				try {
					player.play(chunk);
				} catch (IOException e) {
					System.out.println("Player Timer Task: ERROR! Could not play chunk with seqNum " + chunk.getSeqNum());
					e.printStackTrace();
				}
			}
		}, 20, 20);
		
		
		
		
	}
	
}
