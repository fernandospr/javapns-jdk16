package javapns.notification;

import org.json.*;

/**
 * A Newsstand-specific payload compatible with the Apple Push Notification Service.
 * 
 * @author Sylvain Pedneault
 */
public class NewsstandNotificationPayload extends Payload {

	/**
	 * Create a pre-defined payload with a content-available property set to 1.
	 * 
	 * @return a ready-to-send newsstand payload
	 */
	public static NewsstandNotificationPayload contentAvailable() {
		NewsstandNotificationPayload payload = complex();
		try {
			payload.addContentAvailable();
		} catch (JSONException e) {
		}
		return payload;
	}


	/**
	 * Create an empty payload which you can configure later (most users should not use this).
	 * This method is usually used to create complex or custom payloads.
	 * Note: the payload actually contains the default "aps"
	 * dictionary required by Newsstand.
	 * 
	 * @return a blank payload that can be customized
	 */
	private static NewsstandNotificationPayload complex() {
		NewsstandNotificationPayload payload = new NewsstandNotificationPayload();
		return payload;
	}

	/* The application Dictionnary */
	private JSONObject apsDictionary;


	/**
	 * Create a default payload with a blank "aps" dictionary.
	 */
	NewsstandNotificationPayload() {
		super();
		this.apsDictionary = new JSONObject();
		try {
			JSONObject payload = getPayload();
			payload.put("aps", this.apsDictionary);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	void addContentAvailable() throws JSONException {
		addContentAvailable(1);
	}


	void addContentAvailable(int contentAvailable) throws JSONException {
		logger.debug("Adding ContentAvailable [" + contentAvailable + "]");
		this.apsDictionary.put("content-available", contentAvailable);
	}

}
