package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for WebClip.
 * 
 * @author Sylvain Pedneault
 */
public class WebClipPayload extends MobileConfigPayload {

	public WebClipPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String url, String label) throws JSONException {
		super(payloadVersion, "com.apple.webClip.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("URL", url);
		payload.put("Label", label);
	}


	public void setIcon(Object data) throws JSONException {
		getPayload().put("Icon", data);
	}


	public void setIsRemovable(boolean value) throws JSONException {
		getPayload().put("IsRemovable", value);
	}

}
