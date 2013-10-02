package javapns.communication.exceptions;

/**
 * Thrown when we try to contact Apple with an invalid keystore format.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class InvalidKeystoreFormatException extends KeystoreException {

	/**
	 * Constructor
	 */
	public InvalidKeystoreFormatException() {
		super("Invalid keystore format!  Make sure it is PKCS12...");
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public InvalidKeystoreFormatException(String message) {
		super(message);
	}

}
