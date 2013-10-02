package javapns.notification.management;

import java.util.*;

import org.json.*;

/**
 * An MDM payload for APN (Access Point Name).
 * 
 * @author Sylvain Pedneault
 */
public class APNPayload extends MobileConfigPayload {

	public APNPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, Map<String, String> defaultsData, String defaultsDomainName, Map<String, String>[] apns, String apn, String username) throws JSONException {
		super(payloadVersion, "com.apple.apn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("DefaultsData", defaultsData);
		payload.put("defaultsDomainName", defaultsDomainName);
		for (Map<String, String> apnsEntry : apns)
			payload.put("apns", apnsEntry);
		payload.put("apn", apn);
		payload.put("username", username);
	}


	public void setPassword(APNPayload value) throws JSONException {
		getPayload().put("password", value);
	}


	public void setProxy(String value) throws JSONException {
		getPayload().put("proxy", value);
	}


	public void setProxyPort(int value) throws JSONException {
		getPayload().put("proxyPort", value);
	}

}
