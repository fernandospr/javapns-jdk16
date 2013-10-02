package javapns.notification.management;

import java.util.*;

import org.json.*;

/**
 * An MDM payload for SCEP (Simple Certificate Enrollment Protocol).
 * 
 * @author Sylvain Pedneault
 */
public class SCEPPayload extends MobileConfigPayload {

	public SCEPPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String url) throws JSONException {
		super(payloadVersion, "com.apple.encrypted-profile-service", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("URL", url);
	}


	public void setName(String value) throws JSONException {
		getPayload().put("Name", value);
	}


	public void setSubject(String value) throws JSONException {
		String[] parts = value.split("/");
		List<String[]> list = new ArrayList<String[]>();
		for (String part : parts) {
			String[] subparts = value.split("=");
			list.add(subparts);
		}
		String[][] subject = list.toArray(new String[0][0]);
		setSubject(subject);
	}


	public void setSubject(String[][] value) throws JSONException {
		getPayload().put("Subject", value);
	}


	public void setChallenge(String value) throws JSONException {
		getPayload().put("Challenge", value);
	}


	public void setKeysize(int value) throws JSONException {
		getPayload().put("Keysize", value);
	}


	public void setKeyType(String value) throws JSONException {
		getPayload().put("Key Type", value);
	}


	public void setKeyUsage(int value) throws JSONException {
		getPayload().put("Key Usage", value);
	}


	public JSONObject addSubjectAltName() throws JSONException {
		JSONObject object = new JSONObject();
		getPayload().put("SubjectAltName", object);
		return object;
	}


	public JSONObject addGetCACaps() throws JSONException {
		JSONObject object = new JSONObject();
		getPayload().put("GetCACaps", object);
		return object;
	}

}
