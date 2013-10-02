package javapns.communication.exceptions;

public class CommunicationException extends Exception {

	private static final long serialVersionUID = 1L;


	public CommunicationException(String message, Exception cause) {
		super(message, cause);
	}

}
