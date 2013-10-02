package javapns.notification.management;

import org.json.*;

/**
 * An MDM payload for LDAP.
 * 
 * @author Sylvain Pedneault
 */
public class LDAPPayload extends MobileConfigPayload {

	public LDAPPayload(int payloadVersion, String payloadOrganization, String payloadIdentifier, String payloadDisplayName, String ldapAccountHostName, boolean ldapAccountUseSSL) throws JSONException {
		super(payloadVersion, "com.apple.webClip.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
		JSONObject payload = getPayload();
		payload.put("LDAPAccountHostName", ldapAccountHostName);
		payload.put("LDAPAccountUseSSL", ldapAccountUseSSL);
	}


	public void setLDAPAccountDescription(boolean value) throws JSONException {
		getPayload().put("LDAPAccountDescription", value);
	}


	public void setLDAPAccountUserName(boolean value) throws JSONException {
		getPayload().put("LDAPAccountUserName", value);
	}


	public void setLDAPAccountPassword(boolean value) throws JSONException {
		getPayload().put("LDAPAccountPassword", value);
	}


	public JSONObject addSearchSettings(String ldapSearchSettingSearchBase, String ldapSearchSettingScope) throws JSONException {
		return addSearchSettings(ldapSearchSettingSearchBase, ldapSearchSettingScope, null);
	}


	public JSONObject addSearchSettings(String ldapSearchSettingSearchBase, int ldapSearchSettingScope) throws JSONException {
		return addSearchSettings(ldapSearchSettingSearchBase, ldapSearchSettingScope, null);
	}


	public JSONObject addSearchSettings(String ldapSearchSettingSearchBase, int ldapSearchSettingScope, String ldapSearchSettingDescription) throws JSONException {
		return addSearchSettings(ldapSearchSettingSearchBase, ldapSearchSettingScope == 0 ? "LDAPSearchSettingScopeBase" : ldapSearchSettingScope == 1 ? "LDAPSearchSettingScopeBase" : "LDAPSearchSettingScopeSubtree", ldapSearchSettingDescription);
	}


	public JSONObject addSearchSettings(String ldapSearchSettingSearchBase, String ldapSearchSettingScope, String ldapSearchSettingDescription) throws JSONException {
		JSONObject payload = getPayload();
		JSONObject searchSettings = new JSONObject();
		payload.put("LDAPSearchSettings", searchSettings);
		searchSettings.put("LDAPSearchSettingSearchBase", ldapSearchSettingSearchBase);
		searchSettings.put("LDAPSearchSettingScope", ldapSearchSettingScope);
		if (ldapSearchSettingDescription != null) searchSettings.put("LDAPSearchSettingDescription", ldapSearchSettingDescription);
		return searchSettings;
	}

}
