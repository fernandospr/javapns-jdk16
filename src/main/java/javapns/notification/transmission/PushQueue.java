package javapns.notification.transmission;

import java.util.*;

import javapns.devices.*;
import javapns.devices.exceptions.*;
import javapns.notification.*;

/**
 * A queue backed by an asynchronous notification thread or threads.
 * 
 * @author Sylvain Pedneault
 */
public interface PushQueue {

	/**
	 * Queue a message for delivery.  A thread will pick it up and push it asynchroneously.
	 * This method has no effect if the underlying notification thread is not in QUEUE mode.
	 * @param payload a payload
	 * @param token a device token
	 * @return the actual queue to which the message was added, which could be a different one if the request was delegated to a sub-queue
	 * @throws InvalidDeviceTokenFormatException 
	 */
	public PushQueue add(Payload payload, String token) throws InvalidDeviceTokenFormatException;


	/**
	 * Queue a message for delivery.  A thread will pick it up and push it asynchroneously.
	 * This method has no effect if the underlying notification thread is not in QUEUE mode.
	 * @param payload a payload
	 * @param device a device
	 * @return the actual queue to which the message was added, which could be a different one if the request was delegated to a sub-queue
	 */
	public PushQueue add(Payload payload, Device device);


	/**
	 * Queue a message for delivery.  A thread will pick it up and push it asynchroneously.
	 * This method has no effect if the underlying notification thread is not in QUEUE mode.
	 * @param message a payload/device pair
	 * @return the actual queue to which the message was added, which could be a different one if the request was delegated to a sub-queue
	 */
	public PushQueue add(PayloadPerDevice message);


	/**
	 * Start the transmission thread(s) working for the queue.
	 * @return the queue itself, as a handy shortcut to create and start a queue in a single line of code
	 */
	public PushQueue start();


	/**
	 * Get a list of critical exceptions that underlying threads experienced.
	 * Critical exceptions include CommunicationException and KeystoreException.
	 * Exceptions related to tokens, payloads and such are *not* included here,
	 * as they are noted in individual PushedNotification objects.
	 * If critical exceptions are present, the underlying thread(s) is most
	 * likely not working at all and you should solve the problem before
	 * trying to go any further.
	 * 
	 * @return a list of critical exceptions
	 */

	public List<Exception> getCriticalExceptions();


	/**
	 * Get a list of all notifications pushed through this queue.
	 * 
	 * @return a list of pushed notifications
	 */
	public PushedNotifications getPushedNotifications();


	/**
	 * Clear the internal lists of PushedNotification objects maintained by this queue.
	 * You should invoke this method once you no longer need the list of PushedNotification objects so that memory can be reclaimed.
	 */
	public void clearPushedNotifications();

}
