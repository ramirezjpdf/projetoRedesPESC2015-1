package br.ufrj.cos.redes.timeInfo;

import java.io.Serializable;

public class TimestampInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long transmitionTimeStamp;
	private long receivedTimeStamp;
	private long playedTimeStamp;
	
	public long getTransmitionTimeStamp() {
		return transmitionTimeStamp;
	}
	public void setTransmitionTimeStamp(long transmitionTimeStamp) {
		this.transmitionTimeStamp = transmitionTimeStamp;
	}
	public long getReceivedTimeStamp() {
		return receivedTimeStamp;
	}
	public void setReceivedTimeStamp(long receivedTimeStamp) {
		this.receivedTimeStamp = receivedTimeStamp;
	}
	public long getPlayedTimeStamp() {
		return playedTimeStamp;
	}
	public void setPlayedTimeStamp(long playedTimeStamp) {
		this.playedTimeStamp = playedTimeStamp;
	}
}
