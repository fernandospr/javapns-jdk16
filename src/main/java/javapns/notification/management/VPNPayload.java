package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for VPN.
 * 
 * @author Sylvain Pedneault
 */
public class VPNPayload extends MobileConfigPayload {

	public static final String VPNTYPE_L2TP = "L2TP";
	public static final String VPNTYPE_PPTP = "PPTP";
	public static final String VPNTYPE_IPSec = "IPSec";


	public VPNPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String userDefinedName, boolean overridePrimary, String vpnType) throws JSONException {
		super(payloadVersion, "com.apple.vpn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("UserDefinedName", userDefinedName);
		payload.put("OverridePrimary", overridePrimary);
		payload.put("VPNType", vpnType);
	}


	public JSONObject addPPP() throws JSONException {
		JSONObject object = new JSONObject();
		getPayload().put("PPP", object);
		return object;
	}


	public JSONObject addIPSec() throws JSONException {
		JSONObject object = new JSONObject();
		getPayload().put("IPSec", object);
		return object;
	}

}
