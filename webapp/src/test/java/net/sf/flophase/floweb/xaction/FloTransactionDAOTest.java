package net.sf.flophase.floweb.xaction;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.FloCashFlowDAO;
import net.sf.flophase.floweb.test.AbstractDAOTestCase;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This class tests the method of the {@link FloTransactionDAO} class that come
 * from {@link TransactionDAO}.
 */
public class FloTransactionDAOTest extends AbstractDAOTestCase {

	/**
	 * The data access object to be tested.
	 */
	private FloTransactionDAO dao;

	/**
	 * The cash flow.
	 */
	private CashFlow cashflow;

	/**
	 * Sets up the test case. Creates the data acces object.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		dao = new FloTransactionDAO();

		User user = UserServiceFactory.getUserService().getCurrentUser();

		cashflow = new FloCashFlowDAO().createCashFlow(user);
	}

	/**
	 * Tests the
	 * {@link FloTransactionDAO#createTransaction(CashFlow, String, Date)}
	 * method. Creates an account and validates it was created correctly.
	 */
	@Test
	public void testCreateTransaction() {
		Date date = new Date();
		Transaction xaction = dao.createTransaction(cashflow, "Transaction1",
				date);

		assertThat(xaction.getName(), is(equalTo("Transaction1")));
		assertThat(xaction.getDate(), is(equalTo(date)));

		xaction = dao.getTransaction(xaction.getKey());

		// make sure we got the account back out of the data store
		assertThat(xaction, is(not(nullValue())));
	}

	/**
	 * Tests the
	 * {@link FloTransactionDAO#deleteTransaction(com.googlecode.objectify.Key)}
	 * method. Adds a transaction to the data store then deletes it and ensures
	 * the account cannot be retrieved.
	 */
	@Test
	public void testDeleteTransaction() {
		Transaction xaction = new Transaction(cashflow.getKey(),
				"Transaction1", new Date());

		ofy().save().entity(xaction).now();

		dao.deleteTransaction(xaction.getKey()).now();

		assertNull(dao.getTransaction(xaction.getKey()));
	}

	/**
	 * Tests the
	 * {@link FloTransactionDAO#editTransaction(com.googlecode.objectify.Key, String, Date)}
	 * method. Adds a transaction to the data store then updates it and ensures
	 * the details are updated.
	 */
	@Test
	public void testEditTransaction() {
		Transaction xaction = new Transaction(cashflow.getKey(),
				"Transaction1", new Date(0));

		ofy().save().entity(xaction).now();

		Date date = new Date();
		Transaction updatedTransaction = dao.editTransaction(xaction.getKey(),
				"Transaction2", date);

		assertThat(updatedTransaction.getName(), is(equalTo("Transaction2")));
		assertThat(updatedTransaction.getDate(), is(equalTo(date)));

		updatedTransaction = dao.getTransaction(xaction.getKey());

		assertThat(updatedTransaction.getName(), is(equalTo("Transaction2")));
		assertThat(updatedTransaction.getDate(), is(equalTo(date)));
	}

	/**
	 * Tests the {@link FloTransactionDAO#getTransactions(CashFlow, Date)}
	 * method. Adds three transactions into different months and queries each
	 * month to ensure only the proper transaction is returned.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testGetTransactionsByMonth() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MONTH, -1);
		Date oneMonthAgo = calendar.getTime();

		Transaction xaction1 = new Transaction(cashflow.getKey(),
				"Transaction1", oneMonthAgo);

		calendar.add(Calendar.MONTH, 1);
		Date currentDate = calendar.getTime();

		Transaction xaction2 = new Transaction(cashflow.getKey(),
				"Transaction2", currentDate);

		calendar.add(Calendar.MONTH, 1);
		Date oneMonthFromNow = calendar.getTime();

		Transaction xaction3 = new Transaction(cashflow.getKey(),
				"Transaction3", oneMonthFromNow);

		// store the transactions
		ofy().save().entities(xaction1, xaction2, xaction3).now();

		// one month ago
		Date startDate = format.parse(format.format(oneMonthAgo));
		List<Transaction> xactions = dao.getTransactions(cashflow, startDate);

		assertThat(xactions.size(), is(equalTo(1)));
		assertThat(xactions.get(0).getKey(), is(equalTo(xaction1.getKey())));

		// this month
		startDate = format.parse(format.format(currentDate));
		xactions = dao.getTransactions(cashflow, startDate);

		assertThat(xactions.size(), is(equalTo(1)));
		assertThat(xactions.get(0).getKey(), is(equalTo(xaction2.getKey())));

		// next month
		startDate = format.parse(format.format(oneMonthFromNow));
		xactions = dao.getTransactions(cashflow, startDate);

		assertThat(xactions.size(), is(equalTo(1)));
		assertThat(xactions.get(0).getKey(), is(equalTo(xaction3.getKey())));
	}

	/**
	 * Tests the {@link FloTransactionDAO#getTransactions(CashFlow)} method.
	 * Adds three transactions into different months and queries each month to
	 * ensure all transactions are returned.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testGetTransactions() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MONTH, -1);
		Date oneMonthAgo = calendar.getTime();

		Transaction xaction1 = new Transaction(cashflow.getKey(),
				"Transaction1", oneMonthAgo);

		calendar.add(Calendar.MONTH, 1);
		Date currentDate = calendar.getTime();

		Transaction xaction2 = new Transaction(cashflow.getKey(),
				"Transaction2", currentDate);

		calendar.add(Calendar.MONTH, 1);
		Date oneMonthFromNow = calendar.getTime();

		Transaction xaction3 = new Transaction(cashflow.getKey(),
				"Transaction3", oneMonthFromNow);

		// store the transactions
		ofy().save().entities(xaction1, xaction2, xaction3).now();

		// one month ago
		List<Transaction> xactions = dao.getTransactions(cashflow);

		assertThat(xactions.size(), is(equalTo(3)));

		assertThat(xactions.get(0).getKey(), is(equalTo(xaction1.getKey())));

		assertThat(xactions.get(1).getKey(), is(equalTo(xaction2.getKey())));

		assertThat(xactions.get(2).getKey(), is(equalTo(xaction3.getKey())));
	}

	/**
	 * Tests the
	 * {@link FloTransactionDAO#getTransaction(com.googlecode.objectify.Key)}
	 * method. Adds a transaction to the data store then retrieves it and
	 * ensures the details are correct.
	 */
	@Test
	public void testGetTransaction() {
		Transaction xaction = new Transaction(cashflow.getKey(),
				"Transaction1", new Date());

		ofy().save().entity(xaction).now();

		Transaction transaction = dao.getTransaction(xaction.getKey());

		assertThat(transaction.getKey(), is(equalTo(xaction.getKey())));
		assertThat(transaction.getName(), is(equalTo(xaction.getName())));
		assertThat(transaction.getDate(), is(equalTo(xaction.getDate())));
	}

}
