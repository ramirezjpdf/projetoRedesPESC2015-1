package br.ufrj.cos.redes.sender;

import java.io.File;
import java.io.IOException;

import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.fileAccess.RandomFileChunkRetriever;
import br.ufrj.cos.redes.fileAccess.SequencialFileChunkRetriever;
import br.ufrj.cos.redes.packet.InitPacket;

public class SenderExample {
	public static void main(String[] args) {
		boolean isSequential = (args.length == 2 && args[1] != null);
		
		int PORT = 29920;
		int CHUNK_LENGTH = 160;
		double r = isSequential ? Double.parseDouble(args[1]) : 1.0;
		Sender sender = new Sender(PORT);		
		
		try {
			System.out.println("Initiating Sender");
			InitPacket initPkt = sender.initSender();
			if (!initPkt.getSendedInitMsg().equals(InitPacket.INIT_MSG)) {
				return;
			}
			System.out.println("Message OK!");
			System.out.println("Requested File is " + initPkt.getFileName());
			System.out.println("Sending chunks at constant rate");
			
			
			FileChunkRetriever chunkRetriever = isSequential ? new SequencialFileChunkRetriever(new File(initPkt.getFileName()), CHUNK_LENGTH, r)
														: new RandomFileChunkRetriever(new File(initPkt.getFileName()), CHUNK_LENGTH);
			sender.sendChunksAtConstantRate(chunkRetriever, CHUNK_LENGTH, new Sender.SendChunkEndCallback() {
				@Override
				public void execute() {
					System.out.println("All chunks sended");
					try {
						Thread.sleep(20); //wait a bit, else the receiver cannot get the end msg.
						System.out.println("Sending end msg...");
						sender.sendEndMsg();
						System.out.println("End msg sended.");
					} catch (IOException e) {
						System.out.println("Could not send end msg");
					} catch (InterruptedException e) {
						System.out.println("sleep interrupted");
					} finally {
						sender.close();
					}
				}
			});
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		} finally {
			sender.close();
		}
	}
}
