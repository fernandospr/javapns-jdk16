package javapns.communication.exceptions;

/**
 * Thrown when we try to contact Apple with an invalid keystore format.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class KeystoreException extends Exception {

	/**
	 * Constructor with custom message
	 * @param message
	 */
	public KeystoreException(String message) {
		super(message);
	}

	/**
	 * Constructor with custom message
	 * @param message
	 */
	public KeystoreException(String message, Exception cause) {
		super(message, cause);
	}

}
