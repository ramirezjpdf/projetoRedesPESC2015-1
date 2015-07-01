package br.ufrj.cos.redes.fileAccess;

import java.io.Serializable;

public class Chunk implements Comparable<Chunk>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long seqNum;
	private byte[] bytes;
	private int actualChunkLength;
	private boolean available;
	private long transTimeStamp;
	private long recTimeStamp;
	private long playedTimeStamp;
	
	

	public Chunk(int chunkLength) {
		bytes = new byte[chunkLength];
	}
	
	public Chunk(int chunkLength, boolean available) {
		bytes = new byte[chunkLength];
		this.available = available;
	}
	
	public long getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(long seqNum) {
		this.seqNum = seqNum;
	}
	public byte[] getBytes() {
		return bytes;
	}

	public int getActualChunkLength() {
		return actualChunkLength;
	}

	public void setActualChunkLength(int actualChunkLength) {
		this.actualChunkLength = actualChunkLength;
	}

	@Override
	public int compareTo(Chunk otherChunk) {
		if (this.seqNum < otherChunk.getSeqNum()) {
			return -1;
		} else if (this.seqNum > otherChunk.getSeqNum()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public long getTransTimeStamp() {
		return transTimeStamp;
	}
	
	public void setTransTimeStamp(long transTimeStamp) {
		this.transTimeStamp = transTimeStamp;
	}
	
	public long getRecTimeStamp() {
		return recTimeStamp;
	}
	
	public void setRecTimeStamp(long recTimeStamp) {
		this.recTimeStamp = recTimeStamp;
	}
	
	public long getPlayedTimeStamp() {
		return playedTimeStamp;
	}
	
	public void setPlayedTimeStamp(long playedTimeStamp) {
		this.playedTimeStamp = playedTimeStamp;
	}
}

