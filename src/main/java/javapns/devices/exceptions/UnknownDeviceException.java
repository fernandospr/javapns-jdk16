package javapns.devices.exceptions;

/**
 * Thrown when we try to retrieve a device that doesn't exist
 * @author Maxime Peron
 *
 */
@SuppressWarnings("serial")
public class UnknownDeviceException extends Exception{
	
	/* Custom message for this exception */
	private String message;
	
	/**
	 * Constructor
	 */
	public UnknownDeviceException(){
		this.message = "Unknown client";
	}
	
	/**
	 * Constructor with custom message
	 * @param message
	 */
	public UnknownDeviceException(String message){
		this.message = message;
	}
	
	/**
	 * String representation
	 */
	public String toString(){
		return this.message;
	}
}
