package br.ufrj.cos.redes.fileAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequencialFileChunkRetrieverExample {
	
	public static void main(String[] args) throws IOException {
		String readFilePath            = "C:\\Users\\Pedro Freitas\\Desktop\\mac.pdf";
		String sequencialWriteFilePath = "C:\\Users\\Pedro Freitas\\Desktop\\mac2.pdf";
		
		FileOutputStream sostream = new FileOutputStream(sequencialWriteFilePath);
		
		long chunkLength = 160;
		FileChunkRetriever chunkReceiver = new SequencialFileChunkRetriever(new File(readFilePath), chunkLength, 0.1);
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		while (chunkReceiver.hasNext()) {
			Chunk chunk = new Chunk((int)chunkLength);
			if (chunkReceiver.getNextChunk(chunk)) {
				if (chunk.getActualChunkLength() == chunk.getBytes().length) {
					sostream.write(chunk.getBytes());
				} else {
					sostream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
				}
				
				chunks.add(chunk);
			}
			else {
				System.out.println("Failed chunk " + chunk.getSeqNum() + " !");
			}
		}
		
		sostream.flush();
		sostream.close();
	}
		
}
