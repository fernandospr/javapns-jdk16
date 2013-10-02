package javapns.devices.exceptions;

/**
 * Thrown when a Device already exist and we try to add it a second time
 * @author Maxime Peron
 */
@SuppressWarnings("serial")
public class DuplicateDeviceException extends Exception{
	
	/* Custom message for this exception */
	private String message;
	
	/**
	 * Constructor
	 */
	public DuplicateDeviceException(){
		this.message = "Client already exists";
	}
	
	/**
	 * Constructor with custom message
	 * @param message
	 */
	public DuplicateDeviceException(String message){
		this.message = message;
	}
	
	/**
	 * String representation
	 */
	public String toString(){
		return this.message;
	}
}
