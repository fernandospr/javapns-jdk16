package javapns.notification.exceptions;

import org.json.*;

/**
 * Thrown when a payload is expected to exceed the maximum size allowed after adding a given property.
 * Invoke payload.setPayloadSizeEstimatedWhenAdding(false) to disable this automatic checking.
 * 
 * @author Sylvain Pedneault
 */
@SuppressWarnings("serial")
public class PayloadMaxSizeProbablyExceededException extends JSONException {

	/**
	 * Default constructor
	 */
	public PayloadMaxSizeProbablyExceededException() {
		super("Total payload size will most likely exceed allowed limit");
	}


	public PayloadMaxSizeProbablyExceededException(int maxSize) {
		super(String.format("Total payload size will most likely exceed allowed limit (%s bytes max)", maxSize));
	}


	public PayloadMaxSizeProbablyExceededException(int maxSize, int estimatedSize) {
		super(String.format("Total payload size will most likely exceed allowed limit (estimated to become %s bytes, limit is %s)", estimatedSize, maxSize));
	}


	/**
	 * Constructor with custom message
	 * @param message
	 */
	public PayloadMaxSizeProbablyExceededException(String message) {
		super(message);
	}

}
