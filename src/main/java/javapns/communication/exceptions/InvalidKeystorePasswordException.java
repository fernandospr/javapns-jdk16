package javapns.communication.exceptions;

/**
 * Thrown when we try to contact Apple with an invalid password for the keystore.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class InvalidKeystorePasswordException extends KeystoreException {

	/**
	 * Constructor
	 */
	public InvalidKeystorePasswordException() {
		super("Invalid keystore password!  Verify settings for connecting to Apple...");
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public InvalidKeystorePasswordException(String message) {
		super(message);
	}

}
