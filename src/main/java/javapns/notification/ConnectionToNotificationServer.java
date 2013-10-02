package javapns.notification;

import java.security.*;

import javapns.communication.*;
import javapns.communication.exceptions.*;

/**
 * Connection details specific to the Notification Service.
 * 
 * @author Sylvain Pedneault
 */
public class ConnectionToNotificationServer extends ConnectionToAppleServer {

	public ConnectionToNotificationServer(AppleNotificationServer server) throws KeystoreException {
		super(server);
	}


	public ConnectionToNotificationServer(AppleNotificationServer server, KeyStore keystore) throws KeystoreException {
		super(server, keystore);
	}


	@Override
	public String getServerHost() {
		return ((AppleNotificationServer) getServer()).getNotificationServerHost();
	}


	@Override
	public int getServerPort() {
		return ((AppleNotificationServer) getServer()).getNotificationServerPort();
	}

}
