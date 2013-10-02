package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for Wi-Fi.
 * 
 * @author Sylvain Pedneault
 */
public class WiFiPayload extends MobileConfigPayload {

	public WiFiPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String SSID_STR, boolean hiddenNetwork, String encryptionType) throws JSONException {
		super(payloadVersion, "com.apple.wifi.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("SSID_STR", SSID_STR);
		payload.put("HIDDEN_NETWORK", hiddenNetwork);
		payload.put("EncryptionType", encryptionType);
	}


	public void setPassword(String value) throws JSONException {
		getPayload().put("Password", value);
	}


	public JSONObject addEAPClientConfiguration() throws JSONException {
		JSONObject object = new JSONObject();
		getPayload().put("EAPClientConfiguration", object);
		return object;
	}

}
