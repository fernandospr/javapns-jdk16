package javapns.devices.exceptions;

/**
 * Thrown when the given id is null
 * @author Maxime Peron
 */
@SuppressWarnings("serial")
public class NullIdException extends Exception{

	/* Custom message for this exception */
	private String message;
	
	/**
	 * Constructor
	 */
	public NullIdException(){
		this.message = "Client already exists";
	}
	
	/**
	 * Constructor with custom message
	 * @param message
	 */
	public NullIdException(String message){
		this.message = message;
	}
	
	/**
	 * String representation
	 */
	public String toString(){
		return this.message;
	}
}
