package br.ufrj.cos.redes.packet;

import java.io.Serializable;
import java.net.InetAddress;

public class EndPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String END_MSG = "FINISHED";
	
	private String sendedEndMsg;
	private InetAddress serverAddress;
	private int serverAckPort;

	public EndPacket(String sendedEndMsg) {
		this.sendedEndMsg = sendedEndMsg;
	}

	public String getSendedEndMsg() {
		return this.sendedEndMsg;
	}

	public InetAddress getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(InetAddress serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerAckPort() {
		return serverAckPort;
	}

	public void setServerAckPort(int serverAckPort) {
		this.serverAckPort = serverAckPort;
	}
}
