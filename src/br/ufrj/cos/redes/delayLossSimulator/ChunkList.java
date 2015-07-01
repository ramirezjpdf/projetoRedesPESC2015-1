package br.ufrj.cos.redes.delayLossSimulator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class ChunkList {

	private SortedMap<Long, Chunk> chunkList;
	
	public ChunkList() {
		chunkList = Collections.synchronizedSortedMap(new TreeMap<Long, Chunk>());
	}
	
	public void add(long tn, Chunk chunk) {
		chunkList.put(tn, chunk);
	}
	
	public Chunk getChunk(long tn) {
		Chunk chunk = chunkList.get(tn);	
		chunkList.remove(tn);
		
		return chunk;		
	}
	
	public long getTime() {		
		return chunkList.firstKey();		
	}
	
	public boolean isOver() {
		return chunkList.isEmpty();
	}	
	
	public void print() {
		Iterator iterator = chunkList.entrySet().iterator(); 
		System.out.println("List of cunks received:");
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			System.out.println("(timeStamp = " + entry.getKey() + ", seqNum = " + ((Chunk) entry.getValue()).getSeqNum() + ")");
		}		
	}
	
	public int size() {
		return chunkList.size();
	}
}



