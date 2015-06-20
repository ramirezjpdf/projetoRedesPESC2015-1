package br.ufrj.cos.redes.fileAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomFileChunkRetrieverExample {
	public static void main(String[] args) throws IOException {
		String readFilePath            = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\mac.pdf";
		String randomWriteFilePath     = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\mac2.pdf";
		String sequencialWriteFilePath = "C:\\Users\\Joao&Duda\\Desktop\\JP\\2015-1\\redes\\mac3.pdf";
		
		FileOutputStream rostream = new FileOutputStream(randomWriteFilePath);
		FileOutputStream sostream = new FileOutputStream(sequencialWriteFilePath);
		
		long chunkLength = 160;
		FileChunkRetriever chunkReceiver = new RandomFileChunkRetriever(new File(readFilePath), chunkLength);
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		while (chunkReceiver.hasNext()) {
			Chunk chunk = new Chunk((int)chunkLength);
			if (chunkReceiver.getNextChunk(chunk)) {
				if (chunk.getActualChunkLength() == chunk.getBytes().length) {
					rostream.write(chunk.getBytes());
				} else {
					rostream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
				}
				
				chunks.add(chunk);
			}
		}
		
		Collections.sort(chunks);
		for (Chunk chunk : chunks) {
			if (chunk.getActualChunkLength() == chunk.getBytes().length) {
				sostream.write(chunk.getBytes());
			} else {
				sostream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
			}
		}
		
		rostream.flush();
		rostream.close();
		sostream.flush();
		sostream.close();
	}
}
