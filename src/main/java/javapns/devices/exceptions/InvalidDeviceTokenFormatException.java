package javapns.devices.exceptions;

/**
 * Thrown when a device token cannot be parsed (invalid format).
 * 
 * @author Sylvain Pedneault
 */
@SuppressWarnings("serial")
public class InvalidDeviceTokenFormatException extends Exception {

	public InvalidDeviceTokenFormatException(String message) {
		super(message);
	}


	public InvalidDeviceTokenFormatException(String token, String problem) {
		super(String.format("Device token cannot be parsed, most likely because it contains invalid hexadecimal characters: %s in %s", problem, token));
	}

}
