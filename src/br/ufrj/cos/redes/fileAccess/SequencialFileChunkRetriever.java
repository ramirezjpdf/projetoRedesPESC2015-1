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
	private double r;

	public SequencialFileChunkRetriever(File file, long chunkLength, double r) throws FileNotFoundException {
		fileReader = new RandomAccessFile(file, "r");
		fileLength = file.length();
		System.out.println("File size: " + fileLength + " bytes");
		chunkCounter = 0;
		this.chunkLength = chunkLength;
		this.r = r;
		totalChunk  = (int) Math.ceil((double) fileLength / (double) chunkLength);
		System.out.println("Total of chunks: "+ totalChunk);
		System.out.println("r = " + r);
	}	
	
	public Chunk lottery(Chunk chunk, double success) {
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
		chunk = lottery(chunk, this.r);
		
		if(chunk.isAvailable()) {		
			fileReader.seek(chunkId * chunkLength);
			chunk.setActualChunkLength(fileReader.read(chunk.getBytes()));
			fileReader.seek(0);
		} else {
			chunkId = --chunkCounter;
			return false;
		}
		
		return true;
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < totalChunk;
	}
	
	@Override
	public long getTotalFileSize() {
		return this.fileLength;
	}

}
