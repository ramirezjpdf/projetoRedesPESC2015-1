package br.ufrj.cos.redes.delayLossSimulator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import br.ufrj.cos.redes.fileAccess.Chunk;
import br.ufrj.cos.redes.packet.EndPacket;
import br.ufrj.cos.redes.packet.Package;
import br.ufrj.cos.redes.receiver.Buffer;

public class DelayLossSimulatorDummyTabajara extends DelayLossSimulator {

	private Buffer buffer;
	
	public DelayLossSimulatorDummyTabajara(double F, double LAMBDA, long RTT, Buffer buffer) {
		super(F, LAMBDA, RTT, buffer);
		this.buffer = buffer;
		// I'm dummy. I will do nothing with these parameters. 
	}

	@Override
	public void receive(DatagramSocket clientSocket) throws IOException,
			ClassNotFoundException {
		boolean keep = true;
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
					endPkt = (EndPacket) obj;
				} catch (Exception ee) {
					System.out.println("Error in init object");
				}					
				keep = !endPkt.getSendedEndMsg().equals(EndPacket.END_MSG);
				buffer.alertEnd();
			}
			
			if(keep) {				
				Chunk chunk = pkg.getChunk();
				buffer.add(chunk);
			}
		}
	}
}
