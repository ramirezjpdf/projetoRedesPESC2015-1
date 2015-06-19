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
		chunkCounter = 0;
		this.chunkLength = chunkLength;
		chunkIdList = new ArrayList<Long>();
		for (long i = 0; i < (fileLength / chunkLength) + 1; i++) {
			chunkIdList.add(new Long(i));
		}
		Collections.shuffle(chunkIdList);
		
	}
	
	@Override
	public boolean getNextChunk(byte[] bytes) throws IOException {
		if (bytes.length != chunkLength) {
			throw new IllegalArgumentException("The byte array passed to this methos must have length = " + chunkLength);
		}
		
		fileReader.seek(chunkIdList.get(chunkCounter++) * chunkLength);
		fileReader.read(bytes);
		fileReader.seek(0);
		return true;
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < chunkIdList.size();
	}

}
