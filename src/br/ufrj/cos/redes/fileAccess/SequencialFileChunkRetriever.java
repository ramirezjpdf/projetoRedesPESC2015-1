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
		chunk =  raffle(chunk, this.success);
		if (chunk.getBytes().length != chunkLength) {
			throw new IllegalArgumentException("The byte array passed to this methos must have length = " + chunkLength);
		}
		
		long chunkId = chunkCounter++;
		chunk.setSeqNum(chunkId);

		if(chunk.isAvailable()) {
			fileReader.seek(chunkId * chunkLength);
			chunk.setActualChunkLength(fileReader.read(chunk.getBytes()));
			fileReader.seek(0);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < totalChunk;
	}

}
