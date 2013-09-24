package net.sf.flophase.floweb.user;

import com.google.appengine.api.users.User;

/**
 * This interface defines a store to access the user entity.
 */
public interface UserStore {
	/**
	 * Returns the currently logged in user.
	 * 
	 * @return The user.
	 */
	public abstract User getUser();

	/**
	 * Returns true if the current user is logged in.
	 * 
	 * @return Whether or not the current user is logged in.
	 */
	public abstract boolean isUserLoggedIn();

	/**
	 * Returns the value for the requested setting. Returns null if the setting
	 * does not exist.
	 * 
	 * @param key
	 *            The setting key.
	 * @return The value.
	 */
	public abstract String getSetting(String key);

	/**
	 * Sets the value for a user setting.
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public abstract void putSetting(String key, String value);

	/**
	 * Create a URL where a user can login.
	 * 
	 * @param destinationURL
	 *            The URI of the destination page.
	 * @return The URL
	 */
	public String createLoginURL(String destinationURL);

	/**
	 * Create a URL where a user can logout.
	 * 
	 * @param destinationURL
	 *            The URI of the destination page.
	 * @return The URL
	 */
	public String createLogoutURL(String destinationURL);
}
