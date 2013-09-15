package net.sf.flophase.floweb.xaction;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.entry.Entry;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.UserService;

/**
 * This class tests the {@link FloTransactionService} class.
 */
public class FloTransactionServiceTest {

	/**
	 * The message indicating there is an invalid key.
	 */
	private static final String MSG_INVALID_KEY = "The key is not valid";

	/**
	 * The message indicating there is an invalid date.
	 */
	private static final String MSG_INVALID_DATE = "Date was not in the correct format (" + Constants.ISO_DATE_FORMAT
	        + ")";

	/**
	 * A message indicating there is an invalid month.
	 */
	private static final String MSG_INVALID_MONTH = "Month was not in the correct format (yyyy-MM)";

	/**
	 * A message indicating the month is missing.
	 */
	private static final String MSG_MISSING_MONTH = "Month is required";

	/**
	 * A message indicating the date is missing.
	 */
	private static final String MSG_MISSING_DATE = "Date is required";

	/**
	 * A message indicating the name is missing.
	 */
	private static final String MSG_MISSING_NAME = "Description is required";

	/**
	 * A message indicating that permission was denied.
	 */
	private static final String MSG_PERMISSION_DENIED = "Permission denied";

	/**
	 * An invalid key.
	 */
	private static final String INVALID_KEY = "xyz";

	/**
	 * An invalid date.
	 */
	private static final String INVALID_DATE = "def";

	/**
	 * An invalid month.
	 */
	private static final String INVALID_MONTH = "abc";

	/**
	 * The transaction key.
	 */
	private static final long TRANSACTION_KEY = 1L;

	/**
	 * The month of Feb 2012.
	 */
	private static final String MONTH_FEB_2012 = "2012-02";

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * A transaction name that is too long.
	 */
	private static final String INVALID_TRANSACTION_NAME_TOO_LONG = "1234567890123456789012345678901";

	/**
	 * The date of Feb 1, 2012.
	 */
	private static final Date DATE_FEB_1_2012 = new GregorianCalendar(2012, 1, 1).getTime();

	/**
	 * A ISO 8601 formatted version of February 1, 2012
	 */
	private static final String DATE_FEB_12_2012 = "2012-02-12";

	/**
	 * The message when the name is too long.
	 */
	private static final String MSG_NAME_TOO_LONG = "Description must be no longer than 30 characters";

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The service to be tested.
	 */
	private FloTransactionService service;

	/**
	 * The transaction store.
	 */
	private TransactionStore xactionStore;

	/**
	 * The user service.
	 */
	private UserService userService;

	/**
	 * Sets up the test case. Creates the mock transaction store and user service. Creates the service to be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		xactionStore = context.mock(TransactionStore.class);
		userService = context.mock(UserService.class);

		service = new FloTransactionService(xactionStore, userService);
	}

	/**
	 * Tests the {@link FloTransactionService#getTransactions(String)} method. Ensures the transaction store is called
	 * with the correct parameters.
	 */
	@Test
	public void testGetTransactions() {
		final List<FinancialTransaction> transactions = new ArrayList<FinancialTransaction>();

		Transaction xaction = new Transaction(null, TRANSACTION_NAME, DATE_FEB_1_2012);

		transactions.add(new FinancialTransaction(xaction, new HashMap<Long, Entry>()));

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(xactionStore).getTransactions(DATE_FEB_1_2012);
				will(returnValue(transactions));
			}
		});

		Response<List<FinancialTransaction>> response = service.getTransactions(MONTH_FEB_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(transactions)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#getTransactions(String)} method with no user logged in. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testGetTransactionsWithNoUserLoggedIn() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<List<FinancialTransaction>> response = service.getTransactions(MONTH_FEB_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#getTransactions(String)} method with an invalid month. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testGetTransactionsWithInvalidMonth() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<List<FinancialTransaction>> response = service.getTransactions(INVALID_MONTH);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_MONTH, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#getTransactions(String)} method with a null month. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testGetTransactionsWithNullMonth() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<List<FinancialTransaction>> response = service.getTransactions(null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_MONTH, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#getTransactions(String)} method with an empty month. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testGetTransactionsWithEmptyMonth() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<List<FinancialTransaction>> response = service.getTransactions("");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_MONTH, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method. Ensures the transaction store is
	 * called with the correct parameters.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testAddTransaction() throws Exception {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);

		final Date date = dateFormat.parse(DATE_FEB_12_2012);

		final Transaction xaction = new Transaction(null, TRANSACTION_NAME, date);

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(xactionStore).createTransaction(TRANSACTION_NAME, date);
				will(returnValue(xaction));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(TRANSACTION_NAME, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent().getDetails(), is(equalTo(xaction)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with no logged in user. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(TRANSACTION_NAME, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with an invalid date. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithInvalidDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(TRANSACTION_NAME, INVALID_DATE);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with a null date. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithNullDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(TRANSACTION_NAME, null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with an empty date. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithEmptyDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(TRANSACTION_NAME, "");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with a null name. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithNullName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(null, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with an empty name. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithEmptyName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction("", DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#addTransaction(String, String)} method with a name that is too long.
	 * Ensures a failure response is returned with the correct message.
	 */
	@Test
	public void testAddTransactionWithNameTooLong() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.addTransaction(INVALID_TRANSACTION_NAME_TOO_LONG,
		        DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_NAME_TOO_LONG, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#deleteTransaction(String)} method. Ensures the transaction store is called
	 * with the correct parameters.
	 */
	@Test
	public void testDeleteTransaction() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(xactionStore).deleteTransaction(TRANSACTION_KEY);
			}
		});

		Response<Void> response = service.deleteTransaction(String.valueOf(TRANSACTION_KEY));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#deleteTransaction(String)} method with no logged in user. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testDeleteTransactionWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<Void> response = service.deleteTransaction(String.valueOf(TRANSACTION_KEY));

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#deleteTransaction(String)} method with an invalid key. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testDeleteTransactionWithInvalidKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Void> response = service.deleteTransaction(INVALID_KEY);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#deleteTransaction(String)} method with a null key. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testDeleteTransactionWithNullKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Void> response = service.deleteTransaction(null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#deleteTransaction(String)} method with an empty key. Ensures a failure
	 * response is returned with the correct message.
	 */
	@Test
	public void testDeleteTransactionWithEmptyKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<Void> response = service.deleteTransaction("");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method. Ensures the transaction
	 * store is called with the correct parameters.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testCopyTransaction() throws Exception {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);

		final Date date = dateFormat.parse(DATE_FEB_12_2012);

		final Map<Long, Entry> entries = new HashMap<Long, Entry>();
		final FinancialTransaction xaction = new FinancialTransaction( new Transaction(null, TRANSACTION_NAME, date), entries );

		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				oneOf(xactionStore).copyTransaction(TRANSACTION_KEY, TRANSACTION_NAME, date);
				will(returnValue(xaction));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));

		assertThat(response.getContent(), is(equalTo(xaction)));

		assertTrue(response.getMessages().isEmpty());

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method with no logged in user.
	 * Ensures a failure response is returned with the correct message.
	 */
	@Test
	public void testCopyTransactionWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method with an invalid key.
	 * Ensures a failure response is returned with the correct message.
	 */
	@Test
	public void testCopyTransactionWithInvalidKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(INVALID_KEY, TRANSACTION_NAME, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method with a null key. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testCopyTransactionWithNullKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(null, TRANSACTION_NAME, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method with an empty key. Ensures
	 * a failure response is returned with the correct message.
	 */
	@Test
	public void testCopyTransactionWithEmptyKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction("", TRANSACTION_NAME, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is an invalid
	 * date. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithInvalidDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        INVALID_DATE);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_INVALID_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is a null date.
	 * Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithNullDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        null);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is an empty
	 * date. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithEmptyDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME, "");

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is a null name.
	 * Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithNullName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), null,
		        DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is an empty
	 * name. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithEmptyName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY), "", DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#copyTransaction(String, String, String)} method when there is an empty
	 * name. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testCopyTransactionWithNameTooLong() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});

		Response<FinancialTransaction> response = service.copyTransaction(String.valueOf(TRANSACTION_KEY),
		        INVALID_TRANSACTION_NAME_TOO_LONG, DATE_FEB_12_2012);

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(response.getContent(), is(nullValue()));

		assertThat(MSG_NAME_TOO_LONG, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method. Ensures the transaction
	 * store is called with the correct parameters.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testEditTransaction() throws Exception {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);
	
		final Date date = dateFormat.parse(DATE_FEB_12_2012);
	
		final Transaction xaction = new Transaction(null, TRANSACTION_NAME, date);
	
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
	
				oneOf(xactionStore).editTransaction(TRANSACTION_KEY, TRANSACTION_NAME, date);
				will(returnValue(xaction));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_SUCCESS)));
	
		assertThat(response.getContent(), is(equalTo(xaction)));
	
		assertTrue(response.getMessages().isEmpty());
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method with no logged in user.
	 * Ensures a failure response is returned with the correct message.
	 */
	@Test
	public void testEditTransactionWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method with an invalid key.
	 * Ensures a failure response is returned with the correct message.
	 */
	@Test
	public void testEditTransactionWithInvalidKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(INVALID_KEY, TRANSACTION_NAME, DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method with a null key. Ensures a
	 * failure response is returned with the correct message.
	 */
	@Test
	public void testEditTransactionWithNullKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(null, TRANSACTION_NAME, DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method with an empty key. Ensures
	 * a failure response is returned with the correct message.
	 */
	@Test
	public void testEditTransactionWithEmptyKey() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction("", TRANSACTION_NAME, DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_INVALID_KEY, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is an invalid
	 * date. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithInvalidDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        INVALID_DATE);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_INVALID_DATE, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is a null date.
	 * Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithNullDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME,
		        null);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is an empty
	 * date. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithEmptyDate() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), TRANSACTION_NAME, "");
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_MISSING_DATE, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is a null name.
	 * Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithNullName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), null,
		        DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is an empty
	 * name. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithEmptyName() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY), "", DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_MISSING_NAME, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionService#editTransaction(String, String, String)} method when there is an empty
	 * name. Ensures the response indicates failure and the proper message is returned.
	 */
	@Test
	public void testEditTransactionWithNameTooLong() {
		context.checking(new Expectations() {
			{
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));
			}
		});
	
		Response<Transaction> response = service.editTransaction(String.valueOf(TRANSACTION_KEY),
		        INVALID_TRANSACTION_NAME_TOO_LONG, DATE_FEB_12_2012);
	
		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));
	
		assertThat(response.getContent(), is(nullValue()));
	
		assertThat(MSG_NAME_TOO_LONG, isIn(response.getMessages()));
	
		context.assertIsSatisfied();
	}

}
