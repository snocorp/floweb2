package net.sf.flophase.floweb.common;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

/**
 * This class tests the {@link FloUserStore} class.
 */
public class FloUserStoreTest {

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The user service.
	 */
	private UserService userService;

	/**
	 * The store to be tested.
	 */
	private FloUserStore store;

	/**
	 * Sets up the test case. Creates the mock user service. Creates the store to be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		userService = context.mock(UserService.class);
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

		store = new FloUserStore(userService);

		User user = store.getUser();

		assertEquals(expectedUser, user);

		context.assertIsSatisfied();
	}

}
