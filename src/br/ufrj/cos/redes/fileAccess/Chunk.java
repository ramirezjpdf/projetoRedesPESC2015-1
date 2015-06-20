package br.ufrj.cos.redes.fileAccess;

public class Chunk implements Comparable<Chunk>{
	private long seqNum;
	private byte[] bytes;
	private int actualChunkLength;
	
	public Chunk(int chunkLength) {
		bytes = new byte[chunkLength];
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
	
}
