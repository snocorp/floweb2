package net.sf.flophase.floweb.user;

import java.util.Map;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.User;

/**
 * This class provides an interface to interact with a user's attributes.
 */
public interface UserService {
	/**
	 * Returns the settings specified in the keys array. If the array is null or
	 * empty, returns all settings.
	 * 
	 * @param keys
	 *            The array of keys
	 * @return The settings map, maybe be empty.
	 */
	public Response<Map<String, String>> getSettings(String... keys);

	/**
	 * Sets the given settings for the current user.
	 * 
	 * @param settings
	 *            The settings.
	 * @return the response
	 */
	public Response<Void> putSettings(Map<String, String> settings);

	/**
	 * Returns true if the current user is logged in.
	 * 
	 * @return Whether or not the current user is logged in.
	 */
	public Response<Boolean> isUserLoggedIn();

	/**
	 * Returns the current user, if there is one.
	 * 
	 * @return The current user
	 */
	public Response<User> getCurrentUser();
}
