package javapns.notification;

import java.util.*;

/**
 * <p>A list of PushedNotification objects.</p>
 * 
 * <p>This list can be configured to retain a maximum number of objects.  When that maximum is reached, older objects are removed from the list before new ones are added.</p>
 * 
 * <p>Internally, this list extends Vector.</p>
 * 
 * @author Sylvain Pedneault
 */
public class PushedNotifications extends Vector<PushedNotification> implements List<PushedNotification> {

	private static final long serialVersionUID = 1L;

	private int maxRetained = 1000;


	/**
	 * Construct an empty list of PushedNotification objects.
	 */
	public PushedNotifications() {
	}


	/**
	 * Construct an empty list of PushedNotification objects with a suggested initial capacity.
	 * 
	 * @param capacity
	 */
	public PushedNotifications(int capacity) {
		super(capacity);
	}


	/**
	 * Construct an empty list of PushedNotification objects, and copy the maxRetained property from the provided parent list.
	 * 
	 * @param parent
	 */
	public PushedNotifications(PushedNotifications parent) {
		this.maxRetained = parent.getMaxRetained();
	}


	/**
	 * Filter a list of pushed notifications and return only the ones that were successful.
	 * 
	 * @return a filtered list containing only notifications that were succcessful
	 */
	public PushedNotifications getSuccessfulNotifications() {
		PushedNotifications filteredList = new PushedNotifications(this);
		for (PushedNotification notification : this) {
			if (notification.isSuccessful()) filteredList.add(notification);
		}
		return filteredList;
	}


	/**
	 * Filter a list of pushed notifications and return only the ones that failed.
	 * 
	 * @return a filtered list containing only notifications that were <b>not</b> successful
	 */
	public PushedNotifications getFailedNotifications() {
		PushedNotifications filteredList = new PushedNotifications(this);
		for (PushedNotification notification : this) {
			if (!notification.isSuccessful()) {
				filteredList.add(notification);
			}
		}
		return filteredList;
	}


	@Override
	public synchronized boolean add(PushedNotification notification) {
		prepareAdd(1);
		return super.add(notification);
	}


	@Override
	public synchronized void addElement(PushedNotification notification) {
		prepareAdd(1);
		super.addElement(notification);
	}


	@Override
	public synchronized boolean addAll(Collection<? extends PushedNotification> notifications) {
		prepareAdd(notifications.size());
		return super.addAll(notifications);
	}


	private void prepareAdd(int n) {
		int size = size();
		if (size + n > maxRetained) {
			for (int i = 0; i < n; i++)
				remove(0);
		}
	}


	/**
	 * Set the maximum number of objects that this list retains.
	 * When this maximum is reached, older objects are removed from the list before new ones are added.
	 * 
	 * @param maxRetained the maxRetained value currently configured (default is 1000)
	 */
	public void setMaxRetained(int maxRetained) {
		this.maxRetained = maxRetained;
	}


	/**
	 * Get the maximum number of objects that this list retains.
	 * @return the maximum number of objects that this list retains
	 */
	public int getMaxRetained() {
		return maxRetained;
	}

}
