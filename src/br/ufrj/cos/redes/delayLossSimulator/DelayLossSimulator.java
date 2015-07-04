package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.EndAckPacket;
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
		int chunkRecvCount = 0;
		try {
			while(keep) {				
				byte[] recvBytes = new byte[1024];
				DatagramPacket recvPkt = new DatagramPacket(recvBytes, recvBytes.length);
				System.out.println("Waiting chunk to complete a total of " + (chunkRecvCount + 1) + " received.");
				clientSocket.receive(recvPkt);
				System.out.println("Received " + ++chunkRecvCount + " chunks already.");
				
				ObjectInputStream objIStream = new ObjectInputStream(new ByteArrayInputStream(recvPkt.getData()));
				
				Package pkg = null;
				Object obj = objIStream.readObject();
				try {					
					pkg = (Package) obj;
				} catch (ClassCastException e) {
					EndPacket endPkt = null;					
					try {
						System.out.println("End Msg received");
						System.out.println("Total chunks received is " + chunkRecvCount);
						endPkt = (EndPacket) obj;
					} catch (Exception ee) {
						System.out.println("Error in init object");
					}					
					keep = !endPkt.getSendedEndMsg().equals(EndPacket.END_MSG);
					sendEndAck(endPkt);
					
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
						
						//this prevents repetition of tn
						while (chunkList.hasChunkWithTn(tn)) {
							tn++;
						}
						// unfortunately we need a final variable inside the run method. 
						long finalTn = tn; 
						chunk.setRecTimeStamp(finalTn);
						// gambiarra we need this to find the real played timestamp
						chunk.setPlayedTimeStamp(Calendar.getInstance().getTimeInMillis());
						chunkList.add(finalTn, chunk);
						
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								System.out.println("tn = " + finalTn);
								Chunk c;
								if (chunkList.hasChunkWithTn(finalTn)) {
									c = chunkList.getChunk(finalTn);
									if (c != null) {
										System.out.println("chunk for tn = " + finalTn + ": chunk.seqNum = " + c.getSeqNum());
										buffer.add(c);
									}
									else {
										System.out.println("chunk for tn = " + finalTn + "is ====NULL====");
									}
								}
//								chunkList.print();
							}
						}, tn);
						
						
					} else {
						System.out.println("chunk with number " + chunk.getSeqNum() + " was lost in the simulator");
					}					
				}				
			}
			
		} finally {
			clientSocket.close();
		}
		
	}
	
	private void sendEndAck(EndPacket endpkt) throws IOException {
		DatagramSocket ackSocket = new DatagramSocket();
		try {
			ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
			ObjectOutputStream objOStream = new ObjectOutputStream(byteOStream);
			
			EndAckPacket endAckPkt = new EndAckPacket(EndAckPacket.END_ACK_MSG);
			objOStream.writeObject(endAckPkt);
			byte[] ackBytes = byteOStream.toByteArray();
			DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length, endpkt.getServerAddress(), endpkt.getServerAckPort());
			
			ackSocket.send(ack);
		} finally {
			ackSocket.close();
		}
		
	}
	
}
