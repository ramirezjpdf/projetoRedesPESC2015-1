package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.EndPacket;
import br.ufrj.cos.redes.packet.Package;

public class DelayLossSimulator {

	public static void receive(DatagramSocket clientSocket) throws IOException, ClassNotFoundException {		
		double fail = 0.1;
		double LAMBDA = 50;
		double RTT = 600;
		
		Map<Double, Long> tnXseqNum = new TreeMap<Double, Long>();
		
		boolean keep = true;
		
		try {
			while(keep) {				
				System.out.println("Waiting for chunk...");
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
						endPkt = (EndPacket) obj;
					} catch (Exception ee) {
						System.out.println("Error in init object");
					}					
					keep = !endPkt.getSendedEndMsg().equals(EndPacket.END_MSG);					
				}
				
				if(keep) {				
					Chunk chunk = pkg.getChunk();
				
					boolean available = (Math.random() < fail) ? false : true;			
					chunk.setAvailable(available);
					
					if(chunk.isAvailable()) {
						ExponentialSampleGenerator generator = new ExponentialSampleGenerator(LAMBDA);
						double x = generator.getSample();
						
						double tn = pkg.getTimeStamp() + RTT/2 + x;		
						pkg.setTimeStamp(tn);
						
						tnXseqNum.put(pkg.getTimeStamp(), chunk.getSeqNum());
						
						//TODO Send to Buffer when the current time was equal to the new timestamp

						System.out.println("chunk number " + pkg.getChunk().getSeqNum() + " received");
						
					} else {
						System.out.println("chunk number " + chunk.getSeqNum() + " was lost");
					}					
				}				
			}
		
			printMap(tnXseqNum);
			System.out.println("number of chunks received: " + tnXseqNum.size());
			System.out.println("All chunks received");
			
		} finally {
			clientSocket.close();
		}
		
	}	
	
	public static void printMap(Map map) {
		Iterator iterator = map.entrySet().iterator(); 
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			System.out.println("TimeStamp: " + entry.getKey() + " SeqNum: " + entry.getValue());
		}
	}
	
}
