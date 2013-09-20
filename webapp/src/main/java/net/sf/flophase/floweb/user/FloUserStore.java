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
		String cacheKey = user.getUserId() + ":" + key;

		Object value = memcacheService.get(cacheKey);
		if (value == null) {
			Filter filter = new Query.FilterPredicate("user_id",
					FilterOperator.EQUAL, user.getUserId());
			Query q = new Query("UserSetting").setFilter(filter);

			Entity userSettings = datastore.prepare(q).asSingleEntity();

			value = userSettings.getProperty(key);

			memcacheService.put(cacheKey, value); // populate cache
		}

		return value == null ? null : value.toString();
	}

	@Override
	public void putSetting(String key, String value) {
		if (!isUserLoggedIn()) {
			return;
		}

		// persist the setting
		Entity userSettings = new Entity("UserSettings");
		userSettings.setProperty(key, value);

		datastore.put(userSettings);

		User user = getUser();
		String cacheKey = user.getUserId() + ":" + key;

		// cache it for later
		memcacheService.put(cacheKey, value);
	}

}
