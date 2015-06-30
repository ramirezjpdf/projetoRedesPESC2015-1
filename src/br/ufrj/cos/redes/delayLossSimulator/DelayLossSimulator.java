package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.EndPacket;
import br.ufrj.cos.redes.packet.Package;
import br.ufrj.cos.redes.receiver.Buffer;

public class DelayLossSimulator {
	
	private double F;
	private double LAMBDA;
	private double RTT;

	public DelayLossSimulator(double F, double LAMBDA, double RTT) {
		this.F = F;
		this.LAMBDA = LAMBDA;
		this.RTT = RTT;
	}

	public void receive(DatagramSocket clientSocket, Buffer buffer) throws IOException, ClassNotFoundException {		
		TreeMap<Double, Chunk> tnXChunk = new TreeMap<Double, Chunk>();
		
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
					buffer.alertEnd();
				}
				
				if(keep) {				
					Chunk chunk = pkg.getChunk();
				
					boolean available = (Math.random() < F) ? false : true;			
					chunk.setAvailable(available);
					
					if(chunk.isAvailable()) {
						ExponentialSampleGenerator generator = new ExponentialSampleGenerator(LAMBDA);
						double x = generator.getSample();
						
						double tn = pkg.getTimeStamp() + RTT/2 + x;		
						pkg.setTimeStamp(tn);
						
						tnXChunk.put(pkg.getTimeStamp(), chunk);
						
						int firstTn = (int) Math.round(tnXChunk.firstKey());
						Entry firstEntry = tnXChunk.firstEntry();
						
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								buffer.add((Chunk) tnXChunk.firstEntry().getValue());								
								tnXChunk.remove(firstEntry);
								System.out.println("chunk number " + ((Chunk) firstEntry).getSeqNum() + " send to buffer at " + firstTn + "ms");
								timer.cancel();
							}
						}, firstTn, firstTn);
						
						
					} else {
						System.out.println("chunk number " + chunk.getSeqNum() + " was lost");
					}					
				}				
			}
		
			printMap(tnXChunk);
			System.out.println("number of chunks received: " + tnXChunk.size());
			System.out.println("All chunks received");
			
		} finally {
			clientSocket.close();
		}
		
	}	
	
	public static void printMap(Map map) {
		Iterator iterator = map.entrySet().iterator(); 
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			System.out.println("List of cunks received");
			System.out.println("TimeStamp: " + entry.getKey() + " SeqNum: " + ((Chunk) entry.getValue()).getSeqNum());
		}
	}
	
}
