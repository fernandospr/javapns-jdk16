package javapns.notification.transmission;

/**
 * <h1>An event listener for monitoring progress of NotificationThreads</h1>
 * 
 * @author Sylvain Pedneault
 */
public interface NotificationProgressListener {

	public void eventAllThreadsStarted(NotificationThreads notificationThreads);


	public void eventThreadStarted(NotificationThread notificationThread);


	public void eventThreadFinished(NotificationThread notificationThread);


	public void eventConnectionRestarted(NotificationThread notificationThread);


	public void eventAllThreadsFinished(NotificationThreads notificationThreads);


	public void eventCriticalException(NotificationThread notificationThread, Exception exception);

}
