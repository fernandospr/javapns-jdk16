package javapns.notification;

/**
 * A response packet, as described in Apple's enhanced notification format.
 * 
 * @author Sylvain Pedneault
 */
public class ResponsePacket {

	private int command;
	private int status;
	private int identifier;


	protected ResponsePacket() {
	}


	protected ResponsePacket(int command, int status, int identifier) {
		this.command = command;
		this.status = status;
		this.identifier = identifier;
	}


	protected void linkToPushedNotification(PushNotificationManager notificationManager) {
		PushedNotification notification = null;
		try {
			notification = notificationManager.getPushedNotifications().get(identifier);
			if (notification != null) {
				notification.setResponse(this);
			}
		} catch (Exception e) {
		}
	}


	/**
	 * Returns the response's command number.  It should be 8 for all error responses.
	 *  
	 * @return the response's command number (which should be 8)
	 */
	public int getCommand() {
		return command;
	}


	protected void setCommand(int command) {
		this.command = command;
	}


	/**
	 * Determine if this packet is an error-response packet.
	 * @return true if command number is 8, false otherwise
	 */
	public boolean isErrorResponsePacket() {
		return command == 8;
	}


	/**
	 * Returns the response's status code <i>(see getMessage() for a human-friendly status message instead)</i>.
	 * @return the response's status code
	 */
	public int getStatus() {
		return status;
	}


	protected void setStatus(int status) {
		this.status = status;
	}


	/**
	 * Determine if this packet is a valid error-response packet.
	 * To be valid, it must be an error-response packet (command number 8) and it must have a non-zero status code.
	 * @return true if command number is 8 and status code is not 0, false otherwise
	 */
	public boolean isValidErrorMessage() {
		if (!isErrorResponsePacket()) return false;
		return status != 0;
	}


	/**
	 * Returns the response's identifier, which matches the pushed notification's.
	 * 
	 * @return the response's identifier
	 */
	public int getIdentifier() {
		return identifier;
	}


	protected void setIdentifier(int identifier) {
		this.identifier = identifier;
	}


	/**
	 * Returns a humand-friendly error message, as documented by Apple.
	 * 
	 * @return a humand-friendly error message
	 */
	public String getMessage() {
		if (command == 8) {
			String prefix = "APNS: [" + identifier + "] "; //APNS ERROR FOR MESSAGE ID #" + identifier + ": ";
			if (status == 0) return prefix + "No errors encountered";
			if (status == 1) return prefix + "Processing error";
			if (status == 2) return prefix + "Missing device token";
			if (status == 3) return prefix + "Missing topic";
			if (status == 4) return prefix + "Missing payload";
			if (status == 5) return prefix + "Invalid token size";
			if (status == 6) return prefix + "Invalid topic size";
			if (status == 7) return prefix + "Invalid payload size";
			if (status == 8) return prefix + "Invalid token";
			if (status == 255) return prefix + "None (unknown)";
			return prefix + "Undocumented status code: " + status;
		}
		return "APNS: Undocumented response command: " + command;
	}

}
