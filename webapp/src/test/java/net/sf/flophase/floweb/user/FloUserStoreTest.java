package net.sf.flophase.floweb.user;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * This class tests the {@link FloUserStore} class.
 */
public class FloUserStoreTest {

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig(),
			new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true)
			.setEnvEmail("email@example.com").setEnvAuthDomain("localhost");

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The mock user service.
	 */
	private UserService userService;

	/**
	 * The mock memcache service
	 */
	private MemcacheService memcacheService;

	/**
	 * The mock data store.
	 */
	private DatastoreService datastore;

	/**
	 * The store to be tested.
	 */
	private FloUserStore store;

	/**
	 * Sets up the test case. Creates the mock user service. Creates the store
	 * to be tested.
	 */
	@Before
	public void setUp() {
		userService = context.mock(UserService.class);

		memcacheService = context.mock(MemcacheService.class);

		datastore = context.mock(DatastoreService.class);

		helper.setUp();
	}

	/**
	 * Tears down the test case.
	 */
	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * Tests the {@link FloUserStore#getUser()} method.
	 */
	@Test
	public void testGetUser() {
		final User expectedUser = new User("email@example.com", "localhost");

		context.checking(new Expectations() {
			{
				oneOf(userService).getCurrentUser();
				will(returnValue(expectedUser));
			}
		});

		store = new FloUserStore(userService, memcacheService, datastore);

		User user = store.getUser();

		assertEquals(expectedUser, user);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the the isUserLoggedIn properly delegates to the user service.
	 */
	@Test
	public void testIsUserLoggedIn() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		store = new FloUserStore(userService, memcacheService, datastore);

		boolean result = store.isUserLoggedIn();

		assertTrue(result);

		context.assertIsSatisfied();
	}

	/**
	 * Tests that get setting attempts to load from the cache and then loads
	 * from the data store.
	 */
	@Test
	public void testGetSettingCached() {
		final User user = new User("email@example.com", "localhost", "uniqueId");

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(userService).getCurrentUser();
				will(returnValue(user));

				oneOf(memcacheService).get("uniqueId:key");
				will(returnValue("result"));
			}
		});

		store = new FloUserStore(userService, memcacheService, datastore);

		String result = store.getSetting("key");

		assertEquals("result", result);

		context.assertIsSatisfied();
	}

	/**
	 * Tests that get setting attempts to load from the cache and then loads
	 * from the data store.
	 */
	@Test
	public void testGetSettingNotCached() {
		final User user = new User("email@example.com", "localhost", "uniqueId");

		final PreparedQuery preparedQuery = context.mock(PreparedQuery.class);

		final Entity entity = new Entity("UserSettings");
		entity.setProperty("key", "testResult");

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(datastore).prepare(with(aNonNull(Query.class)));
				will(returnValue(preparedQuery));

				oneOf(preparedQuery).asSingleEntity();
				will(returnValue(entity));

				oneOf(userService).getCurrentUser();
				will(returnValue(user));

				oneOf(memcacheService).put("uniqueId:key", "testResult");

				oneOf(memcacheService).get("uniqueId:key");
				will(returnValue(null));
			}
		});

		store = new FloUserStore(userService, memcacheService, datastore);

		String result = store.getSetting("key");

		assertEquals("testResult", result);

		context.assertIsSatisfied();
	}

	/**
	 * Tests that putting a setting persists and caches the value.
	 */
	@Test
	public void testPutSetting() {
		final User user = new User("email@example.com", "localhost", "uniqueId");

		final PreparedQuery preparedQuery = context.mock(PreparedQuery.class);

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(datastore).prepare(with(aNonNull(Query.class)));
				will(returnValue(preparedQuery));

				oneOf(preparedQuery).asSingleEntity();
				will(returnValue(new Entity("UserSettings")));

				oneOf(datastore).put(with(aNonNull(Entity.class)));

				oneOf(userService).getCurrentUser();
				will(returnValue(user));

				oneOf(memcacheService).put("uniqueId:testKey", "testValue");
			}
		});

		store = new FloUserStore(userService, memcacheService, datastore);

		store.putSetting("testKey", "testValue");

		context.assertIsSatisfied();
	}

}
