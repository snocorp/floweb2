package net.sf.flophase.floweb.entry;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import net.sf.flophase.floweb.common.Response;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.UserService;

/**
 * This class tests the {@link FloEntryService} class.
 */
public class FloEntryServiceTest {

	/**
	 * Invalid amount message.
	 */
	private static final String MSG_INVALID_AMOUNT = "Amount must be a valid number";

	/**
	 * The permission denied message.
	 */
	private static final String MSG_PERMISSION_DENIED = "Permission denied";

	/**
	 * The amount is missing message.
	 */
	private static final String MSG_MISSING_AMOUNT = "Amount is required";

	/**
	 * An invalid amount.
	 */
	private static final String INVALID_AMOUNT = "a";

	/**
	 * An invalid transaction ID.
	 */
	private static final String INVALID_XACTION_ID = "c";

	/**
	 * An invalid account ID.
	 */
	private static final String INVALID_ACCOUNT_ID = "b";

	/**
	 * The entry amount.
	 */
	private static final double AMOUNT1 = 1.23;

	/**
	 * The transaction ID.
	 */
	private static final long XACTION_ID = 2L;

	/**
	 * The account ID
	 */
	private static final long ACCOUNT_ID = 1L;

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The service to be tested.
	 */
	private FloEntryService service;

	/**
	 * The user service.
	 */
	private UserService userService;

	/**
	 * The entry store.
	 */
	private EntryStore entryStore;

	/**
	 * Sets up the test case. Creates mocked instances of the user service and entry store. Creates the service to be
	 * tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		userService = context.mock(UserService.class);
		entryStore = context.mock(EntryStore.class);

		service = new FloEntryService(userService, entryStore);
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method. Ensures that the entry store is
	 * called with the correct parameters.
	 */
	@Test
	public void testEditEntry() {
		final Entry entry = new Entry();

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(entryStore).editEntry(ACCOUNT_ID, XACTION_ID, AMOUNT1);
				will(returnValue(entry));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(ACCOUNT_ID), String.valueOf(XACTION_ID),
		        String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertTrue(response.getMessages().isEmpty());

		assertThat(response.getContent(), is(equalTo(entry)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditEntryWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(ACCOUNT_ID), String.valueOf(XACTION_ID),
		        String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an invalid amount. Ensures that a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithInvalidAmount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(ACCOUNT_ID), String.valueOf(XACTION_ID),
		        INVALID_AMOUNT);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_AMOUNT, isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with a null amount. Ensures that a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithNullAmount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(ACCOUNT_ID), String.valueOf(XACTION_ID), null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_MISSING_AMOUNT, isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an empty amount. Ensures that a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithEmptyAmount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(ACCOUNT_ID), String.valueOf(XACTION_ID), "");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_MISSING_AMOUNT, isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an invalid account. Ensures that
	 * a failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithInvalidAccount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(INVALID_ACCOUNT_ID, String.valueOf(XACTION_ID),
		        String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The account key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with a null account. Ensures that a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithNullAccount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(null, String.valueOf(XACTION_ID), String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The account key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an empty account. Ensures that a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithEmptyAccount() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry("", String.valueOf(XACTION_ID), String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The account key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an invalid transaction. Ensures
	 * that a failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithInvalidTransaction() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(XACTION_ID), INVALID_XACTION_ID,
		        String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The transaction key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with a null transaction. Ensures that
	 * a failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithNullTransaction() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(XACTION_ID), null, String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The transaction key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloEntryService#editEntry(String, String, String)} method with an empty transaction. Ensures
	 * that a failure response is returned with the correct message.
	 */
	@Test
	public void testEditEntryWithEmptyTransaction() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Entry> response = service.editEntry(String.valueOf(XACTION_ID), "", String.valueOf(AMOUNT1));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat("The transaction key is not valid", isIn(response.getMessages()));

		assertThat(response.getContent(), is(nullValue()));

		context.assertIsSatisfied();
	}

}
