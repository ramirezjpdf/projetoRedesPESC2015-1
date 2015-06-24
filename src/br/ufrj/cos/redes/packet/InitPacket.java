package br.ufrj.cos.redes.packet;

import java.io.Serializable;

public class InitPacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String INIT_MSG = "SENDME";
	
	private String sendedInitMsg;
	private String fileName;

	public InitPacket(String sendedInitMsg, String fileName) {
		this.sendedInitMsg = sendedInitMsg;
		this.fileName = fileName;
	}

	public String getSendedInitMsg() {
		return this.sendedInitMsg;
	}
	
	public String getFileName() {
		return this.fileName;
	}
}
