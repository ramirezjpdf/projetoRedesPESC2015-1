package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.EndPacket;
import br.ufrj.cos.redes.packet.Package;
import br.ufrj.cos.redes.receiver.Buffer;

public class DelayLossSimulator {
	
	private double F;
	private double LAMBDA;
	private long RTT;
	private Buffer buffer;
	private ChunkList chunkList;

	public DelayLossSimulator(double F, double LAMBDA, long RTT, Buffer buffer) {
		this.F = F;
		this.LAMBDA = LAMBDA;
		this.RTT = RTT;
		this.buffer = buffer;
		this.chunkList = new ChunkList();
	}

	public void receive(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {			
		boolean keep = true;
		
		try {
			while(keep) {				
				byte[] recvBytes = new byte[1024];
				DatagramPacket recvPkt = new DatagramPacket(recvBytes, recvBytes.length);
				clientSocket.receive(recvPkt);
				
				ObjectInputStream objIStream = new ObjectInputStream(new ByteArrayInputStream(recvPkt.getData()));
				
				Package pkg = null;
				Object obj = objIStream.readObject();
				try {					
					pkg = (Package) obj;					
				} catch (ClassCastException e) {
					EndPacket endPkt = null;					
					try {
						System.out.println("End Msg received");
						endPkt = (EndPacket) obj;
					} catch (Exception ee) {
						System.out.println("Error in init object");
					}					
					keep = !endPkt.getSendedEndMsg().equals(EndPacket.END_MSG);	

					System.out.println("waiting for last chunk in simulator...");
					do {} while(!chunkList.isOver());
					buffer.alertEnd();				
				}
				
				if(keep) {				
					Chunk chunk = pkg.getChunk();
				
					boolean available = (Math.random() < F) ? false : true;			
					chunk.setAvailable(available);
					
					if(chunk.isAvailable()) {
						ExponentialSampleGenerator generator = new ExponentialSampleGenerator(LAMBDA);
						long x = (long) (generator.getSample()*1000);
						
						long tn = chunk.getTransTimeStamp() + RTT/2 + x;	
						chunk.setRecTimeStamp(tn);
						
						chunkList.add(tn, chunk);
						
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								System.out.println("tn = " + tn);
//								chunkList.print();
								buffer.add((Chunk) chunkList.getChunk(tn));
							}
						}, tn);
						
						
					} else {
						System.out.println("chunk with number " + chunk.getSeqNum() + " was lost in receive size simulator");
					}					
				}				
			}
			
		} finally {
			clientSocket.close();
		}
		
	}	
	
}
