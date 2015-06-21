package br.ufrj.cos.redes.fileAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SequencialFileChunkRetriever implements FileChunkRetriever {
	private RandomAccessFile fileReader;
	private long fileLength;
	private int chunkCounter;
	private long chunkLength;
	private int totalChunk;
	private double success;

	public SequencialFileChunkRetriever(File file, long chunkLength, double success) throws FileNotFoundException {
		fileReader = new RandomAccessFile(file, "r");
		fileLength = file.length();
		System.out.println(fileLength);
		chunkCounter = 0;
		this.chunkLength = chunkLength;
		this.success = success;
		totalChunk  = (int) Math.ceil((double) fileLength / (double) chunkLength);
		System.out.println(totalChunk);
	}	
	
	public Chunk raffle(Chunk chunk, double success) {
		boolean available = (Math.random() < success) ? true : false;
		chunk.setAvailable(available);
		return chunk;
	}
	
	@Override
	public boolean getNextChunk(Chunk chunk) throws IOException {
		if (chunk.getBytes().length != chunkLength) {
			throw new IllegalArgumentException("The byte array passed to this methos must have length = " + chunkLength);
		}
		
		long chunkId = chunkCounter++;
		chunk.setSeqNum(chunkId);				
		
		chunk = raffle(chunk, this.success);		
		
		while (!chunk.isAvailable()) {
			chunk = raffle(chunk, this.success);
			System.out.println("Chunk " + chunk.getSeqNum() + " not available! Try again in 20ms");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		fileReader.seek(chunkId * chunkLength);
		chunk.setActualChunkLength(fileReader.read(chunk.getBytes()));
		fileReader.seek(0);
		return true;
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < totalChunk;
	}

}
