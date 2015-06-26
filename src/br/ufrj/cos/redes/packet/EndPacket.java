package br.ufrj.cos.redes.packet;

import java.io.Serializable;

public class EndPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String END_MSG = "FINISHED";
	
	private String sendedEndMsg;

	public EndPacket(String sendedEndMsg) {
		this.sendedEndMsg = sendedEndMsg;
	}

	public String getSendedEndMsg() {
		return this.sendedEndMsg;
	}
}
