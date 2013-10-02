package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for PasswordPolicy.
 * 
 * @author Sylvain Pedneault
 */
public class PasswordPolicyPayload extends MobileConfigPayload {

	public PasswordPolicyPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName) throws JSONException {
		super(payloadVersion, "com.apple.mobiledevice.passwordpolicy", payloadOrganization, payloadIdentifier, payloadDisplayName);
	}


	public void setAllowSimple(boolean value) throws JSONException {
		getPayload().put("allowSimple", value);
	}


	public void setForcePIN(boolean value) throws JSONException {
		getPayload().put("forcePIN", value);
	}


	public void setMaxFailedAttempts(int value) throws JSONException {
		getPayload().put("maxFailedAttempts", value);
	}


	public void setMaxInactivity(int value) throws JSONException {
		getPayload().put("maxInactivity", value);
	}


	public void setMaxPINAgeInDays(int value) throws JSONException {
		getPayload().put("maxPINAgeInDays", value);
	}


	public void setMinComplexChars(int value) throws JSONException {
		getPayload().put("minComplexChars", value);
	}


	public void setMinLength(int value) throws JSONException {
		getPayload().put("minLength", value);
	}


	public void setRequireAlphanumeric(boolean value) throws JSONException {
		getPayload().put("requireAlphanumeric", value);
	}


	public void setPinHistory(int value) throws JSONException {
		getPayload().put("pinHistory", value);
	}


	public void setManualFetchingWhenRoaming(boolean value) throws JSONException {
		getPayload().put("manualFetchingWhenRoaming", value);
	}


	public void setMaxGracePeriod(int value) throws JSONException {
		getPayload().put("maxGracePeriod", value);
	}

}
