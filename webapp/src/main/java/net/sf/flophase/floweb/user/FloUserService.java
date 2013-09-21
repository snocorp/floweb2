package net.sf.flophase.floweb.user;

import java.util.HashMap;
import java.util.Map;

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
	public FloUserService(UserStore userStore) {
		this.userStore = userStore;
	}

	@Override
	public Map<String, String> getSettings(String... keys) {
		Map<String, String> settings = new HashMap<String, String>();
		String value;
		for (String key : keys) {
			value = userStore.getSetting(key);
			if (value != null) {
				settings.put(key, value);
			}
		}

		return settings;
	}

	@Override
	public boolean isUserLoggedIn() {
		return userStore.isUserLoggedIn();
	}

	@Override
	public User getCurrentUser() {
		return userStore.getUser();
	}

	@Override
	public void putSettings(Map<String, String> settings) {
		for (Map.Entry<String, String> entry : settings.entrySet()) {
			userStore.putSetting(entry.getKey(), entry.getValue());
		}
	}

}
