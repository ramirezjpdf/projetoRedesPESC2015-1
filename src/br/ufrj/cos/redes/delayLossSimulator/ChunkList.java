package br.ufrj.cos.redes.delayLossSimulator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class ChunkList {

	private Map<Long, Chunk> chunkList;
	
	public ChunkList() {
		chunkList = Collections.synchronizedMap(new HashMap<Long, Chunk>());
	}
	
	public void add(long tn, Chunk chunk) {
		chunkList.put(tn, chunk);
	}
	
	public Chunk getChunk(long tn) {
		Chunk chunk = chunkList.get(tn);	
		chunkList.remove(tn);
		
		return chunk;		
	}
		
	public boolean isOver() {
		return chunkList.isEmpty();
	}	
	
	public void print() {
		Iterator iterator = chunkList.entrySet().iterator(); 
		System.out.println("List of chunks passed in simulator:");
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			System.out.println("(tn = " + entry.getKey() + ", seqNum = " + ((Chunk) entry.getValue()).getSeqNum() + ")");
		}		
	}
	
	
}



