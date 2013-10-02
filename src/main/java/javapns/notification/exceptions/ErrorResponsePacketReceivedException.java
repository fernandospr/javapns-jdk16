package javapns.notification.exceptions;

import javapns.notification.*;

/**
 * Thrown when an error response packet was received from an APNS server.
 * 
 * @author Sylvain Pedneault
 */
@SuppressWarnings("serial")
public class ErrorResponsePacketReceivedException extends Exception {

	private ResponsePacket packet;


	public ErrorResponsePacketReceivedException(ResponsePacket packet) {
		super(String.format("An error response packet was received from the APNS server: %s", packet.getMessage()));
		this.packet = packet;
	}


	public ResponsePacket getPacket() {
		return packet;
	}

}
