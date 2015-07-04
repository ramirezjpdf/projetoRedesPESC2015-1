package br.ufrj.cos.redes.packet;

import java.io.Serializable;

public class EndAckPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String END_ACK_MSG = "END_ACK_MSG";
	
	private String msg;

	public EndAckPacket(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}
