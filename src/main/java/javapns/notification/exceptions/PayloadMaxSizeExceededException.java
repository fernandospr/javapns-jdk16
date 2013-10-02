package javapns.notification.exceptions;

/**
 * Thrown when a payload exceeds the maximum size allowed.
 * @author Sylvain Pedneault
 *
 */
@SuppressWarnings("serial")
public class PayloadMaxSizeExceededException extends Exception {

	/**
	 * Default constructor
	 */
	public PayloadMaxSizeExceededException() {
		super("Total payload size exceeds allowed limit");
	}


	public PayloadMaxSizeExceededException(int maxSize) {
		super(String.format("Total payload size exceeds allowed limit (%s bytes max)", maxSize));
	}


	public PayloadMaxSizeExceededException(int maxSize, int currentSize) {
		super(String.format("Total payload size exceeds allowed limit (payload is %s bytes, limit is %s)", currentSize, maxSize));
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public PayloadMaxSizeExceededException(String message) {
		super(message);
	}

}
