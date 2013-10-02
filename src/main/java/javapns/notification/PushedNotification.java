package javapns.notification;

import java.util.*;

import javapns.devices.*;
import javapns.notification.exceptions.*;

/**
 * <p>An object representing the result of a push notification to a specific payload to a single device.</p>
 * 
 * <p>If any error occurred while trying to push the notification, an exception is attached.</p>
 * 
 * <p>If Apple's Push Notification Service returned an error-response packet, it is linked to the related PushedNotification
 * so you can find out what the actual error was.</p>
 * 
 * @author Sylvain Pedneault
 */
public class PushedNotification {

	private Payload payload;
	private Device device;
	private ResponsePacket response;

	private int identifier;
	private long expiry;
	private int transmissionAttempts;
	private boolean transmissionCompleted;

	private Exception exception;


	protected PushedNotification(Device device, Payload payload) {
		this.device = device;
		this.payload = payload;
	}


	protected PushedNotification(Device device, Payload payload, int identifier) {
		this.device = device;
		this.payload = payload;
		this.identifier = identifier;
	}


	public PushedNotification(Device device, Payload payload, Exception exception) {
		this.device = device;
		this.payload = payload;
		this.exception = exception;
	}


	/**
	 * Returns the payload that was pushed.
	 * 
	 * @return the payload that was pushed
	 */
	public Payload getPayload() {
		return payload;
	}


	protected void setPayload(Payload payload) {
		this.payload = payload;
	}


	/**
	 * Returns the device that the payload was pushed to.
	 * @return the device that the payload was pushed to
	 */
	public Device getDevice() {
		return device;
	}


	protected void setDevice(Device device) {
		this.device = device;
	}


	/**
	 * Returns the connection-unique identifier referred to by
	 * error-response packets.
	 * 
	 * @return a connection-unique identifier
	 */
	public int getIdentifier() {
		return identifier;
	}


	protected void setIdentifier(int identifier) {
		this.identifier = identifier;
	}


	/**
	 * Returns the expiration date of the push notification.
	 * 
	 * @return the expiration date of the push notification.
	 */
	public long getExpiry() {
		return expiry;
	}


	protected void setExpiry(long expiry) {
		this.expiry = expiry;
	}


	protected void setTransmissionAttempts(int transmissionAttempts) {
		this.transmissionAttempts = transmissionAttempts;
	}


	protected void addTransmissionAttempt() {
		transmissionAttempts++;
	}


	/**
	 * Returns the number of attempts that have been made to transmit the notification.
	 * @return a number of attempts
	 */
	public int getTransmissionAttempts() {
		return transmissionAttempts;
	}


	/**
	 * Returns a human-friendly description of the number of attempts made to transmit the notification.
	 * @return a human-friendly description of the number of attempts made to transmit the notification
	 */
	public String getLatestTransmissionAttempt() {
		if (transmissionAttempts == 0) return "no attempt yet";
		switch (transmissionAttempts) {
			case 0:
				return "no attempt yet";
			case 1:
				return "first attempt";
			case 2:
				return "second attempt";
			case 3:
				return "third attempt";
			case 4:
				return "fourth attempt";
			default:
				return "attempt #" + transmissionAttempts;
		}
	}


	protected void setTransmissionCompleted(boolean completed) {
		this.transmissionCompleted = completed;
	}


	/**
	 * Indicates if the notification has been streamed successfully to Apple's server.
	 * This does <b>not</b> indicate if an error-response was received or not, but simply
	 * that the library successfully completed the transmission of the notification to
	 * Apple's server.
	 * @return true if the notification was successfully streamed to Apple, false otherwise
	 */
	public boolean isTransmissionCompleted() {
		return transmissionCompleted;
	}


	protected void setResponse(ResponsePacket response) {
		this.response = response;
		if (response != null && exception == null) exception = new ErrorResponsePacketReceivedException(response);
	}


	/**
	 * If a response packet regarding this notification was received,
	 * this method returns it. Otherwise it returns null.
	 * 
	 * @return a response packet, if one was received for this notification
	 */
	public ResponsePacket getResponse() {
		return response;
	}


	/**
	 * <p>Returns true if no response packet was received for this notification,
	 * or if one was received but is not an error-response (ie command 8),
	 * or if one was received but its status is 0 (no error occurred).</p>
	 * 
	 * <p>Returns false if an error-response packet is attached and has
	 * a non-zero status code.</p>
	 * 
	 * <p>Returns false if an exception is attached.</p>
	 * 
	 * <p>Make sure you use the Feedback Service to cleanup your list of
	 * invalid device tokens, as Apple's documentation says.</p>
	 * 
	 * @return true if push was successful, false otherwise
	 */
	public boolean isSuccessful() {
		if (!transmissionCompleted) return false;
		if (response == null) return true;
		if (!response.isValidErrorMessage()) return true;
		return false;
	}


	/**
	 * Filters a list of pushed notifications and returns only the ones that were successful.
	 * 
	 * @param notifications a list of pushed notifications
	 * @return a filtered list containing only notifications that were succcessful
	 */
	public static List<PushedNotification> findSuccessfulNotifications(List<PushedNotification> notifications) {
		List<PushedNotification> filteredList = new Vector<PushedNotification>();
		for (PushedNotification notification : notifications) {
			if (notification.isSuccessful()) filteredList.add(notification);
		}
		return filteredList;
	}


	/**
	 * Filters a list of pushed notifications and returns only the ones that failed.
	 * 
	 * @param notifications a list of pushed notifications
	 * @return a filtered list containing only notifications that were <b>not</b> successful
	 */
	public static List<PushedNotification> findFailedNotifications(List<PushedNotification> notifications) {
		List<PushedNotification> filteredList = new Vector<PushedNotification>();
		for (PushedNotification notification : notifications) {
			if (!notification.isSuccessful()) {
				filteredList.add(notification);
			}
		}
		return filteredList;
	}


	/**
	 * Returns a human-friendly description of this pushed notification.
	 */
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("[" + identifier + "]");
		msg.append(transmissionCompleted ? " transmitted " + payload + " on " + getLatestTransmissionAttempt() : " not transmitted");
		msg.append(" to token " + device.getToken().substring(0, 5) + ".." + device.getToken().substring(59, 64));
		if (response != null) {
			msg.append("  " + response.getMessage());
		}
		if (exception != null) {
			msg.append("  " + exception);
		}
		return msg.toString();
	}


	void setException(Exception exception) {
		this.exception = exception;
	}


	/**
	 * Get the exception that occurred while trying to push this notification, if any.
	 * @return an exception (if any was thrown)
	 */
	public Exception getException() {
		return exception;
	}

}
