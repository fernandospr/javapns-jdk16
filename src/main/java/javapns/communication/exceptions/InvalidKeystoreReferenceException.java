package javapns.communication.exceptions;

/**
 * Thrown when we try to contact Apple with an invalid keystore format.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class InvalidKeystoreReferenceException extends KeystoreException {

	/**
	 * Constructor
	 */
	public InvalidKeystoreReferenceException() {
		super("Invalid keystore parameter.  Must be InputStream, File, String (as a file path), or byte[].");
	}


	/**
	 * Constructor with custom message
	 * @param keystore
	 */
	public InvalidKeystoreReferenceException(Object keystore) {
		super("Invalid keystore parameter (" + keystore + ").  Must be InputStream, File, String (as a file path), or byte[].");
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public InvalidKeystoreReferenceException(String message) {
		super(message);
	}
}
