package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for RemovalPassword.
 * 
 * @author Sylvain Pedneault
 */
public class RemovalPasswordPayload extends MobileConfigPayload {

	public RemovalPasswordPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName) throws JSONException {
		super(payloadVersion, "com.apple.profileRemovalPassword", payloadOrganization, payloadIdentifier, payloadDisplayName);
	}


	public void setRemovalPasword(String value) throws JSONException {
		getPayload().put("RemovalPassword", value);
	}

}
