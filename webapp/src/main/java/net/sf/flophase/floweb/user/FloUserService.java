package net.sf.flophase.floweb.user;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.Response;

import com.google.appengine.api.users.User;

/**
 * This service provides a simple access layer for the user store.
 */
public class FloUserService implements UserService {

	private final UserStore userStore;

	/**
	 * Creates a new {@link FloUserService}.
	 * 
	 * @param userStore
	 *            The user store
	 */
	@Inject
	public FloUserService(UserStore userStore) {
		this.userStore = userStore;
	}

	@Override
	public Response<Map<String, String>> getSettings(String... keys) {
		Response<Map<String, String>> response;

		if (userStore.isUserLoggedIn()) {
			Map<String, String> settings = new HashMap<String, String>();
			String value;
			for (String key : keys) {
				value = userStore.getSetting(key);
				if (value != null) {
					settings.put(key, value);
				}
			}

			response = new Response<Map<String, String>>(
					Response.RESULT_SUCCESS, settings);
		}
		// the user was not logged in
		else {
			// respond with failure
			response = new Response<Map<String, String>>(
					Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

	@Override
	public Response<Boolean> isUserLoggedIn() {
		return new Response<Boolean>(Response.RESULT_SUCCESS,
				userStore.isUserLoggedIn());
	}

	@Override
	public Response<User> getCurrentUser() {
		return new Response<User>(Response.RESULT_SUCCESS, userStore.getUser());
	}

	@Override
	public Response<Void> putSettings(Map<String, String> settings) {
		Response<Void> response;

		if (userStore.isUserLoggedIn()) {
			for (Map.Entry<String, String> entry : settings.entrySet()) {
				userStore.putSetting(entry.getKey(), entry.getValue());
			}

			response = new Response<Void>(Response.RESULT_SUCCESS);
		}
		// the user was not logged in
		else {
			// respond with failure
			response = new Response<Void>(Response.RESULT_FAILURE);

			// indicate permission was denied
			response.addMessage("Permission denied");
		}

		return response;
	}

}
