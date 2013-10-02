package javapns;

import java.util.*;

import javapns.communication.exceptions.*;
import javapns.devices.*;
import javapns.devices.exceptions.*;
import javapns.devices.implementations.basic.*;
import javapns.feedback.*;
import javapns.notification.*;
import javapns.notification.transmission.*;

/**
 * <p>Main class for easily interacting with the Apple Push Notification System</p>
 * 
 * <p>This is the best starting point for pushing simple or custom notifications,
 * or for contacting the Feedback Service to cleanup your list of devices.</p>
 * 
 * <p>The <b>JavaPNS</b> library also includes more advanced options such as
 * multithreaded transmission, special payloads, and more.
 * See the library's documentation at <a href="http://code.google.com/p/javapns/">http://code.google.com/p/javapns/</a>
 * for more information.</p>
 * 
 * @author Sylvain Pedneault
 * @see NotificationThreads
 */
public class Push {

	private Push() {

	}


	/**
	 * Push a simple alert to one or more devices.
	 * 
	 * @param message the alert message to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications alert(String message, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(PushNotificationPayload.alert(message), keystore, password, production, devices);
	}


	/**
	 * Push a simple badge number to one or more devices.
	 * 
	 * @param badge the badge number to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications badge(int badge, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(PushNotificationPayload.badge(badge), keystore, password, production, devices);
	}


	/**
	 * Push a simple sound name to one or more devices.
	 * 
	 * @param sound the sound name (stored in the client app) to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications sound(String sound, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(PushNotificationPayload.sound(sound), keystore, password, production, devices);
	}


	/**
	 * Push a notification combining an alert, a badge and a sound. 
	 * 
	 * @param message the alert message to push (set to null to skip).
	 * @param badge the badge number to push (set to -1 to skip).
	 * @param sound the sound name to push (set to null to skip).
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications combined(String message, int badge, String sound, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(PushNotificationPayload.combined(message, badge, sound), keystore, password, production, devices);
	}


	/**
	 * Push a content-available notification for Newsstand.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications contentAvailable(Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(NewsstandNotificationPayload.contentAvailable(), keystore, password, production, devices);
	}


	/**
	 * Push a special test notification with an alert message containing useful debugging information.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications test(Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(PushNotificationPayload.test(), keystore, password, production, devices);
	}


	/**
	 * Push a preformatted payload to a list of devices.
	 * 
	 * @param payload a simple or complex payload to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications payload(Payload payload, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		return sendPayload(payload, keystore, password, production, devices);
	}


	/**
	 * Push a preformatted payload to a list of devices.
	 * 
	 * @param payload a simple or complex payload to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	private static PushedNotifications sendPayload(Payload payload, Object keystore, String password, boolean production, Object devices) throws CommunicationException, KeystoreException {
		PushedNotifications notifications = new PushedNotifications();
		if (payload == null) return notifications;
		PushNotificationManager pushManager = new PushNotificationManager();
		try {
			AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
			pushManager.initializeConnection(server);
			List<Device> deviceList = Devices.asDevices(devices);
			notifications.setMaxRetained(deviceList.size());
			for (Device device : deviceList) {
				try {
					BasicDevice.validateTokenFormat(device.getToken());
					PushedNotification notification = pushManager.sendNotification(device, payload, false);
					notifications.add(notification);
				} catch (InvalidDeviceTokenFormatException e) {
					notifications.add(new PushedNotification(device, payload, e));
				}
			}
		} finally {
			try {
				pushManager.stopConnection();
			} catch (Exception e) {
			}
		}
		return notifications;
	}


	/**
	 * Push a preformatted payload to a list of devices using multiple simulatenous threads (and connections).
	 * 
	 * @param payload a simple or complex payload to push.
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param numberOfThreads the number of parallel threads to use to push the notifications
	 * @param devices a list or an array of tokens or devices: {@link java.lang.String String[]}, {@link java.util.List}<{@link java.lang.String}>, {@link javapns.devices.Device Device[]}, {@link java.util.List}<{@link javapns.devices.Device}>, {@link java.lang.String} or {@link javapns.devices.Device}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws Exception thrown if any critical exception occurs
	 */
	public static PushedNotifications payload(Payload payload, Object keystore, String password, boolean production, int numberOfThreads, Object devices) throws Exception {
		if (numberOfThreads <= 0) return sendPayload(payload, keystore, password, production, devices);
		AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
		List<Device> deviceList = Devices.asDevices(devices);
		NotificationThreads threads = new NotificationThreads(server, payload, deviceList, numberOfThreads);
		threads.start();
		try {
			threads.waitForAllThreads(true);
		} catch (InterruptedException e) {
		}
		return threads.getPushedNotifications();
	}


	/**
	 * Build and start an asynchronous queue for sending notifications later without opening and closing connections.
	 * The returned queue is not started, meaning that underlying threads and connections are not initialized.
	 * The queue will start if you invoke its start() method or one of the add() methods.
	 * Once the queue is started, its underlying thread(s) and connection(s) will remain active until the program ends.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param numberOfThreads the number of parallel threads to use to push the notifications
	 * @return a live queue to which you can add notifications to be sent asynchronously
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 */
	public static PushQueue queue(Object keystore, String password, boolean production, int numberOfThreads) throws KeystoreException {
		AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
		PushQueue queue = numberOfThreads <= 1 ? new NotificationThread(server) : new NotificationThreads(server, numberOfThreads);
		return queue;
	}


	/**
	 * Push a different preformatted payload for each device.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param payloadDevicePairs a list or an array of PayloadPerDevice: {@link java.util.List}<{@link javapns.notification.PayloadPerDevice}>, {@link javapns.notification.PayloadPerDevice PayloadPerDevice[]} or {@link javapns.notification.PayloadPerDevice}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static PushedNotifications payloads(Object keystore, String password, boolean production, Object payloadDevicePairs) throws CommunicationException, KeystoreException {
		return sendPayloads(keystore, password, production, payloadDevicePairs);
	}


	/**
	 * Push a different preformatted payload for each device using multiple simulatenous threads (and connections).
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param numberOfThreads the number of parallel threads to use to push the notifications
	 * @param payloadDevicePairs a list or an array of PayloadPerDevice: {@link java.util.List}<{@link javapns.notification.PayloadPerDevice}>, {@link javapns.notification.PayloadPerDevice PayloadPerDevice[]} or {@link javapns.notification.PayloadPerDevice}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws Exception thrown if any critical exception occurs
	 */
	public static PushedNotifications payloads(Object keystore, String password, boolean production, int numberOfThreads, Object payloadDevicePairs) throws Exception {
		if (numberOfThreads <= 0) return sendPayloads(keystore, password, production, payloadDevicePairs);
		AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
		List<PayloadPerDevice> payloadPerDevicePairs = Devices.asPayloadsPerDevices(payloadDevicePairs);
		NotificationThreads threads = new NotificationThreads(server, payloadPerDevicePairs, numberOfThreads);
		threads.start();
		try {
			threads.waitForAllThreads(true);
		} catch (InterruptedException e) {
		}
		return threads.getPushedNotifications();
	}


	/**
	 * Push a different preformatted payload for each device.
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @param payloadDevicePairs a list or an array of PayloadPerDevice: {@link java.util.List}<{@link javapns.notification.PayloadPerDevice}>, {@link javapns.notification.PayloadPerDevice PayloadPerDevice[]} or {@link javapns.notification.PayloadPerDevice}
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	private static PushedNotifications sendPayloads(Object keystore, String password, boolean production, Object payloadDevicePairs) throws CommunicationException, KeystoreException {
		PushedNotifications notifications = new PushedNotifications();
		if (payloadDevicePairs == null) return notifications;
		PushNotificationManager pushManager = new PushNotificationManager();
		try {
			AppleNotificationServer server = new AppleNotificationServerBasicImpl(keystore, password, production);
			pushManager.initializeConnection(server);
			List<PayloadPerDevice> pairs = Devices.asPayloadsPerDevices(payloadDevicePairs);
			notifications.setMaxRetained(pairs.size());
			for (PayloadPerDevice ppd : pairs) {
				Device device = ppd.getDevice();
				Payload payload = ppd.getPayload();
				try {
					PushedNotification notification = pushManager.sendNotification(device, payload, false);
					notifications.add(notification);
				} catch (Exception e) {
					notifications.add(new PushedNotification(device, payload, e));
				}
			}
		} finally {
			try {
				pushManager.stopConnection();
			} catch (Exception e) {
			}
		}
		return notifications;
	}


	/**
	 * <p>Retrieve a list of devices that should be removed from future notification lists.</p>
	 * 
	 * <p>Devices in this list are ones that you previously tried to push a notification to,
	 * but to which Apple could not actually deliver because the device user has either
	 * opted out of notifications, has uninstalled your application, or some other conditions.</p>
	 * 
	 * <p>Important: Apple's Feedback Service always resets its list of inactive devices
	 * after each time you contact it.  Calling this method twice will not return the same
	 * list of devices!</p>
	 * 
	 * <p>Please be aware that Apple does not specify precisely when a device will be listed
	 * by the Feedback Service.  More specifically, it is unlikely that the device will
	 * be  listed immediately if you uninstall the application during testing.  It might
	 * get listed after some number of notifications couldn't reach it, or some amount of
	 * time has elapsed, or a combination of both.</p>
	 * 
	 * <p>Further more, if you are using Apple's sandbox servers, the Feedback Service will
	 * probably not list your device if you uninstalled your app and it was the last one
	 * on your device that was configured to receive notifications from the sandbox.
	 * See the library's wiki for more information.</p>
	 * 
	 * @param keystore a keystore containing your private key and the certificate signed by Apple ({@link java.io.File}, {@link java.io.InputStream}, byte[], {@link java.security.KeyStore} or {@link java.lang.String} for a file path)
	 * @param password the keystore's password.
	 * @param production true to use Apple's production servers, false to use the sandbox servers.
	 * @return a list of devices that are inactive.
	 * @throws KeystoreException thrown if an error occurs when loading the keystore
	 * @throws CommunicationException thrown if an unrecoverable error occurs while trying to communicate with Apple servers
	 */
	public static List<Device> feedback(Object keystore, String password, boolean production) throws CommunicationException, KeystoreException {
		List<Device> devices = new Vector<Device>();
		FeedbackServiceManager feedbackManager = new FeedbackServiceManager();
		AppleFeedbackServer server = new AppleFeedbackServerBasicImpl(keystore, password, production);
		devices.addAll(feedbackManager.getDevices(server));
		return devices;
	}

}
