package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for CalendarSubscription.
 * 
 * @author Sylvain Pedneault
 */
public class CalendarSubscriptionPayload extends MobileConfigPayload {

	public CalendarSubscriptionPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String subCalAccountHostName, boolean subCalAccountUseSSL) throws JSONException {
		super(payloadVersion, "com.apple.caldav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("SubCalAccountHostName", subCalAccountHostName);
		payload.put("SubCalAccountUseSSL", subCalAccountUseSSL);
	}


	public void setSubCalAccountDescription(String value) throws JSONException {
		getPayload().put("SubCalAccountDescription", value);
	}


	public void setSubCalAccountUsername(String value) throws JSONException {
		getPayload().put("SubCalAccountUsername", value);
	}


	public void setSubCalAccountPassword(String value) throws JSONException {
		getPayload().put("SubCalAccountPassword", value);
	}


	public void setSubCalAccountUseSSL(boolean value) throws JSONException {
		getPayload().put("SubCalAccountUseSSL", value);
	}

}
