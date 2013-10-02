package javapns.feedback;

import java.security.*;

import javapns.communication.*;
import javapns.communication.exceptions.*;
import javapns.notification.*;

/**
 * Class representing a connection to a specific Feedback Server.
 * 
 * @author Sylvain Pedneault
 */
public class ConnectionToFeedbackServer extends ConnectionToAppleServer {

	public ConnectionToFeedbackServer(AppleFeedbackServer feedbackServer) throws KeystoreException {
		super(feedbackServer);
	}


	public ConnectionToFeedbackServer(AppleNotificationServer server, KeyStore keystore) throws KeystoreException {
		super(server, keystore);
	}


	@Override
	public String getServerHost() {
		return ((AppleFeedbackServer) getServer()).getFeedbackServerHost();
	}


	@Override
	public int getServerPort() {
		return ((AppleFeedbackServer) getServer()).getFeedbackServerPort();
	}

}
