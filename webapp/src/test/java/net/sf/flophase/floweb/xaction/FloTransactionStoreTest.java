package net.sf.flophase.floweb.xaction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowStore;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryStore;
import net.sf.flophase.floweb.test.MockProvider;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link FloTransactionStore} class.
 */
public class FloTransactionStoreTest {

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME_1 = "Transaction1";

	/**
	 * The second transaction name.
	 */
	private static final String TRANSACTION_NAME_2 = "Transaction2";

	/**
	 * The cash flow id.
	 */
	private static final long CASHFLOW_ID = 1L;

	/**
	 * The transaction id.
	 */
	private static final long TRANSACTION_ID_1 = 2L;

	/**
	 * The transaction id.
	 */
	private static final long TRANSACTION_ID_2 = 4L;

	/**
	 * The account id.
	 */
	private static final long ACCOUNT_ID = 3L;

	/**
	 * The entry amount.
	 */
	private static final double ENTRY_AMOUNT = 1.23;

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	/**
	 * The object under test.
	 */
	private FloTransactionStore store;

	/**
	 * The transaction data access object.
	 */
	private TransactionDAO xactionDAO;

	/**
	 * The cash flow store.
	 */
	private CashFlowStore cashflowStore;

	/**
	 * The entry store.
	 */
	private EntryStore entryStore;

	/**
	 * Sets up the test case. Sets up the app engine helper. Creates mock
	 * transaction data access object and cash flow store. Creates the store to
	 * be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();

		xactionDAO = context.mock(TransactionDAO.class);
		entryStore = context.mock(EntryStore.class);
		cashflowStore = context.mock(CashFlowStore.class);

		store = new FloTransactionStore(xactionDAO, entryStore,
				new MockProvider<CashFlowStore>(cashflowStore));
	}

	/**
	 * Tears down the test case. Tears down the app engine helper.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	/**
	 * Tests the {@link FloTransactionStore#createTransaction(String, Date)}
	 * method. Ensures that the transaction data access object is called with
	 * the expected parameters.
	 */
	@Test
	public void testCreateTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Date date = new Date();

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).createTransaction(cashflow,
						TRANSACTION_NAME_1, date);
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.createTransaction(TRANSACTION_NAME_1,
				date);

		assertThat(transaction, is(equalTo(xaction)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#deleteTransaction(long)} method.
	 * Ensures the transaction data access object is called with the correct
	 * parameters.
	 */
	@Test
	public void testDeleteTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Transaction> key = Key.create(cashflow.getKey(),
				Transaction.class, TRANSACTION_ID_1);

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).deleteTransaction(with(equal(key)));
			}
		});

		store.deleteTransaction(TRANSACTION_ID_1);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#editTransaction(long, String, Date)}
	 * method. Ensures the transaction data access object is called with the
	 * correct parameters.
	 */
	@Test
	public void testEditTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Transaction> key = Key.create(cashflow.getKey(),
				Transaction.class, TRANSACTION_ID_1);
		final Date date = new Date();

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).editTransaction(with(equal(key)),
						with(equal(TRANSACTION_NAME_1)), with(equal(date)));
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.editTransaction(TRANSACTION_ID_1,
				TRANSACTION_NAME_1, date);

		assertThat(transaction, is(equalTo(xaction)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#getTransactions(Date)} method.
	 * Ensures the transaction data access object is called with the correct
	 * parameters.
	 */
	@Test
	public void testGetTransactions() {
		final CashFlow cashflow = new CashFlow();
		final Date date = new Date();
		final Transaction transaction = new Transaction(null,
				TRANSACTION_NAME_1, date);

		final List<Transaction> xactions = new ArrayList<Transaction>();
		xactions.add(transaction);

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).getTransactions(cashflow, date);
				will(returnValue(xactions));

				oneOf(entryStore).getEntries(transaction);
				will(returnValue(new HashMap<Long, Entry>()));
			}
		});

		List<FinancialTransaction> transactions = store.getTransactions(date);

		for (int i = 0; i < transactions.size(); i++) {
			assertThat(transactions.get(i).getDetails(),
					is(equalTo(xactions.get(i))));
		}

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#getTransaction(long)} method. Ensure
	 * the transaction data access object is called with the correct parameters.
	 */
	@Test
	public void testGetTransaction() {
		final CashFlow cashflow = getMockCashFlow();

		final Key<Transaction> key = Key.create(cashflow.getKey(),
				Transaction.class, TRANSACTION_ID_1);

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).getTransaction(key);
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.getTransaction(TRANSACTION_ID_1);

		assertThat(transaction, is(equalTo(xaction)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that copying a transaction creates a new version of the transaction
	 * at the given date with the given name that has the same entries.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testCopyTransaction() throws Exception {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Transaction> origKey = Key.create(cashflow.getKey(),
				Transaction.class, TRANSACTION_ID_1);
		final Key<Transaction> copyKey = Key.create(cashflow.getKey(),
				Transaction.class, TRANSACTION_ID_2);
		final Date date = new Date();

		final Transaction original = new Transaction();
		final Map<Long, Entry> origEntries = new HashMap<Long, Entry>();
		origEntries.put(ACCOUNT_ID,
				new Entry(origKey, ACCOUNT_ID, ENTRY_AMOUNT));

		final Transaction copy = new Transaction() {

			@Override
			public Key<Transaction> getKey() {
				return copyKey;
			}

		};
		final Map<Long, Entry> copyEntries = new HashMap<Long, Entry>();
		copyEntries.put(ACCOUNT_ID,
				new Entry(copyKey, ACCOUNT_ID, ENTRY_AMOUNT));

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(xactionDAO).getTransaction(origKey);
				will(returnValue(original));

				oneOf(entryStore).getEntries(original);
				will(returnValue(origEntries));

				oneOf(xactionDAO).createTransaction(cashflow,
						TRANSACTION_NAME_2, date);
				will(returnValue(copy));

				oneOf(entryStore).editEntry(ACCOUNT_ID, TRANSACTION_ID_2,
						ENTRY_AMOUNT);

				oneOf(entryStore).getEntries(copy);
				will(returnValue(copyEntries));
			}
		});

		FinancialTransaction actualCopy = store.copyTransaction(
				TRANSACTION_ID_1, TRANSACTION_NAME_2, date);

		assertThat(actualCopy.getDetails().getKey(), is(equalTo(copyKey)));
		assertThat(actualCopy.getEntries(), is(equalTo(copyEntries)));

		context.assertIsSatisfied();
	}

	/**
	 * Builds and returns a mock cash flow to avoid building a real key.
	 * 
	 * @return A mock cash flow
	 */
	private CashFlow getMockCashFlow() {
		return new CashFlow() {

			@Override
			public Key<CashFlow> getKey() {
				return Key.create(CashFlow.class, CASHFLOW_ID);
			}

		};
	}

}
