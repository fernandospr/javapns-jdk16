package javapns.devices.exceptions;

/**
 * Thrown when the given token is null
 * @author Maxime Peron
 *
 */
@SuppressWarnings("serial")
public class NullDeviceTokenException extends Exception{

	/* Custom message for this exception */
	private String message;
	
	/**
	 * Constructor
	 */
	public NullDeviceTokenException(){
		this.message = "Client already exists";
	}
	
	/**
	 * Constructor with custom message
	 * @param message
	 */
	public NullDeviceTokenException(String message){
		this.message = message;
	}
	
	/**
	 * String representation
	 */
	public String toString(){
		return this.message;
	}
}
