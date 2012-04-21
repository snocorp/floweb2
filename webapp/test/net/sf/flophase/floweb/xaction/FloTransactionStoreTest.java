package net.sf.flophase.floweb.xaction;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowStore;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryStore;

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
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * The cash flow id.
	 */
	private static final long CASHFLOW_ID = 1L;

	/**
	 * The transaction id.
	 */
	private static final long TRANSACTION_ID = 2L;

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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
	 * Sets up the test case. Sets up the app engine helper. Creates mock transaction data access object and cash flow
	 * store. Creates the store to be tested.
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

		store = new FloTransactionStore(xactionDAO, entryStore, cashflowStore);
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
	 * Tests the {@link FloTransactionStore#createTransaction(String, Date)} method. Ensures that the transaction data
	 * access object is called with the expected parameters.
	 */
	@Test
	public void testCreateTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Date date = new Date();

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				one(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				one(xactionDAO).createTransaction(cashflow, TRANSACTION_NAME, date);
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.createTransaction(TRANSACTION_NAME, date);

		assertThat(transaction, is(equalTo(xaction)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#deleteTransaction(long)} method. Ensures the transaction data access object
	 * is called with the correct parameters.
	 */
	@Test
	public void testDeleteTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Transaction> key = new Key<Transaction>(cashflow.getKey(), Transaction.class, TRANSACTION_ID);

		context.checking(new Expectations() {
			{
				one(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				one(xactionDAO).deleteTransaction(with(equal(key)));
			}
		});

		store.deleteTransaction(TRANSACTION_ID);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#editTransaction(long, String, Date)} method. Ensures the transaction data
	 * access object is called with the correct parameters.
	 */
	@Test
	public void testEditTransaction() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Transaction> key = new Key<Transaction>(cashflow.getKey(), Transaction.class, TRANSACTION_ID);
		final Date date = new Date();

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				one(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				one(xactionDAO).editTransaction(with(equal(key)), with(equal(TRANSACTION_NAME)), with(equal(date)));
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.editTransaction(TRANSACTION_ID, TRANSACTION_NAME, date);

		assertThat(transaction, is(equalTo(xaction)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#getTransactions(Date)} method. Ensures the transaction data access object is
	 * called with the correct parameters.
	 */
	@Test
	public void testGetTransactions() {
		final CashFlow cashflow = new CashFlow();
		final Date date = new Date();
		final Transaction transaction = new Transaction(null, TRANSACTION_NAME, date);

		final List<Transaction> xactions = new ArrayList<Transaction>();
		xactions.add(transaction);

		context.checking(new Expectations() {
			{
				one(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				one(xactionDAO).getTransactions(cashflow, date);
				will(returnValue(xactions));

				one(entryStore).getEntries(transaction);
				will(returnValue(new HashMap<Key<Account>, Entry>()));
			}
		});

		List<FinancialTransaction> transactions = store.getTransactions(date);

		for (int i = 0; i < transactions.size(); i++) {
			assertThat(transactions.get(i).getDetails(), is(equalTo(xactions.get(i))));
		}

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloTransactionStore#getTransaction(long)} method. Ensure the transaction data access object is
	 * called with the correct parameters.
	 */
	@Test
	public void testGetTransaction() {
		final CashFlow cashflow = getMockCashFlow();

		final Key<Transaction> key = new Key<Transaction>(cashflow.getKey(), Transaction.class, TRANSACTION_ID);

		final Transaction xaction = new Transaction();

		context.checking(new Expectations() {
			{
				one(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				one(xactionDAO).getTransaction(key);
				will(returnValue(xaction));
			}
		});

		Transaction transaction = store.getTransaction(TRANSACTION_ID);

		assertThat(transaction, is(equalTo(xaction)));

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
				return new Key<CashFlow>(CashFlow.class, CASHFLOW_ID);
			}

		};
	}

}
