package br.ufrj.cos.redes.timeInfo;

import java.io.Serializable;

public class LatencyInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long transmitionLatency;
	private long receivedLatency;
	private long playedLatency;
	
	public long getTransmitionLatency() {
		return transmitionLatency;
	}
	public void setTransmitionLatency(long transmitionLatency) {
		this.transmitionLatency = transmitionLatency;
	}
	public long getReceivedLatency() {
		return receivedLatency;
	}
	public void setReceivedLatency(long receivedLatency) {
		this.receivedLatency = receivedLatency;
	}
	public long getPlayedLatency() {
		return playedLatency;
	}
	public void setPlayedLatency(long playedLatency) {
		this.playedLatency = playedLatency;
	}
}
