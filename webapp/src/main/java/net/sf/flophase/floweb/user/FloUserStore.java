package net.sf.flophase.floweb.user;

import javax.inject.Inject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

/**
 * The user store allows access to the logged in user.
 */
public class FloUserStore implements UserStore {

	private static final String TYPE_USER_SETTING = "UserSettings";

	private static final String KEY_USER_ID = "user_id";

	private static final String KEY_SEPARATOR = ":";

	/**
	 * The user service.
	 */
	private final UserService userService;

	/**
	 * The memcache service.
	 */
	private final MemcacheService memcacheService;

	/**
	 * The data store.
	 */
	private final DatastoreService datastore;

	/**
	 * Creates a mew FlowUserStore instance.
	 * 
	 * @param userService
	 *            The user service
	 * @param memcacheService
	 *            The memcache service
	 * @param datastore
	 *            The datastore service
	 */
	@Inject
	public FloUserStore(UserService userService,
			MemcacheService memcacheService, DatastoreService datastore) {
		this.userService = userService;
		this.memcacheService = memcacheService;
		this.datastore = datastore;
	}

	@Override
	public User getUser() {
		return userService.getCurrentUser();
	}

	@Override
	public boolean isUserLoggedIn() {
		return userService.isUserLoggedIn();
	}

	@Override
	public String getSetting(String key) {
		if (!isUserLoggedIn()) {
			return null;
		}

		User user = getUser();
		String cacheKey = user.getUserId() + KEY_SEPARATOR + key;

		Object value = memcacheService.get(cacheKey);
		if (value == null) {
			Entity userSettings = getSettingsEntity(user);
			if (userSettings != null) {
				value = userSettings.getProperty(key);

				memcacheService.put(cacheKey, value); // populate cache
			}
		}

		return value == null ? null : value.toString();
	}

	private Entity getSettingsEntity(User user) {
		Filter filter = new Query.FilterPredicate(KEY_USER_ID,
				FilterOperator.EQUAL, user.getUserId());
		Query q = new Query(TYPE_USER_SETTING).setFilter(filter);

		Entity userSettings = datastore.prepare(q).asSingleEntity();
		return userSettings;
	}

	@Override
	public void putSetting(String key, String value) {
		if (!isUserLoggedIn() || KEY_USER_ID.equals(key)) {
			return;
		}

		// persist the setting
		final User user = getUser();
		Entity userSettings = getSettingsEntity(user);
		if (userSettings == null) {
			userSettings = new Entity(TYPE_USER_SETTING);
			userSettings.setProperty(KEY_USER_ID, user.getUserId());
		}

		userSettings.setProperty(key, value);

		datastore.put(userSettings);

		String cacheKey = user.getUserId() + KEY_SEPARATOR + key;

		// cache it for later
		memcacheService.put(cacheKey, value);
	}

	@Override
	public String createLoginURL(String destinationURL) {
		return userService.createLoginURL(destinationURL);
	}

	@Override
	public String createLogoutURL(String destinationURL) {
		return userService.createLogoutURL(destinationURL);
	}

}
