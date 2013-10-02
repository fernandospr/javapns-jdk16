package javapns.notification.transmission;

import java.util.*;

import javapns.devices.*;
import javapns.devices.exceptions.*;
import javapns.notification.*;

/**
 * <h1>Pushes a payload to a large number of devices using multiple threads</h1>
 * 
 * <p>The list of devices is spread evenly into multiple {@link javapns.notification.transmission.NotificationThread}s.</p>
 * 
 * <p>Usage: once a NotificationThreads is created, invoke {@code start()} to start all {@link javapns.notification.transmission.NotificationThread} threads.</p>
 * <p>You can provide a {@link javapns.notification.transmission.NotificationProgressListener} to receive events about the work being done.</p>

 * @author Sylvain Pedneault
 * @see NotificationThread.MODE
 * @see NotificationThread
 */
public class NotificationThreads extends ThreadGroup implements PushQueue {

	private static final long DEFAULT_DELAY_BETWEEN_THREADS = 500; // the number of milliseconds to wait between each thread startup
	private List<NotificationThread> threads = new Vector<NotificationThread>();
	private NotificationProgressListener listener;
	private boolean started = false;
	private int threadsRunning = 0;
	private int nextThread = 0;
	private Object finishPoint = new Object();
	private long delayBetweenThreads = DEFAULT_DELAY_BETWEEN_THREADS;


	/**
	 * Create the specified number of notification threads and spread the devices evenly between the threads.
	 * 
	 * @param server the server to push to
	 * @param payload the payload to push
	 * @param devices a very large list of devices
	 * @param numberOfThreads the number of threads to create to share the work
	 */
	public NotificationThreads(AppleNotificationServer server, Payload payload, List<Device> devices, int numberOfThreads) {
		super("javapns notification threads (" + numberOfThreads + " threads)");
		for (List deviceGroup : makeGroups(devices, numberOfThreads))
			threads.add(new NotificationThread(this, new PushNotificationManager(), server, payload, deviceGroup));
	}


	/**
	 * Create the specified number of notification threads and spread the messages evenly between the threads.
	 * 
	 * @param server the server to push to
	 * @param messages a very large list of payload/device pairs
	 * @param numberOfThreads the number of threads to create to share the work
	 */
	public NotificationThreads(AppleNotificationServer server, List<PayloadPerDevice> messages, int numberOfThreads) {
		super("javapns notification threads (" + numberOfThreads + " threads)");
		for (List deviceGroup : makeGroups(messages, numberOfThreads))
			threads.add(new NotificationThread(this, new PushNotificationManager(), server, deviceGroup));
	}


	/**
	 * Create the specified number of notification threads and spread the devices evenly between the threads.
	 * Internally, this constructor uses a AppleNotificationServerBasicImpl to encapsulate the provided keystore, password and production parameters.
	 * 
	 * @param keystore the keystore to use (can be a File, an InputStream, a String for a file path, or a byte[] array)
	 * @param password the keystore's password
	 * @param production true to use Apple's production servers, false to use the sandbox
	 * @param payload the payload to push
	 * @param devices a very large list of devices
	 * @param numberOfThreads the number of threads to create to share the work
	 * @throws Exception 
	 */
	public NotificationThreads(Object keystore, String password, boolean production, Payload payload, List<Device> devices, int numberOfThreads) throws Exception {
		this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, devices, numberOfThreads);
	}


	/**
	 * Spread the devices evenly between the provided threads.
	 * 
	 * @param server the server to push to
	 * @param payload the payload to push
	 * @param devices a very large list of devices
	 * @param threads a list of pre-built threads
	 */
	@SuppressWarnings("unchecked")
	public NotificationThreads(AppleNotificationServer server, Payload payload, List<Device> devices, List<NotificationThread> threads) {
		super("javapns notification threads (" + threads.size() + " threads)");
		this.threads = threads;
		List<List> groups = makeGroups(devices, threads.size());
		for (int i = 0; i < groups.size(); i++)
			threads.get(i).setDevices(groups.get(i));
	}


	/**
	 * Spread the devices evenly between the provided threads.
	 * Internally, this constructor uses a AppleNotificationServerBasicImpl to encapsulate the provided keystore, password and production parameters.
	 * 
	 * @param keystore the keystore to use (can be a File, an InputStream, a String for a file path, or a byte[] array)
	 * @param password the keystore's password
	 * @param production true to use Apple's production servers, false to use the sandbox
	 * @param payload the payload to push
	 * @param devices a very large list of devices
	 * @param threads a list of pre-built threads
	 * @throws Exception 
	 */
	public NotificationThreads(Object keystore, String password, boolean production, Payload payload, List<Device> devices, List<NotificationThread> threads) throws Exception {
		this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, devices, threads);
	}


	/**
	 * Use the provided threads which should already each have their group of devices to work with.
	 * 
	 * @param server the server to push to
	 * @param payload the payload to push
	 * @param threads a list of pre-built threads
	 */
	public NotificationThreads(AppleNotificationServer server, Payload payload, List<NotificationThread> threads) {
		super("javapns notification threads (" + threads.size() + " threads)");
		this.threads = threads;
	}


	/**
	 * Use the provided threads which should already each have their group of devices to work with.
	 * Internally, this constructor uses a AppleNotificationServerBasicImpl to encapsulate the provided keystore, password and production parameters.
	 * 
	 * @param keystore the keystore to use (can be a File, an InputStream, a String for a file path, or a byte[] array)
	 * @param password the keystore's password
	 * @param production true to use Apple's production servers, false to use the sandbox
	 * @param payload the payload to push
	 * @param threads a list of pre-built threads
	 * @throws Exception 
	 */
	public NotificationThreads(Object keystore, String password, boolean production, Payload payload, List<NotificationThread> threads) throws Exception {
		this(new AppleNotificationServerBasicImpl(keystore, password, production), payload, threads);
	}


	/**
	 * Create a pool of notification threads in QUEUE mode.
	 * 
	 * @param server the server to push to
	 * @param numberOfThreads the number of threads to create in the pool
	 */
	public NotificationThreads(AppleNotificationServer server, int numberOfThreads) {
		super("javapns notification thread pool (" + numberOfThreads + " threads)");
		for (int i = 0; i < numberOfThreads; i++) {
			threads.add(new NotificationThread(this, new PushNotificationManager(), server));
		}
	}


	public PushQueue add(Payload payload, String token) throws InvalidDeviceTokenFormatException {
		return add(new PayloadPerDevice(payload, token));
	}


	public PushQueue add(Payload payload, Device device) {
		return add(new PayloadPerDevice(payload, device));
	}


	public PushQueue add(PayloadPerDevice message) {
		start(); // just in case start() was not invoked before
		NotificationThread targetThread = getNextAvailableThread();
		targetThread.add(message);
		return targetThread;
	}


	/**
	 * Get the next available thread.
	 * 
	 * @return a thread potentially available to work
	 */
	protected NotificationThread getNextAvailableThread() {
		for (int i = 0; i < threads.size(); i++) {
			NotificationThread thread = getNextThread();
			boolean busy = thread.isBusy();
			if (!busy) return thread;
		}
		return getNextThread(); /* All threads are busy, return the next one regardless of its busy status */
	}


	/**
	 * Get the next thread to use.
	 * 
	 * @return a thread
	 */
	protected synchronized NotificationThread getNextThread() {
		if (nextThread >= threads.size()) nextThread = 0;
		NotificationThread thread = threads.get(nextThread++);
		return thread;
	}


	/**
	 * Create groups of devices or payload/device pairs ready to be dispatched to worker threads.
	 * 
	 * @param devices a large list of devices
	 * @param threads the number of threads to group devices for
	 * @return
	 */
	private static List<List> makeGroups(List objects, int threads) {
		List<List> groups = new Vector<List>(threads);
		int total = objects.size();
		int devicesPerThread = (total / threads);
		if (total % threads > 0) devicesPerThread++;
		//System.out.println("Making "+threads+" groups of "+devicesPerThread+" devices out of "+total+" devices in total");
		for (int i = 0; i < threads; i++) {
			int firstObject = i * devicesPerThread;
			if (firstObject >= total) break;
			int lastObject = firstObject + devicesPerThread - 1;
			if (lastObject >= total) lastObject = total - 1;
			lastObject++;
			//System.out.println("Grouping together "+(lastDevice-firstDevice)+" devices (#"+firstDevice+" to "+lastDevice+")");
			List threadObjects = objects.subList(firstObject, lastObject);
			groups.add(threadObjects);
		}
		return groups;
	}


	/**
	 * Start all notification threads.
	 * 
	 * This method returns immediately, as all threads start working on their own.
	 * To wait until all threads are finished, use the waitForAllThreads() method.
	 */
	public synchronized NotificationThreads start() {
		if (started) return this;
		started = true;
		if (threadsRunning > 0) throw new IllegalStateException("NotificationThreads already started (" + threadsRunning + " still running)");
		assignThreadsNumbers();
		for (NotificationThread thread : threads) {
			threadsRunning++;
			thread.start();
			try {
				/* Wait for a specific number of milliseconds to elapse so that not all threads start simultaenously. */
				Thread.sleep(delayBetweenThreads);
			} catch (InterruptedException e) {
			}
		}
		if (listener != null) listener.eventAllThreadsStarted(this);
		return this;
	}


	/**
	 * Configure in all threads the maximum number of notifications per connection.
	 * 
	 * As soon as a thread reaches that maximum, it will automatically close the connection,
	 * initialize a new connection and continue pushing more notifications.
	 * 
	 * @param notifications the maximum number of notifications that threads will push in a single connection (default is 200)
	 */
	public void setMaxNotificationsPerConnection(int notifications) {
		for (NotificationThread thread : threads)
			thread.setMaxNotificationsPerConnection(notifications);
	}


	/**
	 * Configure in all threads the number of milliseconds that threads should wait between each notification.
	 * 
	 * This feature is intended to alleviate intense resource usage that can occur when
	 * sending large quantities of notifications very quickly.

	 * @param milliseconds the number of milliseconds threads should sleep between individual notifications (default is 0)
	 */
	public void setSleepBetweenNotifications(long milliseconds) {
		for (NotificationThread thread : threads)
			thread.setSleepBetweenNotifications(milliseconds);
	}


	/**
	 * Get a list of threads created to push notifications.
	 * 
	 * @return a list of threads
	 */
	public List<NotificationThread> getThreads() {
		return threads;
	}


	/**
	 * Get the progress listener, if any is attached.
	 * @return a progress listener
	 */
	public NotificationProgressListener getListener() {
		return listener;
	}


	/**
	 * Attach an event listener to this object as well as all linked threads.
	 * 
	 * @param listener
	 */
	public void setListener(NotificationProgressListener listener) {
		this.listener = listener;
		for (NotificationThread thread : threads)
			thread.setListener(listener);
	}


	/**
	 * Worker threads invoke this method as soon as they have completed their work.
	 * This method tracks the number of threads still running, allowing us
	 * to detect when ALL threads have finished.
	 * 
	 * When all threads are done working, this method fires an AllThreadsFinished
	 * event to the attached listener (if one is present) and wakes up any
	 * object that is waiting for the waitForAllThreads() method to return.
	 * 
	 * @param notificationThread
	 */
	protected synchronized void threadFinished(NotificationThread notificationThread) {
		threadsRunning--;
		if (threadsRunning == 0) {
			if (listener != null) listener.eventAllThreadsFinished(this);
			try {
				synchronized (finishPoint) {
					finishPoint.notifyAll();
				}
			} catch (Exception e) {
			}
		}
	}


	/**
	 * Wait for all threads to complete their work.
	 * 
	 * This method blocks and returns only when all threads are done.
	 * When using this method, you need to check critical exceptions manually to make sure that all threads were able to do their work.
	 * 
	 * This method should not be used in QUEUE mode, as threads stay idle and never end.
	 * 
	 * @throws InterruptedException
	 */
	public void waitForAllThreads() throws InterruptedException {
		try {
			synchronized (finishPoint) {
				finishPoint.wait();
			}
		} catch (IllegalMonitorStateException e) {
			/* All threads are most likely already done, so we ignore this */
		}
	}


	/**
	 * Wait for all threads to complete their work, but throw any critical exception that occurs in a thread.
	 * 
	 * This method blocks and returns only when all threads are done.
	 * 
	 * This method should not be used in QUEUE mode, as threads stay idle and never end.
	 * 
	 * @param throwCriticalExceptions If true, this method will throw the first critical exception that occured in a thread (if any).  If false, critical exceptions will not be checked.
	 * @throws Exception if throwCriticalExceptions is true and a critical exception did occur in a thread
	 */
	public void waitForAllThreads(boolean throwCriticalExceptions) throws Exception {
		waitForAllThreads();
		if (throwCriticalExceptions) {
			List<Exception> exceptions = getCriticalExceptions();
			if (exceptions.size() > 0) throw exceptions.get(0);
		}
	}


	/**
	 * Assign unique numbers to worker threads.
	 * Thread numbers allow each thread to generate message identifiers that
	 * are unique to all threads in the group.
	 */
	private void assignThreadsNumbers() {
		int t = 1;
		for (NotificationThread thread : threads)
			thread.setThreadNumber(t++);
	}


	/**
	 * Get a list of all notifications pushed by all threads.
	 * 
	 * @return a list of pushed notifications
	 */
	public PushedNotifications getPushedNotifications() {
		int capacity = 0;
		for (NotificationThread thread : threads)
			capacity += thread.getPushedNotifications().size();
		PushedNotifications all = new PushedNotifications(capacity);
		all.setMaxRetained(capacity);
		for (NotificationThread thread : threads)
			all.addAll(thread.getPushedNotifications());
		return all;
	}


	/**
	 * Clear the internal list of PushedNotification objects maintained in each thread.
	 * You should invoke this method once you no longer need the list of PushedNotification objects so that memory can be reclaimed.
	 */
	public void clearPushedNotifications() {
		for (NotificationThread thread : threads)
			thread.clearPushedNotifications();
	}


	/**
	 * Get a list of all notifications that all threads attempted to push but that failed.
	 * 
	 * @return a list of failed notifications
	 */
	public PushedNotifications getFailedNotifications() {
		return getPushedNotifications().getFailedNotifications();
	}


	/**
	 * Get a list of all notifications that all threads attempted to push and succeeded.
	 * 
	 * @return a list of successful notifications
	 */
	public PushedNotifications getSuccessfulNotifications() {
		return getPushedNotifications().getSuccessfulNotifications();
	}


	/**
	 * Get a list of critical exceptions that threads experienced.
	 * Critical exceptions include CommunicationException and KeystoreException.
	 * Exceptions related to tokens, payloads and such are *not* included here,
	 * as they are noted in individual PushedNotification objects.
	 * 
	 * @return a list of critical exceptions
	 */
	public List<Exception> getCriticalExceptions() {
		List<Exception> exceptions = new Vector<Exception>();
		for (NotificationThread thread : threads) {
			Exception exception = thread.getCriticalException();
			if (exception != null) exceptions.add(exception);
		}
		return exceptions;
	}


	/**
	 * Set the amount of time that the library will wait after starting a thread and before starting the next one.
	 * The default delay is 200 milliseconds.  This means that starting 10 threads will take 2 seconds to fully start.
	 * As discussed in issue report #102, adding a delay improves reliability.
	 * 
	 * @param delayBetweenThreads a number of milliseconds
	 */
	public void setDelayBetweenThreads(long delayBetweenThreads) {
		this.delayBetweenThreads = delayBetweenThreads;
	}


	/**
	 * Get the amount of time that the library will wait after starting a thread and before starting the next one.
	 * @return the number of milliseconds currently configured
	 */
	public long getDelayBetweenThreads() {
		return delayBetweenThreads;
	}

}
