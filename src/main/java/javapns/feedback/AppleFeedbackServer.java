package javapns.feedback;

import javapns.communication.*;

/**
 * Interface representing a connection to an Apple Feedback Server
 * 
 * @author Sylvain Pedneault
 */
public interface AppleFeedbackServer extends AppleServer {

	public static final String	PRODUCTION_HOST = "feedback.push.apple.com";
	public static final int 	PRODUCTION_PORT = 2196;
	
	public static final String	DEVELOPMENT_HOST = "feedback.sandbox.push.apple.com";
	public static final int 	DEVELOPMENT_PORT = 2196;
	

	public String getFeedbackServerHost();


	public int getFeedbackServerPort();

}
