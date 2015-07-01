package br.ufrj.cos.redes.packet;

import java.io.Serializable;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class Package implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Chunk chunk;
	
	private long fileSize;
	
	public Package(Chunk chunk) {
		this.chunk = chunk;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	
}
