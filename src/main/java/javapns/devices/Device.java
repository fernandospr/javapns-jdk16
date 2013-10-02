package javapns.devices;

import java.sql.*;

/**
 * This is the common interface for all Devices.
 * It allows the DeviceFactory to support multiple
 * implementations of Device (in-memory, JPA-backed, etc.)
 * 
 * @author Sylvain Pedneault
 */
public interface Device {

	/**
	 * An id representing a particular device.
	 * 
	 * Note that this is a local reference to the device,
	 * which is not related to the actual device UUID or
	 * other device-specific identification. Most of the
	 * time, this deviceId should be the same as the token.
	 * 
	 * @return the device id
	 */
	public String getDeviceId();


	/**
	 * A device token.
	 * 
	 * @return the device token
	 */
	public String getToken();


	/**
	 * 
	 * @return the last register
	 */
	public Timestamp getLastRegister();


	/**
	 * An id representing a particular device.
	 * 
	 * Note that this is a local reference to the device,
	 * which is not related to the actual device UUID or
	 * other device-specific identification. Most of the
	 * time, this deviceId should be the same as the token.
	 * 
	 * @param id the device id
	 */
	public void setDeviceId(String id);


	/**
	 * Set the device token
	 * @param token
	 */
	public void setToken(String token);


	/**
	 * 
	 * @param lastRegister the last register
	 */
	public void setLastRegister(Timestamp lastRegister);

}