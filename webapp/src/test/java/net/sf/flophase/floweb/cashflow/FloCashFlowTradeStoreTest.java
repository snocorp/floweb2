package net.sf.flophase.floweb.cashflow;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryStore;
import net.sf.flophase.floweb.xaction.FinancialTransaction;
import net.sf.flophase.floweb.xaction.Transaction;
import net.sf.flophase.floweb.xaction.TransactionStore;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link FloCashFlowTradeStore} class.
 */
public class FloCashFlowTradeStoreTest {

	/**
	 * The account balance.
	 */
	private static final double ACCOUNT_BALANCE = 1.23;

	/**
	 * The account name.
	 */
	private static final String ACCOUNT_NAME = "Account1";

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * The account id for the entry.
	 */
	private static final Long ACCOUNT_ID = 12345678L;

	/**
	 * The new account id for the entry.
	 */
	private static final Long NEW_ACCOUNT_ID = 87654321L;

	/**
	 * The new transaction id for the entry.
	 */
	private static final Long NEW_TRANSACTION_ID = 9999L;

	/**
	 * The entry amount.
	 */
	private static final double ENTRY_AMOUNT = 12.34;

	/**
	 * App engine helper for when we need a key.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The store to be tested.
	 */
	private FloCashFlowTradeStore store;

	/**
	 * The mock account store.
	 */
	private AccountStore accountStore;

	/**
	 * The mock transaction store.
	 */
	private TransactionStore xactionStore;

	/**
	 * The mock cash flow store.
	 */
	private CashFlowStore cashflowStore;

	/**
	 * The mock entry store
	 */
	private EntryStore entryStore;

	/**
	 * Sets up the test case. Creates the mock user store and the cash flow data
	 * access object. Creates the store to be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();

		cashflowStore = context.mock(CashFlowStore.class);
		accountStore = context.mock(AccountStore.class);
		xactionStore = context.mock(TransactionStore.class);
		entryStore = context.mock(EntryStore.class);

		store = new FloCashFlowTradeStore(cashflowStore, accountStore,
				xactionStore, entryStore);
	}

	/**
	 * Tears down the test case.
	 */
	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * Tests the {@link FloCashFlowTradeStore#getCashFlowExport()} method.
	 * Ensures the data access objects are called with the correct parameters.
	 */
	@Test
	public void testGetCashFlowExport() {
		final List<Account> accounts = new ArrayList<Account>();
		final List<FinancialTransaction> xactions = new ArrayList<FinancialTransaction>();

		context.checking(new Expectations() {
			{
				oneOf(accountStore).getAccounts();
				will(returnValue(accounts));

				oneOf(xactionStore).getTransactions();
				will(returnValue(xactions));
			}
		});

		CashFlowExport actualCashflow = store.getCashFlowExport();

		assertEquals(accounts, actualCashflow.getAccounts());
		assertEquals(xactions, actualCashflow.getTransactions());

		context.assertIsSatisfied();
	}

	/**
	 * Tests that a cash flow export can be imported and that the status is
	 * updated properly.
	 */
	@Test
	public void testImportCashFlow() {

		final CashFlowImportStatus status = new CashFlowImportStatus();

		final List<Account> accountList = new ArrayList<Account>();
		accountList.add(new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE) {

			@Override
			public Key<Account> getKey() {
				return Key.create(Account.class, ACCOUNT_ID);
			}

		});

		final Account createdAccount = new Account(null, ACCOUNT_NAME,
				ACCOUNT_BALANCE) {

			@Override
			public Key<Account> getKey() {
				return Key.create(Account.class, NEW_ACCOUNT_ID);
			}

		};

		final Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);

		Transaction xaction = new Transaction(null, TRANSACTION_NAME,
				calendar.getTime());

		Map<Long, Entry> entryMap = new HashMap<Long, Entry>();
		entryMap.put(ACCOUNT_ID, new Entry(null, ACCOUNT_ID, ENTRY_AMOUNT));

		final List<FinancialTransaction> xactionList = new ArrayList<FinancialTransaction>();
		xactionList.add(new FinancialTransaction(xaction, entryMap));

		final Transaction createdTransaction = new Transaction(null,
				TRANSACTION_NAME, calendar.getTime()) {

			@Override
			public Key<Transaction> getKey() {
				return Key.create(Transaction.class, NEW_TRANSACTION_ID);
			}

		};

		CashFlowExport cashflowExport = new CashFlowExport();
		cashflowExport.setAccounts(accountList);
		cashflowExport.setTransactions(xactionList);

		final Sequence updateSequence = context.sequence("updateSequence");

		context.checking(new Expectations() {
			{
				oneOf(cashflowStore).updateCashFlowImportStatus(status, 0, 2);
				inSequence(updateSequence);

				oneOf(accountStore).createAccount(ACCOUNT_NAME,
						ACCOUNT_BALANCE, 0.0, 0.0);
				will(returnValue(createdAccount));

				oneOf(cashflowStore).updateCashFlowImportStatus(status, 1, 2);
				inSequence(updateSequence);

				oneOf(xactionStore).createTransaction(TRANSACTION_NAME,
						calendar.getTime());
				will(returnValue(createdTransaction));

				oneOf(entryStore).editEntry(NEW_ACCOUNT_ID, NEW_TRANSACTION_ID,
						ENTRY_AMOUNT);

				oneOf(cashflowStore).updateCashFlowImportStatus(status, 2, 2);
				inSequence(updateSequence);
			}
		});

		store.importCashFlow(status, cashflowExport);

		context.assertIsSatisfied();
	}
}
