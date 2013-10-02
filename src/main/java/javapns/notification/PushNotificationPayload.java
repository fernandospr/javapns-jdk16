package javapns.notification;

import java.util.*;

import javapns.notification.exceptions.*;

import org.json.*;

/**
 * A payload compatible with the Apple Push Notification Service.
 * 
 * @author Maxime Peron
 * @author Sylvain Pedneault
 */
public class PushNotificationPayload extends Payload {

	/* Maximum total length (serialized) of a payload */
	private static final int MAXIMUM_PAYLOAD_LENGTH = 256;


	/**
	 * Create a pre-defined payload with a simple alert message.
	 * 
	 * @param message the alert's message
	 * @return a ready-to-send payload
	 */
	public static PushNotificationPayload alert(String message) {
		if (message == null) throw new IllegalArgumentException("Alert cannot be null");
		PushNotificationPayload payload = complex();
		try {
			payload.addAlert(message);
		} catch (JSONException e) {
		}
		return payload;
	}


	/**
	 * Create a pre-defined payload with a badge.
	 * 
	 * @param badge the badge
	 * @return a ready-to-send payload
	 */
	public static PushNotificationPayload badge(int badge) {
		PushNotificationPayload payload = complex();
		try {
			payload.addBadge(badge);
		} catch (JSONException e) {
		}
		return payload;
	}


	/**
	 * Create a pre-defined payload with a sound name.
	 * 
	 * @param sound the name of the sound
	 * @return a ready-to-send payload
	 */
	public static PushNotificationPayload sound(String sound) {
		if (sound == null) throw new IllegalArgumentException("Sound name cannot be null");
		PushNotificationPayload payload = complex();
		try {
			payload.addSound(sound);
		} catch (JSONException e) {
		}
		return payload;
	}


	/**
	 * Create a pre-defined payload with a simple alert message, a badge and a sound.
	 * 
	 * @param message the alert message
	 * @param badge the badge
	 * @param sound the name of the sound
	 * @return a ready-to-send payload
	 */
	public static PushNotificationPayload combined(String message, int badge, String sound) {
		if (message == null && badge < 0 && sound == null) throw new IllegalArgumentException("Must provide at least one non-null argument");
		PushNotificationPayload payload = complex();
		try {
			if (message != null) payload.addAlert(message);
			if (badge >= 0) payload.addBadge(badge);
			if (sound != null) payload.addSound(sound);
		} catch (JSONException e) {
		}
		return payload;
	}


	/**
	 * Create a special payload with a useful debugging alert message.
	 * 
	 * @return a ready-to-send payload
	 */
	public static PushNotificationPayload test() {
		PushNotificationPayload payload = complex();
		payload.setPreSendConfiguration(1);
		return payload;
	}


	/**
	 * Create an empty payload which you can configure later.
	 * This method is usually used to create complex or custom payloads.
	 * Note: the payload actually contains the default "aps"
	 * dictionary required by APNS.
	 * 
	 * @return a blank payload that can be customized
	 */
	public static PushNotificationPayload complex() {
		PushNotificationPayload payload = new PushNotificationPayload();
		return payload;
	}


	/**
	 * Create a PushNotificationPayload object from a preformatted JSON payload.
	 * @param rawJSON a JSON-formatted string representing a payload (ex: {"aps":{"alert":"Hello World!"}} )
	 * @return a ready-to-send payload
	 * @throws JSONException if any exception occurs parsing the JSON string
	 */
	public static PushNotificationPayload fromJSON(String rawJSON) throws JSONException {
		PushNotificationPayload payload = new PushNotificationPayload(rawJSON);
		return payload;
	}

	/* The application Dictionnary */
	private JSONObject apsDictionary;


	/**
	 * Create a default payload with a blank "aps" dictionary.
	 */
	public PushNotificationPayload() {
		super();
		this.apsDictionary = new JSONObject();
		try {
			JSONObject payload = getPayload();
			if (!payload.has("aps")) payload.put("aps", this.apsDictionary);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Construct a Payload object from a JSON-formatted string.
	 * If an aps dictionary is not included, one will be created automatically.
	 * @param rawJSON a JSON-formatted string (ex: {"aps":{"alert":"Hello World!"}} )
	 * @throws JSONException thrown if a exception occurs while parsing the JSON string
	 */
	public PushNotificationPayload(String rawJSON) throws JSONException {
		super(rawJSON);
		try {
			JSONObject payload = getPayload();
			this.apsDictionary = payload.getJSONObject("aps");
			if (this.apsDictionary == null) {
				this.apsDictionary = new JSONObject();
				payload.put("aps", this.apsDictionary);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create a payload and immediately add an alert message, a badge and a sound.
	 * 
	 * @param alert the alert message
	 * @param badge the badge
	 * @param sound the name of the sound
	 * @throws JSONException
	 */
	public PushNotificationPayload(String alert, int badge, String sound) throws JSONException {
		this();
		if (alert != null) addAlert(alert);
		addBadge(badge);
		if (sound != null) addSound(sound);
	}


	/**
	 * Add a badge.
	 * 
	 * @param badge a badge number
	 * @throws JSONException
	 */
	public void addBadge(int badge) throws JSONException {
		logger.debug("Adding badge [" + badge + "]");
		put("badge", badge, this.apsDictionary, true);
	}


	/**
	 * Add a sound.
	 * 
	 * @param sound the name of a sound
	 * @throws JSONException
	 */
	public void addSound(String sound) throws JSONException {
		logger.debug("Adding sound [" + sound + "]");
		put("sound", sound, this.apsDictionary, true);
	}


	/**
	 * Add a simple alert message.
	 * Note: you cannot add a simple and a custom alert in the same payload.
	 * 
	 * @param alertMessage the alert's message
	 * @throws JSONException
	 */
	public void addAlert(String alertMessage) throws JSONException {
		String previousAlert = getCompatibleProperty("alert", String.class, "A custom alert (\"%s\") was already added to this payload");
		logger.debug("Adding alert [" + alertMessage + "]" + (previousAlert != null ? " replacing previous alert [" + previousAlert + "]" : ""));
		put("alert", alertMessage, this.apsDictionary, false);
	}


	/**
	 * Get the custom alert object, creating it if it does not yet exist.
	 * 
	 * @return the JSON object defining the custom alert
	 * @throws JSONException if a simple alert has already been added to this payload
	 */
	private JSONObject getOrAddCustomAlert() throws JSONException {
		JSONObject alert = getCompatibleProperty("alert", JSONObject.class, "A simple alert (\"%s\") was already added to this payload");
		if (alert == null) {
			alert = new JSONObject();
			put("alert", alert, this.apsDictionary, false);
		}
		return alert;
	}


	/**
	 * Get the value of a given property, but only if it is of the expected class.
	 * If the value exists but is of a different class than expected, an
	 * exception is thrown.
	 * 
	 * This method simply invokes the other getCompatibleProperty method with the root aps dictionary.
	 * 
	 * 
	 * @param <T> the property value's class
	 * @param propertyName the name of the property to get
	 * @param expectedClass the property value's expected (required) class
	 * @param exceptionMessage the exception message to throw if the value is not of the expected class
	 * @return the property's value
	 * @throws JSONException
	 */
	private <T> T getCompatibleProperty(String propertyName, Class<T> expectedClass, String exceptionMessage) throws JSONException {
		return getCompatibleProperty(propertyName, expectedClass, exceptionMessage, this.apsDictionary);
	}


	/**
	 * Get the value of a given property, but only if it is of the expected class.
	 * If the value exists but is of a different class than expected, an
	 * exception is thrown.
	 * 
	 * This method is useful for properly supporting properties that can have a simple
	 * or complex value (such as "alert")
	 * 
	 * @param <T> the property value's class
	 * @param propertyName the name of the property to get
	 * @param expectedClass the property value's expected (required) class
	 * @param exceptionMessage the exception message to throw if the value is not of the expected class
	 * @param dictionary the dictionary where to get the property from
	 * @return the property's value
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private <T> T getCompatibleProperty(String propertyName, Class<T> expectedClass, String exceptionMessage, JSONObject dictionary) throws JSONException {
		Object propertyValue = null;
		try {
			propertyValue = dictionary.get(propertyName);
		} catch (Exception e) {
		}
		if (propertyValue == null) return null;
		if (propertyValue.getClass().equals(expectedClass)) return (T) propertyValue;
		try {
			exceptionMessage = String.format(exceptionMessage, propertyValue);
		} catch (Exception e) {
		}
		throw new PayloadAlertAlreadyExistsException(exceptionMessage);

	}


	/**
	 * Create a custom alert (if none exist) and add a body to the custom alert.
	 * 
	 * @param body the body of the alert
	 * @throws JSONException if the custom alert cannot be added because a simple alert already exists
	 */
	public void addCustomAlertBody(String body) throws JSONException {
		put("body", body, getOrAddCustomAlert(), false);
	}


	/**
	 * Create a custom alert (if none exist) and add a custom text for the right button of the popup.
	 * 
	 * @param actionLocKey the title of the alert's right button, or null to remove the button
	 * @throws JSONException if the custom alert cannot be added because a simple alert already exists
	 */
	public void addCustomAlertActionLocKey(String actionLocKey) throws JSONException {
		Object value = actionLocKey != null ? actionLocKey : new JSONNull();
		put("action-loc-key", value, getOrAddCustomAlert(), false);
	}


	/**
	 * Create a custom alert (if none exist) and add a loc-key parameter.
	 * 
	 * @param locKey
	 * @throws JSONException if the custom alert cannot be added because a simple alert already exists
	 */
	public void addCustomAlertLocKey(String locKey) throws JSONException {
		put("loc-key", locKey, getOrAddCustomAlert(), false);
	}


	/**
	 * Create a custom alert (if none exist) and add sub-parameters for the loc-key parameter.
	 * 
	 * @param args
	 * @throws JSONException if the custom alert cannot be added because a simple alert already exists
	 */
	public void addCustomAlertLocArgs(List args) throws JSONException {
		put("loc-args", args, getOrAddCustomAlert(), false);
	}


	/**
	 * Return the maximum payload size in bytes.
	 * For APNS payloads, this method returns 256.
	 * 
	 * @return the maximum payload size in bytes (256)
	 */
	@Override
	public int getMaximumPayloadSize() {
		return MAXIMUM_PAYLOAD_LENGTH;
	}


	void verifyPayloadIsNotEmpty() {
		if (getPreSendConfiguration() != 0) return;
		if (toString().equals("{\"aps\":{}}")) throw new IllegalArgumentException("Payload cannot be empty");
	}
}
