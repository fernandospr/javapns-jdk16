package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for Email.
 * 
 * @author Sylvain Pedneault
 */
public class EmailPayload extends MobileConfigPayload {

	public EmailPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String emailAccountType, String emailAddress, String incomingMailServerAuthentication, String incomingMailServerHostName, String incomingMailServerUsername, String outgoingMailServerAuthentication, String outgoingMailServerHostName, String outgoingMailServerUsername) throws JSONException {
		super(payloadVersion, "com.apple.mail.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("EmailAccountType", emailAccountType);
		payload.put("EmailAddress", emailAddress);
		payload.put("IncomingMailServerAuthentication", incomingMailServerAuthentication);
		payload.put("IncomingMailServerHostName", incomingMailServerHostName);
		payload.put("IncomingMailServerUsername", incomingMailServerUsername);
		payload.put("OutgoingMailServerAuthentication", outgoingMailServerAuthentication);
		payload.put("OutgoingMailServerHostName", outgoingMailServerHostName);
		payload.put("OutgoingMailServerUsername", outgoingMailServerUsername);
	}


	public void setEmailAccountDescription(String value) throws JSONException {
		getPayload().put("EmailAccountDescription", value);
	}


	public void setEmailAccountName(String value) throws JSONException {
		getPayload().put("EmailAccountName", value);
	}


	public void setIncomingMailServerPortNumber(int value) throws JSONException {
		getPayload().put("IncomingMailServerPortNumber", value);
	}


	public void setIncomingMailServerUseSSL(boolean value) throws JSONException {
		getPayload().put("IncomingMailServerUseSSL", value);
	}


	public void setIncomingPassword(String value) throws JSONException {
		getPayload().put("IncomingPassword", value);
	}


	public void setOutgoingPassword(String value) throws JSONException {
		getPayload().put("OutgoingPassword", value);
	}


	public void setOutgoingPasswwordSameAsIncomingPassword(boolean value) throws JSONException {
		getPayload().put("OutgoingPasswwordSameAsIncomingPassword", value);
	}


	public void setOutgoingMailServerPortNumber(int value) throws JSONException {
		getPayload().put("OutgoingMailServerPortNumber", value);
	}


	public void setOutgoingMailServerUseSSL(boolean value) throws JSONException {
		getPayload().put("OutgoingMailServerUseSSL", value);
	}

}
