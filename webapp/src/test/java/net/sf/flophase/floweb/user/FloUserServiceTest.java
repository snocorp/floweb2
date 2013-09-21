package net.sf.flophase.floweb.user;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sf.flophase.floweb.common.Response;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionContaining;
import org.hamcrest.collection.IsMapContaining;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;

/**
 * This class tests the {@link FloUserService} class.
 */
public class FloUserServiceTest {

	private final Mockery context = new Mockery();

	private UserStore mockUserStore;

	private FloUserService service;

	/**
	 * Tests the getSettngs method.
	 */
	@Test
	public void testGetSettings() {

		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).isUserLoggedIn();
				will(returnValue(true));

				oneOf(mockUserStore).getSetting("a");
				will(returnValue("valueA"));

				oneOf(mockUserStore).getSetting("b");
				will(returnValue("valueB"));

				oneOf(mockUserStore).getSetting("c");
				will(returnValue("valueC"));
			}
		});

		Response<Map<String, String>> response = service.getSettings("a", "b",
				"c");

		Map<String, String> settings = response.getContent();

		assertThat(settings, new IsMapContaining<>(Matchers.equalTo("a"),
				Matchers.equalTo("valueA")));
		assertThat(settings, new IsMapContaining<>(Matchers.equalTo("b"),
				Matchers.equalTo("valueB")));
		assertThat(settings, new IsMapContaining<>(Matchers.equalTo("c"),
				Matchers.equalTo("valueC")));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the getSettngs method.
	 */
	@Test
	public void testGetSettingsPermissionDenied() {

		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Map<String, String>> response = service.getSettings("a", "b",
				"c");

		Map<String, String> settings = response.getContent();

		assertNull(settings);

		assertEquals(Response.RESULT_FAILURE, response.getResult());

		assertThat(response.getMessages(), new IsCollectionContaining<>(
				Matchers.equalTo("Permission denied")));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the isUserLoggedIn method.
	 */
	@Test
	public void testIsUserLoggedIn() {
		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Boolean> result = service.isUserLoggedIn();

		assertTrue(result.getContent());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the getCurrentUser method.
	 */
	@Test
	public void testGetCurrentUser() {
		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).getUser();
				will(returnValue(new User("test@example.com", "localhost")));
			}
		});

		Response<User> response = service.getCurrentUser();

		User user = response.getContent();

		assertEquals("test@example.com", user.getEmail());
		assertEquals("localhost", user.getAuthDomain());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the putSettings method.
	 */
	@Test
	public void testPutSettings() {

		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).isUserLoggedIn();
				will(returnValue(true));

				oneOf(mockUserStore).putSetting("x", "valueX");

				oneOf(mockUserStore).putSetting("y", "valueY");

				oneOf(mockUserStore).putSetting("z", "valueZ");
			}
		});

		Map<String, String> settings = new HashMap<String, String>();
		settings.put("x", "valueX");
		settings.put("y", "valueY");
		settings.put("z", "valueZ");

		Response<Void> response = service.putSettings(settings);

		assertEquals(Response.RESULT_SUCCESS, response.getResult());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the putSettings method.
	 */
	@Test
	public void testPutSettingsPermissionDenied() {

		context.checking(new Expectations() {
			{
				oneOf(mockUserStore).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Map<String, String> settings = new HashMap<String, String>();
		settings.put("x", "valueX");
		settings.put("y", "valueY");
		settings.put("z", "valueZ");

		Response<Void> response = service.putSettings(settings);

		assertEquals(Response.RESULT_FAILURE, response.getResult());

		assertThat(response.getMessages(), new IsCollectionContaining<>(
				Matchers.equalTo("Permission denied")));

		context.assertIsSatisfied();
	}

	/**
	 * Sets up the test case. Mocks the user store and creates the service
	 * instance.
	 */
	@Before
	public void setUp() {
		mockUserStore = context.mock(UserStore.class);

		service = new FloUserService(mockUserStore);
	}
}
