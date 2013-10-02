package javapns.notification.exceptions;

/**
 * Thrown when a payload is empty.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class PayloadIsEmptyException extends Exception {

	public PayloadIsEmptyException() {
		super("Payload is empty");
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public PayloadIsEmptyException(String message) {
		super(message);
	}

}
