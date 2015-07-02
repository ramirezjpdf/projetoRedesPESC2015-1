package br.ufrj.cos.redes.receiver;

import java.util.SortedSet;
import java.util.TreeSet;

import br.ufrj.cos.redes.constant.Constants;
import br.ufrj.cos.redes.fileAccess.Chunk;

public class Buffer {

	private SortedSet<Chunk> buffer;
	private int B;
	private int Bcounter;
	private int BrangeLimit;
	private int Bwindow;
	private boolean availableForReceiver;
	private boolean willReceiveMore;
	private long lastSeqNumRemoved;
	
	public Buffer(int B, int Bwindow) {
		if (Bwindow < B) {
			throw new IllegalArgumentException("Buffer constructor: Error!!. Bwindow < B");
		}
		this.B = B;
		this.Bwindow = Bwindow;
		this.BrangeLimit = (Bwindow) + Constants.INITIAL_CHUNK_SEQ_NUM;
		this.Bcounter = 0;
		buffer = new TreeSet<Chunk>();
		this.availableForReceiver = false;
		this.willReceiveMore = true;
	}
	
	public synchronized boolean isOver() {
		return !willReceiveMore && buffer.isEmpty();
	}
	
	public synchronized void alertEnd() {
		willReceiveMore = false;
		System.out.println("Buffer won't receive any more chunks");
		if (!availableForReceiver) {
			availableForReceiver = true;
			System.out.println("If receiver is waiting, it is time to active him.");
			notify();
		}
	}
	
	public synchronized void add(Chunk chunk) {
		if (chunk.getSeqNum() < lastSeqNumRemoved) {
			return;
		}
		else if (buffer.add(chunk) && willReceiveMore) {
			Bcounter = (chunk.getSeqNum() < BrangeLimit) ? (Bcounter + 1) : Bcounter;
			System.out.println("buffer.add(seqNum = " + chunk.getSeqNum() + "): Bcounter = " + Bcounter);
			if (Bcounter >= B && !availableForReceiver) {
				availableForReceiver = true;
				System.out.println("receiver will stop waiting");
				notify();
			}
		}
	}
	
	public synchronized Chunk get() {
		while (!availableForReceiver) {
			try {
				System.out.println("receiver will wait");
				wait();
				System.out.println("receiver is active");
			} catch (InterruptedException e) {
				System.out.println("interrupt excepion get method from buffer");
			}
		}
		Chunk chunk = buffer.first();
		buffer.remove(chunk);
		lastSeqNumRemoved = chunk.getSeqNum();
		Bcounter--;
		System.out.println("buffer.get(seqNum = " + chunk.getSeqNum() + "): Bcounter = " + Bcounter);
		if (willReceiveMore && Bcounter == 0) {
			prepareNewWindow();
		}
		return chunk;
	}
	
	private void prepareNewWindow() {
		Bcounter = 0;
		BrangeLimit += Bwindow;
		Chunk lowestChunkInWindow = new Chunk(0);
		lowestChunkInWindow.setSeqNum(lastSeqNumRemoved + 1);
		Chunk highestChunkInWindow = new Chunk(0);
		highestChunkInWindow.setSeqNum(BrangeLimit);
		System.out.println("Window being prepared to range from seqNum " + lowestChunkInWindow.getSeqNum() + " to seqNum " + highestChunkInWindow.getSeqNum());
		SortedSet<Chunk> alreadyInWindowChunks = buffer.subSet(lowestChunkInWindow, highestChunkInWindow);
		Bcounter += alreadyInWindowChunks.size();
		System.out.println("Window being prepared already has " + Bcounter + " chunks");
		availableForReceiver = Bcounter >= B ? true : false;
	}
	
}
