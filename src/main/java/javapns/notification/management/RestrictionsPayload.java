package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for Restrictions.
 * 
 * @author Sylvain Pedneault
 */
public class RestrictionsPayload extends MobileConfigPayload {

	public RestrictionsPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName) throws JSONException {
		super(payloadVersion, "com.apple.applicationaccess", payloadOrganization, payloadIdentifier, payloadDisplayName);
	}


	public void setAllowAppInstallation(boolean value) throws JSONException {
		getPayload().put("allowAppInstallation", value);
	}


	public void setAllowCamera(boolean value) throws JSONException {
		getPayload().put("allowCamera", value);
	}


	public void setAllowExplicitContent(boolean value) throws JSONException {
		getPayload().put("allowExplicitContent", value);
	}


	public void setAllowScreenShot(boolean value) throws JSONException {
		getPayload().put("allowScreenShot", value);
	}


	public void setAllowYouTube(boolean value) throws JSONException {
		getPayload().put("allowYouTube", value);
	}


	public void setAllowiTunes(boolean value) throws JSONException {
		getPayload().put("allowAppInstallation", value);
	}


	public void setAllowSafari(boolean value) throws JSONException {
		getPayload().put("allowSafari", value);
	}

}
