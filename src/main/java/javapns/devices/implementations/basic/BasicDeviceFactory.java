package javapns.devices.implementations.basic;

import java.sql.*;
import java.util.*;

import javapns.devices.*;
import javapns.devices.exceptions.*;

/**
 * This class implements an in-memory DeviceFactory (backed by a Map).
 * Since this class does not persist Device objects, it should not be used in a production environment.
 * 
 * NB : Future Improvement :
 * 		 - Add a method to find a device knowing his token
 * 		 - Add a method to update a device (timestamp or token)
 *       - method to compare two devices, and replace when the device token has changed
 *       
 * @author Maxime Peron
 *
 */
@Deprecated
public class BasicDeviceFactory implements DeviceFactory {

	/* A map containing all the devices, identified with their id */
	private Map<String, BasicDevice> devices;

	/* synclock */
	private static final Object synclock = new Object();


	/**
	 * Constructs a VolatileDeviceFactory
	 */
	public BasicDeviceFactory() {
		this.devices = new HashMap<String, BasicDevice>();
	}


	/**
	 * Add a device to the map
	 * @param id The device id
	 * @param token The device token
	 * @throws DuplicateDeviceException
	 * @throws NullIdException 
	 * @throws NullDeviceTokenException 
	 */
	public Device addDevice(String id, String token) throws DuplicateDeviceException, NullIdException, NullDeviceTokenException, Exception {
		if ((id == null) || (id.trim().equals(""))) {
			throw new NullIdException();
		} else if ((token == null) || (token.trim().equals(""))) {
			throw new NullDeviceTokenException();
		} else {
			if (!this.devices.containsKey(id)) {
				token = token.trim().replace(" ", "");
				BasicDevice device = new BasicDevice(id, token, new Timestamp(Calendar.getInstance().getTime().getTime()));
				this.devices.put(id, device);
				return device;
			} else {
				throw new DuplicateDeviceException();
			}
		}
	}


	/**
	 * Get a device according to his id
	 * @param id The device id
	 * @return The device
	 * @throws UnknownDeviceException
	 * @throws NullIdException 
	 */
	public Device getDevice(String id) throws UnknownDeviceException, NullIdException {
		if ((id == null) || (id.trim().equals(""))) {
			throw new NullIdException();
		} else {
			if (this.devices.containsKey(id)) {
				return this.devices.get(id);
			} else {
				throw new UnknownDeviceException();
			}
		}
	}


	/**
	 * Remove a device
	 * @param id The device id
	 * @throws UnknownDeviceException
	 * @throws NullIdException 
	 */
	public void removeDevice(String id) throws UnknownDeviceException, NullIdException {
		if ((id == null) || (id.trim().equals(""))) {
			throw new NullIdException();
		}
		if (this.devices.containsKey(id)) {
			this.devices.remove(id);
		} else {
			throw new UnknownDeviceException();
		}
	}
}