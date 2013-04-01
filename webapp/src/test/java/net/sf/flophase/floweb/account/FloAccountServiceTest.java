package net.sf.flophase.floweb.account;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.sf.flophase.floweb.common.Response;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.UserService;

/**
 * This class tests the {@link FloAccountService} class.
 */
public class FloAccountServiceTest {

	/**
	 * The message returned when there is an invalid key.
	 */
	private static final String MSG_INVALID_KEY = "The key is not valid";

	/**
	 * An invalid account key.
	 */
	private static final String INVALID_ACCOUNT_KEY = "xyz";

	/**
	 * A valid account key.
	 */
	private static final String ACCOUNT_KEY = "1";

	/**
	 * The message returned when there is an invalid balance.
	 */
	private static final String MSG_INVALID_BALANCE = "Balance must be a valid number";

	/**
	 * The message returned when there is an invalid negative threshold.
	 */
	private static final String MSG_INVALID_NEGATIVE_THRESHOLD = "Negative threshold must be a valid number";

	/**
	 * The message returned when there is an invalid positive threshold.
	 */
	private static final String MSG_INVALID_POSITIVE_THRESHOLD = "Positive threshold must be a valid number";

	/**
	 * The message returned when there is no balance.
	 */
	private static final String MSG_MISSING_BALANCE = "Balance is required";

	/**
	 * The message returned when there is no name.
	 */
	private static final String MSG_MISSING_NAME = "Name is required";

	/**
	 * Message returned when attempting to delete the last account.
	 */
	private static final String MSG_LAST_ACCOUNT = "The last account cannot be deleted. Create a new account before deleting the account.";

	/**
	 * An invalid account balance.
	 */
	private static final String INVALID_ACCOUNT_BALANCE = "abc";

	/**
	 * A valid account balance.
	 */
	private static final double ACCOUNT_BALANCE = 1.23;

	/**
	 * The account negative threshold.
	 */
	private static final double ACCOUNT_NEGATIVE_THRESHOLD = 99;

	/**
	 * An invalid account negative threshold.
	 */
	private static final String INVALID_ACCOUNT_NEGATIVE_THRESHOLD = "abc";

	/**
	 * The account positive threshold.
	 */
	private static final double ACCOUNT_POSITIVE_THRESHOLD = 1000;

	/**
	 * An invalid account positive threshold.
	 */
	private static final String INVALID_ACCOUNT_POSITIVE_THRESHOLD = "abc";

	/**
	 * A valid account name.
	 */
	private static final String ACCOUNT_NAME = "Account1";

	/**
	 * An account name that is too long.
	 */
	private static final String INVALID_ACCOUNT_NAME_TOO_LONG = "1234567890123456789012345678901";

	/**
	 * The message returned when permission is denied.
	 */
	private static final String MSG_PERMISSION_DENIED = "Permission denied";

	/**
	 * The message returned whenthe name is too long.
	 */
	private static final String MSG_NAME_TOO_LONG = "Name must be no longer than 30 characters";

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The class under test.
	 */
	private FloAccountService service;

	/**
	 * The user service that the service depends upon.
	 */
	private UserService userService;

	/**
	 * The account store that the service depends upon.
	 */
	private AccountStore acctStore;

	/**
	 * Sets up the test case. Creates mock user service and account store.
	 * Creates the service to be tested.
	 */
	@Before
	public void setUp() {
		userService = context.mock(UserService.class);
		acctStore = context.mock(AccountStore.class);

		service = new FloAccountService(userService, acctStore);
	}

	/**
	 * Tests the {@link FloAccountService#getAccounts()} method.
	 */
	@Test
	public void testGetAccounts() {
		final List<Account> accounts = new ArrayList<Account>();

		context.checking(new Expectations() {
			{
				// there is a user that is logged in
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				// return the list of accounts
				one(acctStore).getAccounts();
				will(returnValue(accounts));
			}
		});

		Response<List<Account>> response = service.getAccounts();

		assertThat(response.getContent(), is(equalTo(accounts)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#getAccounts()} method when there is no
	 * logged in user.
	 */
	@Test
	public void testGetAccountsWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				one(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<List<Account>> response = service.getAccounts();

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method.
	 * Ensures that the account store is called with the correct parameters.
	 */
	@Test
	public void testAddAccount() {
		final Account account = new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE);

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).createAccount(ACCOUNT_NAME, ACCOUNT_BALANCE);
				will(returnValue(account));
			}
		});

		Response<Account> response = service.addAccount(ACCOUNT_NAME,
				String.valueOf(ACCOUNT_BALANCE));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(account)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with no logged in user. Ensures that a failure response is returned with
	 * the correct message.
	 */
	@Test
	public void testAddAccountWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Account> response = service.addAccount(ACCOUNT_NAME,
				String.valueOf(ACCOUNT_BALANCE));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with an invalid balance. Ensures that a failure response is returned with
	 * the correct message.
	 */
	@Test
	public void testAddAccountWithInvalidBalance() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.addAccount(ACCOUNT_NAME,
				INVALID_ACCOUNT_BALANCE);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_INVALID_BALANCE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with a null balance. Ensures that a failure response is returned with the
	 * correct message.
	 */
	@Test
	public void testAddAccountWithNullBalance() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.addAccount(ACCOUNT_NAME, null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_BALANCE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with a null name. Ensures that a failure response is returned with the
	 * correct message.
	 */
	@Test
	public void testAddAccountWithNullName() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.addAccount(null,
				String.valueOf(ACCOUNT_BALANCE));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with an empty name. Ensures that a failure response is returned with the
	 * correct message.
	 */
	@Test
	public void testAddAccountWithEmptyName() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.addAccount("",
				String.valueOf(ACCOUNT_BALANCE));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#addAccount(String, String)} method
	 * with a name that is too long. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testAddAccountWithNameTooLong() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.addAccount(
				INVALID_ACCOUNT_NAME_TOO_LONG, String.valueOf(ACCOUNT_BALANCE));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_NAME_TOO_LONG, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method. Ensures
	 * that the account store is called with the correct parameters.
	 */
	@Test
	public void testDeleteAccount() {
		final List<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account());
		accounts.add(new Account());

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).getAccounts();
				will(returnValue(accounts));

				one(acctStore).deleteAccount(Long.parseLong(ACCOUNT_KEY));
			}
		});

		Response<Void> response = service.deleteAccount(ACCOUNT_KEY);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method on the
	 * last account. Ensures that a failure response is returned with the
	 * correct message.
	 */
	@Test
	public void testDeleteLastAccount() {
		final List<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account());

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).getAccounts();
				will(returnValue(accounts));
			}
		});

		Response<Void> response = service.deleteAccount(ACCOUNT_KEY);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_LAST_ACCOUNT, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method with no
	 * logged in user. Ensures that a failure response is returned with the
	 * correct message.
	 */
	@Test
	public void testDeleteAccountWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Void> response = service.deleteAccount(ACCOUNT_KEY);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method with an
	 * invalid key. Ensures that a failure response is returned with the correct
	 * message.
	 */
	@Test
	public void testDeleteAccountWithInvalidKey() {
		final List<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account());
		accounts.add(new Account());

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).getAccounts();
				will(returnValue(accounts));
			}
		});

		Response<Void> response = service.deleteAccount(INVALID_ACCOUNT_KEY);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method with an
	 * null key. Ensures that a failure response is returned with the correct
	 * message.
	 */
	@Test
	public void testDeleteAccountWithNullKey() {
		final List<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account());
		accounts.add(new Account());

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).getAccounts();
				will(returnValue(accounts));
			}
		});

		Response<Void> response = service.deleteAccount(null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountService#deleteAccount(String)} method with an
	 * empty key. Ensures that a failure response is returned with the correct
	 * message.
	 */
	@Test
	public void testDeleteAccountWithEmptyKey() {
		final List<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account());
		accounts.add(new Account());

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).getAccounts();
				will(returnValue(accounts));
			}
		});

		Response<Void> response = service.deleteAccount("");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method. Ensures that the account store is called with the correct
	 * parameters.
	 */
	@Test
	public void testEditAccount() {
		final Account account = new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE);

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).editAccount(Long.parseLong(ACCOUNT_KEY),
						ACCOUNT_NAME, ACCOUNT_BALANCE,
						ACCOUNT_NEGATIVE_THRESHOLD, ACCOUNT_POSITIVE_THRESHOLD);
				will(returnValue(account));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(account)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with no logged in user. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditAccountWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an invalid balance. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditAccountWithInvalidBalance() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, INVALID_ACCOUNT_BALANCE,
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_INVALID_BALANCE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an invalid negative threshold. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditAccountWithInvalidNegativeThreshold() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE),
				INVALID_ACCOUNT_NEGATIVE_THRESHOLD,
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_INVALID_NEGATIVE_THRESHOLD, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an invalid positive threshold. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditAccountWithInvalidPositiveThreshold() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				INVALID_ACCOUNT_POSITIVE_THRESHOLD);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_INVALID_POSITIVE_THRESHOLD, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a null balance. Ensures that a successful response is
	 * returned with a default negative threshold of 0.0.
	 */
	@Test
	public void testEditAccountWithNullNegativeThreshold() {
		final Account account = new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE);

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).editAccount(Long.parseLong(ACCOUNT_KEY),
						ACCOUNT_NAME, ACCOUNT_BALANCE, 0.0,
						ACCOUNT_POSITIVE_THRESHOLD);
				will(returnValue(account));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE), null,
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(account)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a null balance. Ensures that a successful response is
	 * returned with a default positive threshold of 0.0.
	 */
	@Test
	public void testEditAccountWithNullPositiveThreshold() {
		final Account account = new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE);

		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));

				one(acctStore).editAccount(Long.parseLong(ACCOUNT_KEY),
						ACCOUNT_NAME, ACCOUNT_BALANCE, ACCOUNT_NEGATIVE_THRESHOLD,
						0.0);
				will(returnValue(account));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE), String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(account)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a null balance. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testEditAccountWithNullBalance() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, null, String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_BALANCE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an empty balance. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testEditAccountWithEmptyBalance() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				ACCOUNT_NAME, "", String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_BALANCE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a null name. Ensures that a failure response is returned with
	 * the correct message.
	 */
	@Test
	public void testEditAccountWithNullName() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY, null,
				String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an empty name. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testEditAccountWithEmptyName() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY, "",
				String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a name that is too long. Ensures that a failure response is
	 * returned with the correct message.
	 */
	@Test
	public void testEditAccountWithNameTooLong() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(ACCOUNT_KEY,
				INVALID_ACCOUNT_NAME_TOO_LONG, String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertNull(response.getContent());

		assertThat(MSG_NAME_TOO_LONG, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an invalid key. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testEditAccountWithInvalidKey() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(INVALID_ACCOUNT_KEY,
				ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with a null key. Ensures that a failure response is returned with
	 * the correct message.
	 */
	@Test
	public void testEditAccountWithNullKey() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount(null, ACCOUNT_NAME,
				String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountService#editAccount(String, String, String, String, String)}
	 * method with an empty key. Ensures that a failure response is returned
	 * with the correct message.
	 */
	@Test
	public void testEditAccountWithEmptyKey() {
		context.checking(new Expectations() {
			{
				one(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Account> response = service.editAccount("", ACCOUNT_NAME,
				String.valueOf(ACCOUNT_BALANCE),
				String.valueOf(ACCOUNT_NEGATIVE_THRESHOLD),
				String.valueOf(ACCOUNT_POSITIVE_THRESHOLD));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}
}
