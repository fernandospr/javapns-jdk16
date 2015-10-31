package javapns.notification;

import org.json.JSONException;

public class PushNotificationBigPayload extends PushNotificationPayload {

    /* Maximum total length (serialized) of a payload */
    private static final int MAXIMUM_PAYLOAD_LENGTH = 2048;

    /**
     * Return the maximum payload size in bytes.
     * For APNS payloads, since iOS8, this method returns 2048.
     *
     * @return the maximum payload size in bytes (2048)
     */
    @Override
    public int getMaximumPayloadSize() {
        return MAXIMUM_PAYLOAD_LENGTH;
    }

    public static PushNotificationBigPayload complex() {
        return new PushNotificationBigPayload();
    }

    public static PushNotificationBigPayload fromJSON(String rawJSON) throws JSONException {
        return new PushNotificationBigPayload(rawJSON);
    }

    public PushNotificationBigPayload() {
        super();
    }

    public PushNotificationBigPayload(String rawJSON) throws JSONException {
        super(rawJSON);
    }
}
