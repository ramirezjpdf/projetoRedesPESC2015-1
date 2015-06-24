package br.ufrj.cos.redes.sender;

import java.io.File;
import java.io.IOException;

import br.ufrj.cos.redes.fileAccess.FileChunkRetriever;
import br.ufrj.cos.redes.fileAccess.RandomFileChunkRetriever;
import br.ufrj.cos.redes.packet.InitPacket;

public class SenderExample {
	public static void main(String[] args) {
		int PORT = 29920;
		int CHUNK_LENGTH = 160;
		Sender sender = new Sender(PORT);
		
		try {
			System.out.println("Initiating Sender");
			InitPacket initPkt = sender.initSender();
			if (!initPkt.getSendedInitMsg().equals(InitPacket.INIT_MSG)) {
				return;
			}
			System.out.println("Message OK!");
			System.out.println("Requested File is " + initPkt.getFileName());
			System.out.println("Initiating sending of chunks");
			FileChunkRetriever chunkRetriever = new RandomFileChunkRetriever(new File(initPkt.getFileName()), CHUNK_LENGTH);
			sender.initiateSending(chunkRetriever, CHUNK_LENGTH);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		} finally {
			sender.close();
		}
	}
}
