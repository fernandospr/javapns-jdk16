package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for CalDAV.
 * 
 * @author Sylvain Pedneault
 */
public class CalDAVPayload extends MobileConfigPayload {

	public CalDAVPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String calDAVHostName, String calDAVUsername, boolean calDAVUseSSL) throws JSONException {
		super(payloadVersion, "com.apple.caldav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("CalDAVHostName", calDAVHostName);
		payload.put("CalDAVUsername", calDAVUsername);
		payload.put("CalDAVUseSSL", calDAVUseSSL);
	}


	public void setCalDAVAccountDescription(String value) throws JSONException {
		getPayload().put("CalDAVAccountDescription", value);
	}


	public void setCalDAVPassword(String value) throws JSONException {
		getPayload().put("CalDAVPassword", value);
	}


	public void setCalDAVPort(int value) throws JSONException {
		getPayload().put("CalDAVPort", value);
	}


	public void setCalDAVPrincipalURL(String value) throws JSONException {
		getPayload().put("CalDAVPrincipalURL", value);
	}

}
