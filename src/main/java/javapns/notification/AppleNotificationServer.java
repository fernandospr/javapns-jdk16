package javapns.notification;

import javapns.communication.*;

/**
 * Interface representing a connection to an Apple Notification Server
 * 
 * @author Sylvain Pedneault
 */
public interface AppleNotificationServer extends AppleServer {

	public static final String	PRODUCTION_HOST = "gateway.push.apple.com";
	public static final int 	PRODUCTION_PORT = 2195;
	
	public static final String	DEVELOPMENT_HOST = "gateway.sandbox.push.apple.com";
	public static final int 	DEVELOPMENT_PORT = 2195;
	

	public String getNotificationServerHost();


	public int getNotificationServerPort();

}
