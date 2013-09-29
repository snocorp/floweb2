package net.sf.flophase.floweb.entry;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.xaction.Transaction;
import net.sf.flophase.floweb.xaction.TransactionStore;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link FloEntryStore} class.
 */
public class FloEntryStoreTest {

	/**
	 * The cash flow id.
	 */
	private static final long CASHFLOW_ID = 1L;

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
	 * The class to be tested.
	 */
	private FloEntryStore store;

	/**
	 * The entry data access object.
	 */
	private EntryDAO dao;

	/**
	 * The account store.
	 */
	private AccountStore accountStore;

	/**
	 * The transaction store.
	 */
	private TransactionStore xactionStore;

	/**
	 * Sets up the test case. Sets up the app engine helper. Creates mock entry
	 * data access object and account, transaction and cash flow stores. Creates
	 * the store to be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();

		dao = context.mock(EntryDAO.class);
		accountStore = context.mock(AccountStore.class);
		xactionStore = context.mock(TransactionStore.class);

		store = new FloEntryStore(dao, accountStore, xactionStore);
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
	 * Tests the {@link FloEntryStore#editEntry(long, long, double)} method.
	 * Ensures the data access object is called with the correct parameters.
	 */
	@Test
	public void testEditEntry() {
		final CashFlow cashflow = getMockCashFlow();
		final Account account = getMockAccount(cashflow.getKey());
		final Transaction xaction = getMockTransaction(cashflow.getKey());

		final Entry expectedEntry = new Entry();

		context.checking(new Expectations() {
			{
				oneOf(accountStore).getAccount(4L);
				will(returnValue(account));

				oneOf(xactionStore).getTransaction(5L);
				will(returnValue(xaction));

				oneOf(dao).editEntry(xaction, account.getKey().getId(), 1.23);
				will(returnValue(expectedEntry));
			}
		});

		Entry entry = store.editEntry(4L, 5L, 1.23);

		assertEquals(expectedEntry, entry);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link EntryStore#getEntries(Transaction)} method. Ensures the
	 * data access object is called and the proper result is returned.
	 */
	@Test
	public void testGetEntries() {
		final CashFlow cashflow = getMockCashFlow();
		final Transaction xaction = getMockTransaction(cashflow.getKey());

		final Map<Key<Account>, Entry> expectedEntries = new HashMap<Key<Account>, Entry>();

		context.checking(new Expectations() {
			{
				oneOf(dao).getEntries(xaction);
				will(returnValue(expectedEntries));
			}
		});

		Map<Long, Entry> entries = store.getEntries(xaction);

		assertEquals(expectedEntries, entries);

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

	/**
	 * Builds and returns a mock account to avoid building a real key.
	 * 
	 * @param cashflow
	 *            The cash flow key for the account
	 * @return A mock account
	 */
	private Account getMockAccount(final Key<CashFlow> cashflow) {
		return new Account() {

			@Override
			public Key<Account> getKey() {
				return Key.create(cashflow, Account.class, 2L);
			}

		};
	}

	/**
	 * Builds and returns a mock transaction to avoid building a real key.
	 * 
	 * @param cashflow
	 *            The cash flow key for the transaction
	 * @return A mock transaction
	 */
	private Transaction getMockTransaction(final Key<CashFlow> cashflow) {
		return new Transaction() {

			@Override
			public Key<Transaction> getKey() {
				return Key.create(cashflow, Transaction.class, 3L);
			}

		};
	}

}
