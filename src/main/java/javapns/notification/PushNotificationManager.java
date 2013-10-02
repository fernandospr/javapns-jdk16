package javapns.notification;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.security.cert.Certificate;
import java.text.*;
import java.util.*;

import javapns.communication.*;
import javapns.communication.exceptions.*;
import javapns.devices.*;
import javapns.devices.exceptions.*;
import javapns.devices.implementations.basic.*;
import javapns.notification.exceptions.*;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import org.apache.log4j.*;

/**
 * The main class used to send notification and handle a connection to Apple SSLServerSocket.
 * This class is not multi-threaded.  One instance per thread must be created.
 *
 * @author Maxime Pilon
 * @author Sylvain Pedneault
 * @author Others...
 */
public class PushNotificationManager {

	private static int TESTS_SERIAL_NUMBER = 1;

	/*
	 * Number of milliseconds to use as socket timeout.
	 * Set to -1 to leave the timeout to its default setting.
	 */
	private int sslSocketTimeout = 30 * 1000;

	static final Logger logger = Logger.getLogger(PushNotificationManager.class);

	/* Default retries for a connection */
	private static final int DEFAULT_RETRIES = 3;

	/* Special identifier that tells the manager to generate a sequential identifier for each payload pushed */
	private static final int SEQUENTIAL_IDENTIFIER = -1;

	private static boolean useEnhancedNotificationFormat = true;

	private static boolean heavyDebugMode = false;

	/* Connection helper */
	private ConnectionToAppleServer connectionToAppleServer;

	/* The always connected SSLSocket */
	private SSLSocket socket;

	/* Default retry attempts */
	private int retryAttempts = DEFAULT_RETRIES;

	private int nextMessageIdentifier = 1;

	/*
	 * To circumvent an issue with invalid server certificates,
	 * set to true to use a trust manager that will always accept
	 * server certificates, regardless of their validity.
	 */
	private boolean trustAllServerCertificates = true;

	/* The DeviceFactory to use with this PushNotificationManager */
	@Deprecated
	private DeviceFactory deviceFactory;

	private LinkedHashMap<Integer, PushedNotification> pushedNotifications = new LinkedHashMap<Integer, PushedNotification>();


	/**
	 * Constructs a PushNotificationManager
	 */
	@SuppressWarnings("deprecation")
	public PushNotificationManager() {
		deviceFactory = new BasicDeviceFactory();
	}


	/**
	 * Constructs a PushNotificationManager using a supplied DeviceFactory
	 * @param deviceManager
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public PushNotificationManager(DeviceFactory deviceManager) {
		this.deviceFactory = deviceManager;
	}


	/**
	 * Initialize a connection and create a SSLSocket
	 * @param server The Apple server to connect to.
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public void initializeConnection(AppleNotificationServer server) throws CommunicationException, KeystoreException {
		try {
			this.connectionToAppleServer = new ConnectionToNotificationServer(server);
			this.socket = connectionToAppleServer.getSSLSocket();

			if (heavyDebugMode) {
				dumpCertificateChainDescription();
			}
			logger.debug("Initialized Connection to Host: [" + server.getNotificationServerHost() + "] Port: [" + server.getNotificationServerPort() + "]: " + socket);
		} catch (KeystoreException e) {
			throw e;
		} catch (CommunicationException e) {
			throw e;
		} catch (Exception e) {
			throw new CommunicationException("Error creating connection with Apple server", e);
		}
	}


	private void dumpCertificateChainDescription() {
		try {
			File file = new File("apns-certificatechain.txt");
			FileOutputStream outf = new FileOutputStream(file);
			DataOutputStream outd = new DataOutputStream(outf);
			outd.writeBytes(getCertificateChainDescription());
			outd.close();
		} catch (Exception e) {
		}
	}


	private String getCertificateChainDescription() {
		StringBuilder buf = new StringBuilder();
		try {
			SSLSession session = socket.getSession();

			for (Certificate certificate : session.getLocalCertificates())
				buf.append(certificate.toString());

			buf.append("\n--------------------------------------------------------------------------\n");

			for (X509Certificate certificate : session.getPeerCertificateChain())
				buf.append(certificate.toString());

		} catch (Exception e) {
			buf.append(e);
		}
		return buf.toString();
	}


	/**
	 * Initialize a connection using server settings from the previous connection.
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public void initializePreviousConnection() throws CommunicationException, KeystoreException {
		initializeConnection((AppleNotificationServer) this.connectionToAppleServer.getServer());
	}


	/**
	 * Stop and restart the current connection to the Apple server
	 * @param server the server to start
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public void restartConnection(AppleNotificationServer server) throws CommunicationException, KeystoreException {
		stopConnection();
		initializeConnection(server);
	}


	/**
	 * Stop and restart the current connection to the Apple server using server settings from the previous connection.
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	private void restartPreviousConnection() throws CommunicationException, KeystoreException {
		try {
			logger.debug("Closing connection to restart previous one");
			this.socket.close();
		} catch (Exception e) {
			/* Do not complain if connection is already closed... */
		}
		initializePreviousConnection();
	}


	/**
	 * Read and process any pending error-responses, and then close the connection.
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public void stopConnection() throws CommunicationException, KeystoreException {
		processedFailedNotifications();
		try {
			logger.debug("Closing connection");
			this.socket.close();
		} catch (Exception e) {
			/* Do not complain if connection is already closed... */
		}
	}


	/**
	 * Read and process any pending error-responses.
	 * 
	 * If an error-response packet is received for a particular message, this
	 * method assumes that messages following the one identified in the packet
	 * were completely ignored by Apple, and as such automatically retries to 
	 * send all messages after the problematic one.
	 * 
	 * @return the number of error-response packets received
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	private int processedFailedNotifications() throws CommunicationException, KeystoreException {
		if (useEnhancedNotificationFormat) {
			logger.debug("Reading responses");
			int responsesReceived = ResponsePacketReader.processResponses(this);
			while (responsesReceived > 0) {
				PushedNotification skippedNotification = null;
				List<PushedNotification> notificationsToResend = new ArrayList<PushedNotification>();
				boolean foundFirstFail = false;
				for (PushedNotification notification : pushedNotifications.values()) {
					if (foundFirstFail || !notification.isSuccessful()) {
						if (foundFirstFail) notificationsToResend.add(notification);
						else {
							foundFirstFail = true;
							skippedNotification = notification;
						}
					}
				}
				pushedNotifications.clear();
				int toResend = notificationsToResend.size();
				logger.debug("Found " + toResend + " notifications that must be re-sent");
				if (toResend > 0) {
					logger.debug("Restarting connection to resend notifications");
					restartPreviousConnection();
					for (PushedNotification pushedNotification : notificationsToResend) {
						sendNotification(pushedNotification, false);
					}
				}
				int remaining = responsesReceived = ResponsePacketReader.processResponses(this);
				if (remaining == 0) {
					logger.debug("No notifications remaining to be resent");
					return 0;
				}
			}
			return responsesReceived;
		} else {
			logger.debug("Not reading responses because using simple notification format");
			return 0;
		}
	}


	/**
	 * Send a notification to a single device and close the connection.
	 * 
	 * @param device the device to be notified
	 * @param payload the payload to send
	 * @return a pushed notification with details on transmission result and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 */
	public PushedNotification sendNotification(Device device, Payload payload) throws CommunicationException {
		return sendNotification(device, payload, true);
	}


	/**
	 * Send a notification to a multiple devices in a single connection and close the connection.
	 * 
	 * @param payload the payload to send
	 * @param devices the device to be notified
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public PushedNotifications sendNotifications(Payload payload, List<Device> devices) throws CommunicationException, KeystoreException {
		PushedNotifications notifications = new PushedNotifications();
		for (Device device : devices)
			notifications.add(sendNotification(device, payload, false, SEQUENTIAL_IDENTIFIER));
		stopConnection();
		return notifications;
	}


	/**
	 * Send a notification to a multiple devices in a single connection and close the connection.
	 * 
	 * @param payload the payload to send
	 * @param devices the device to be notified
	 * @return a list of pushed notifications, each with details on transmission results and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 * @throws KeystoreException thrown if there is a problem with your keystore
	 */
	public PushedNotifications sendNotifications(Payload payload, Device... devices) throws CommunicationException, KeystoreException {
		PushedNotifications notifications = new PushedNotifications();
		for (Device device : devices)
			notifications.add(sendNotification(device, payload, false, SEQUENTIAL_IDENTIFIER));
		stopConnection();
		return notifications;
	}


	/**
	 * Send a notification (Payload) to the given device
	 * 
	 * @param device the device to be notified
	 * @param payload the payload to send
	 * @param closeAfter indicates if the connection should be closed after the payload has been sent
	 * @return a pushed notification with details on transmission result and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 */
	public PushedNotification sendNotification(Device device, Payload payload, boolean closeAfter) throws CommunicationException {
		return sendNotification(device, payload, closeAfter, SEQUENTIAL_IDENTIFIER);
	}


	/**
	 * Send a notification (Payload) to the given device
	 * 
	 * @param device the device to be notified
	 * @param payload the payload to send
	 * @param identifier a unique identifier which will match any error reported later (if any)
	 * @return a pushed notification with details on transmission result and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 */
	public PushedNotification sendNotification(Device device, Payload payload, int identifier) throws CommunicationException {
		return sendNotification(device, payload, false, identifier);
	}


	/**
	 * Send a notification (Payload) to the given device
	 * 
	 * @param device the device to be notified
	 * @param payload the payload to send
	 * @param closeAfter indicates if the connection should be closed after the payload has been sent
	 * @param identifier a unique identifier which will match any error reported later (if any)
	 * @return a pushed notification with details on transmission result and error (if any)
	 * @throws CommunicationException thrown if a communication error occurs
	 */
	public PushedNotification sendNotification(Device device, Payload payload, boolean closeAfter, int identifier) throws CommunicationException {
		PushedNotification pushedNotification = new PushedNotification(device, payload, identifier);
		sendNotification(pushedNotification, closeAfter);
		return pushedNotification;
	}


	/**
	 * Actual action of sending a notification
	 * 
	 * @param notification the ready-to-push notification
	 * @param closeAfter indicates if the connection should be closed after the payload has been sent
	 * @throws CommunicationException thrown if a communication error occurs
	 */
	private void sendNotification(PushedNotification notification, boolean closeAfter) throws CommunicationException {
		try {
			Device device = notification.getDevice();
			Payload payload = notification.getPayload();
			try {
				payload.verifyPayloadIsNotEmpty();
			} catch (IllegalArgumentException e) {
				throw new PayloadIsEmptyException();
			} catch (Exception e) {
			}

			if (notification.getIdentifier() <= 0) notification.setIdentifier(newMessageIdentifier());
			if (!pushedNotifications.containsKey(notification.getIdentifier())) pushedNotifications.put(notification.getIdentifier(), notification);
			int identifier = notification.getIdentifier();

			String token = device.getToken();
			// even though the BasicDevice constructor validates the token, we revalidate it in case we were passed another implementation of Device
			BasicDevice.validateTokenFormat(token);
			//		PushedNotification pushedNotification = new PushedNotification(device, payload);
			byte[] bytes = getMessage(token, payload, identifier, notification);
			//		pushedNotifications.put(pushedNotification.getIdentifier(), pushedNotification);

			/* Special simulation mode to skip actual streaming of message */
			boolean simulationMode = payload.getExpiry() == 919191;

			boolean success = false;

			BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			int socketTimeout = getSslSocketTimeout();
			if (socketTimeout > 0) this.socket.setSoTimeout(socketTimeout);
			notification.setTransmissionAttempts(0);
			// Keep trying until we have a success
			while (!success) {
				try {
					logger.debug("Attempting to send notification: " + payload.toString() + "");
					logger.debug("  to device: " + token + "");
					notification.addTransmissionAttempt();
					boolean streamConfirmed = false;
					try {
						if (!simulationMode) {
							this.socket.getOutputStream().write(bytes);
							streamConfirmed = true;
						} else {
							logger.debug("* Simulation only: would have streamed " + bytes.length + "-bytes message now..");
						}
					} catch (Exception e) {
						if (e != null) {
							if (e.toString().contains("certificate_unknown")) {
								throw new InvalidCertificateChainException(e.getMessage());
							}
						}
						throw e;
					}
					logger.debug("Flushing");
					this.socket.getOutputStream().flush();
					if (streamConfirmed) logger.debug("At this point, the entire " + bytes.length + "-bytes message has been streamed out successfully through the SSL connection");

					success = true;
					logger.debug("Notification sent on " + notification.getLatestTransmissionAttempt());
					notification.setTransmissionCompleted(true);

				} catch (IOException e) {
					// throw exception if we surpassed the valid number of retry attempts
					if (notification.getTransmissionAttempts() >= retryAttempts) {
						logger.error("Attempt to send Notification failed and beyond the maximum number of attempts permitted");
						notification.setTransmissionCompleted(false);
						notification.setException(e);
						logger.error("Delivery error", e);
						throw e;

					} else {
						logger.info("Attempt failed (" + e.getMessage() + ")... trying again");
						//Try again
						try {
							this.socket.close();
						} catch (Exception e2) {
							// do nothing
						}
						this.socket = connectionToAppleServer.getSSLSocket();
						if (socketTimeout > 0) this.socket.setSoTimeout(socketTimeout);
					}
				}
			}
		} catch (CommunicationException e) {
			throw e;
		} catch (Exception ex) {

			notification.setException(ex);
			logger.error("Delivery error: " + ex);
			try {
				if (closeAfter) {
					logger.error("Closing connection after error");
					stopConnection();
				}
			} catch (Exception e) {
			}
		}
	}


	/**
	 * Add a device
	 * @param id The device id
	 * @param token The device token
	 * @throws DuplicateDeviceException
	 * @throws NullDeviceTokenException 
	 * @throws NullIdException 
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public void addDevice(String id, String token) throws DuplicateDeviceException, NullIdException, NullDeviceTokenException, Exception {
		logger.debug("Adding Token [" + token + "] to Device [" + id + "]");
		deviceFactory.addDevice(id, token);
	}


	/**
	 * Get a device according to his id
	 * @param id The device id
	 * @return The device
	 * @throws UnknownDeviceException
	 * @throws NullIdException 
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public Device getDevice(String id) throws UnknownDeviceException, NullIdException {
		logger.debug("Getting Token from Device [" + id + "]");
		return deviceFactory.getDevice(id);
	}


	/**
	 * Remove a device
	 * @param id The device id
	 * @throws UnknownDeviceException
	 * @throws NullIdException
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public void removeDevice(String id) throws UnknownDeviceException, NullIdException {
		logger.debug("Removing Token from Device [" + id + "]");
		deviceFactory.removeDevice(id);
	}


	//	/**
	//	 * Set the proxy if needed
	//	 * @param host the proxyHost
	//	 * @param port the proxyPort
	//	 * @deprecated Configuring a proxy with this method affects overall JVM proxy settings.
	//	 * Use AppleNotificationServer.setProxy(..) to set a proxy for JavaPNS only.
	//	 */
	//	public void setProxy(String host, String port) {
	//		proxySet = true;
	//
	//		System.setProperty("http.proxyHost", host);
	//		System.setProperty("http.proxyPort", port);
	//
	//		System.setProperty("https.proxyHost", host);
	//		System.setProperty("https.proxyPort", port);
	//	}

	/**
	 * Compose the Raw Interface that will be sent through the SSLSocket
	 * A notification message is
	 * COMMAND | TOKENLENGTH | DEVICETOKEN | PAYLOADLENGTH | PAYLOAD
	 * or enhanced notification format:
	 * COMMAND | !Identifier! | !Expiry! | TOKENLENGTH| DEVICETOKEN | PAYLOADLENGTH | PAYLOAD
	 * See page 30 of Apple Push Notification Service Programming Guide
	 * @param deviceToken the deviceToken
	 * @param payload the payload
	 * @param message 
	 * @return the byteArray to write to the SSLSocket OutputStream
	 * @throws IOException
	 */
	private byte[] getMessage(String deviceToken, Payload payload, int identifier, PushedNotification message) throws IOException, Exception {
		logger.debug("Building Raw message from deviceToken and payload");

		/* To test with a corrupted or invalid token, uncomment following line*/
		//deviceToken = deviceToken.substring(0,10);

		// First convert the deviceToken (in hexa form) to a binary format
		byte[] deviceTokenAsBytes = new byte[deviceToken.length() / 2];
		deviceToken = deviceToken.toUpperCase();
		int j = 0;
		try {
			for (int i = 0; i < deviceToken.length(); i += 2) {
				String t = deviceToken.substring(i, i + 2);
				int tmp = Integer.parseInt(t, 16);
				deviceTokenAsBytes[j++] = (byte) tmp;
			}
		} catch (NumberFormatException e1) {
			throw new InvalidDeviceTokenFormatException(deviceToken, e1.getMessage());
		}
		preconfigurePayload(payload, identifier, deviceToken);
		// Create the ByteArrayOutputStream which will contain the raw interface
		byte[] payloadAsBytes = payload.getPayloadAsBytes();
		int size = (Byte.SIZE / Byte.SIZE) + (Character.SIZE / Byte.SIZE) + deviceTokenAsBytes.length + (Character.SIZE / Byte.SIZE) + payloadAsBytes.length;
		ByteArrayOutputStream bao = new ByteArrayOutputStream(size);

		// Write command to ByteArrayOutputStream
		// 0 = simple
		// 1 = enhanced
		if (useEnhancedNotificationFormat) {
			byte b = 1;
			bao.write(b);
		} else {
			byte b = 0;
			bao.write(b);
		}

		if (useEnhancedNotificationFormat) {
			// 4 bytes identifier (which will match any error packet received later on)
			bao.write(intTo4ByteArray(identifier));
			message.setIdentifier(identifier);

			// 4 bytes
			int requestedExpiry = payload.getExpiry();
			if (requestedExpiry <= 0) {
				bao.write(intTo4ByteArray(requestedExpiry));
				message.setExpiry(0);
			} else {
				long ctime = System.currentTimeMillis();
				long ttl = requestedExpiry * 1000; // time-to-live in milliseconds
				Long expiryDateInSeconds = ((ctime + ttl) / 1000L);
				bao.write(intTo4ByteArray(expiryDateInSeconds.intValue()));
				message.setExpiry(ctime + ttl);
			}
		}
		// Write the TokenLength as a 16bits unsigned int, in big endian
		int tl = deviceTokenAsBytes.length;
		bao.write(intTo2ByteArray(tl));

		// Write the Token in bytes
		bao.write(deviceTokenAsBytes);

		// Write the PayloadLength as a 16bits unsigned int, in big endian
		int pl = payloadAsBytes.length;
		bao.write(intTo2ByteArray(pl));

		// Finally write the Payload
		bao.write(payloadAsBytes);
		bao.flush();

		byte[] bytes = bao.toByteArray();

		if (heavyDebugMode) {
			try {
				FileOutputStream outf = new FileOutputStream("apns-message.bytes");
				outf.write(bytes);
				outf.close();
			} catch (Exception e) {
			}
		}

		logger.debug("Built raw message ID " + identifier + " of total length " + bytes.length);
		return bytes;
	}


	/**
	 * Get the number of retry attempts
	 * @return int
	 */
	public int getRetryAttempts() {
		return this.retryAttempts;
	}


	private static final byte[] intTo4ByteArray(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}


	private static final byte[] intTo2ByteArray(int value) {
		int s1 = (value & 0xFF00) >> 8;
		int s2 = value & 0xFF;
		return new byte[] { (byte) s1, (byte) s2 };
	}


	/**
	 * Set the number of retry attempts
	 * @param retryAttempts
	 */
	public void setRetryAttempts(int retryAttempts) {
		this.retryAttempts = retryAttempts;
	}


	/**
	 * Sets the DeviceFactory used by this PushNotificationManager.
	 * Usually useful for dependency injection.
	 * @param deviceFactory an object implementing DeviceFactory
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public void setDeviceFactory(DeviceFactory deviceFactory) {
		this.deviceFactory = deviceFactory;
	}


	/**
	 * Returns the DeviceFactory used by this PushNotificationManager.
	 * @return the DeviceFactory in use
	 * @deprecated The DeviceFactory-based architecture is deprecated. 
	 */
	@Deprecated
	public DeviceFactory getDeviceFactory() {
		return deviceFactory;
	}


	/**
	 * Set the SSL socket timeout to use.
	 * @param sslSocketTimeout
	 */
	public void setSslSocketTimeout(int sslSocketTimeout) {
		this.sslSocketTimeout = sslSocketTimeout;
	}


	/**
	 * Get the SSL socket timeout currently in use.
	 * @return the current SSL socket timeout value.
	 */
	public int getSslSocketTimeout() {
		return sslSocketTimeout;
	}


	/**
	 * Set whether or not to enable the "trust all server certificates" feature to simplify SSL communications.
	 * @param trustAllServerCertificates
	 */
	public void setTrustAllServerCertificates(boolean trustAllServerCertificates) {
		this.trustAllServerCertificates = trustAllServerCertificates;
	}


	/**
	 * Get the status of the "trust all server certificates" feature to simplify SSL communications.
	 * @return the status of the "trust all server certificates" feature
	 */
	protected boolean isTrustAllServerCertificates() {
		return trustAllServerCertificates;
	}


	/**
	 * Return a new sequential message identifier.
	 * @return a message identifier unique to this PushNotificationManager
	 */
	private int newMessageIdentifier() {
		int id = nextMessageIdentifier;
		nextMessageIdentifier++;
		return id;
	}


	Socket getActiveSocket() {
		return socket;
	}


	/**
	 * Get the internal list of pushed notifications.
	 * 
	 * @return
	 */
	Map<Integer, PushedNotification> getPushedNotifications() {
		return pushedNotifications;
	}


	/**
	 * Enable or disable the enhanced notification format (enabled by default).
	 * @param enabled true to enable, false to disable
	 */
	public static void setEnhancedNotificationFormatEnabled(boolean enabled) {
		useEnhancedNotificationFormat = enabled;
	}


	/**
	 * Check if the enhanced notification format is currently enabled.
	 * @return the status of the enhanced notification format
	 */
	protected static boolean isEnhancedNotificationFormatEnabled() {
		return useEnhancedNotificationFormat;
	}


	/**
	 * Enable or disable a special heavy debug mode which causes verbose details to be written to local files.
	 * The last raw APSN message will be written to a "apns-message.bytes" file in the working directory.
	 * A detailed description of local and peer SSL certificates will be written to a "apns-certificatechain.txt" file in the working directory.
	 * @param enabled true to enable, false to disable
	 */
	public static void setHeavyDebugMode(boolean enabled) {
		heavyDebugMode = enabled;
	}


	private void preconfigurePayload(Payload payload, int identifier, String deviceToken) {
		try {
			int config = payload.getPreSendConfiguration();
			if (payload instanceof PushNotificationPayload) {
				PushNotificationPayload pnpayload = (PushNotificationPayload) payload;
				if (config == 1) {
					pnpayload.getPayload().remove("alert");
					pnpayload.addAlert(buildDebugAlert(payload, identifier, deviceToken));
				}
			}
		} catch (Exception e) {
		}
	}


	private String buildDebugAlert(Payload payload, int identifier, String deviceToken) {
		StringBuilder alert = new StringBuilder();
		alert.append("JAVAPNS DEBUG ALERT " + (TESTS_SERIAL_NUMBER++) + "\n");

		/* Current date & time */
		alert.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(System.currentTimeMillis()) + "\n");

		/* Selected Apple server */
		alert.append(this.connectionToAppleServer.getServerHost() + "\n");

		/* Device token (shortened), Identifier and expiry */
		int l = useEnhancedNotificationFormat ? 4 : 8;
		alert.append("" + deviceToken.substring(0, l) + "É" + deviceToken.substring(64 - l, 64) + (useEnhancedNotificationFormat ? " [Id:" + identifier + "] " + (payload.getExpiry() <= 0 ? "No-store" : "Exp:T+" + payload.getExpiry()) : "") + "\n");

		/* Format & encoding */
		alert.append((useEnhancedNotificationFormat ? "Enhanced" : "Simple") + " format / " + payload.getCharacterEncoding() + "" + "");

		return alert.toString();
	}
}
