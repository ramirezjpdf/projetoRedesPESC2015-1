package br.ufrj.cos.redes.fileAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomFileChunkRetriever implements FileChunkRetriever {
	private RandomAccessFile fileReader;
	private long fileLength;
	private int chunkCounter;
	private long chunkLength;
	private List<Long> chunkIdList;
	
	public RandomFileChunkRetriever(File file, long chunkLength) throws FileNotFoundException {
		fileReader = new RandomAccessFile(file, "r");
		fileLength = file.length();
		System.out.println(fileLength);
		chunkCounter = 0;
		this.chunkLength = chunkLength;
		chunkIdList = new ArrayList<Long>();
		for (long i = 0; i < (fileLength / chunkLength) + 1; i++) {
			chunkIdList.add(new Long(i));
		}
		Collections.shuffle(chunkIdList);
	}
	
	@Override
	public boolean getNextChunk(Chunk chunk) throws IOException {
		if (chunk.getBytes().length != chunkLength) {
			throw new IllegalArgumentException("The byte array passed to this methos must have length = " + chunkLength);
		}
		
		long chunkId = chunkIdList.get(chunkCounter++);
		chunk.setSeqNum(chunkId);
		fileReader.seek(chunkId * chunkLength);
		chunk.setActualChunkLength(fileReader.read(chunk.getBytes()));
		fileReader.seek(0);
		return true;
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < chunkIdList.size();
	}
}
