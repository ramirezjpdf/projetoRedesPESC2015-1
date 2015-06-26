package br.ufrj.cos.redes.fileAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrj.cos.redes.constant.Constants;

public class RandomFileChunkRetriever implements FileChunkRetriever {
	private RandomAccessFile fileReader;
	private long fileLength;
	private int chunkCounter;
	private long chunkLength;
	private List<Integer> chunkSeqNumList;
	
	public RandomFileChunkRetriever(File file, long chunkLength) throws FileNotFoundException {
		fileReader = new RandomAccessFile(file, "r");
		fileLength = file.length();
		chunkCounter = 0;
		this.chunkLength = chunkLength;
		chunkSeqNumList = new ArrayList<Integer>();
		for (int i = Constants.INITIAL_CHUNK_SEQ_NUM; i < Constants.INITIAL_CHUNK_SEQ_NUM + (fileLength / chunkLength) + 1; i++) {
			chunkSeqNumList.add(new Integer(i));
		}
		Collections.shuffle(chunkSeqNumList);
	}
	
	@Override
	public boolean getNextChunk(Chunk chunk) throws IOException {
		if (chunk.getBytes().length != chunkLength) {
			throw new IllegalArgumentException("The byte array passed to this methos must have length = " + chunkLength);
		}
		
		int chunkSeqNum = chunkSeqNumList.get(chunkCounter++);
		chunk.setSeqNum(chunkSeqNum);
		fileReader.seek(chunkSeqNum * chunkLength);
		chunk.setActualChunkLength(fileReader.read(chunk.getBytes()));
		fileReader.seek(0);
		return true;
	}

	@Override
	public boolean hasNext() {
		return chunkCounter < chunkSeqNumList.size();
	}
	
	@Override
	public long getTotalFileSize() {
		return this.fileLength;
	}
}
