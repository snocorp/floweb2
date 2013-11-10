package net.sf.flophase.floweb.account;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowStore;
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
 * This class tests the {@link FloAccountStore} class.
 */
public class FloAccountStoreTest {

	/**
	 * The account key.
	 */
	private static final long ACCOUNT_KEY = 2L;

	/**
	 * The cash flow key.
	 */
	private static final long CASHFLOW_KEY = 1L;

	/**
	 * The default account name.
	 */
	private static final String DEFAULT_ACCOUNT_NAME = "My Account";

	/**
	 * The account balance.
	 */
	private static final double ACCOUNT_BALANCE = 1.23;

	/**
	 * The account name.
	 */
	private static final String ACCOUNT_NAME = "Account1";

	/**
	 * The account negative threshold.
	 */
	private static final double ACCOUNT_NEGATIVE_THRESHOLD = 99;

	/**
	 * The account positive threshold.
	 */
	private static final double ACCOUNT_POSITIVE_THRESHOLD = 1000;

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
	 * The store to be tested.
	 */
	private FloAccountStore store;

	/**
	 * The account data access object.
	 */
	private AccountDAO accountDAO;

	/**
	 * The cash flow store.
	 */
	private CashFlowStore cashflowStore;

	/**
	 * Sets up the test case. Sets up the app engine helper. Creates the mock
	 * account data access object and cash flow store. Creates the store to be
	 * tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();

		accountDAO = context.mock(AccountDAO.class);

		cashflowStore = context.mock(CashFlowStore.class);

		store = new FloAccountStore(accountDAO,
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
	 * Tests the {@link FloAccountStore#getAccounts()} method. Ensures the
	 * account data access object is called with the appropriate parameters.
	 */
	@Test
	public void testGetAccounts() {
		final CashFlow cashflow = new CashFlow();
		final List<Account> expectedAccounts = new ArrayList<Account>();
		expectedAccounts.add(new Account(Key.create(CashFlow.class, 1),
				ACCOUNT_NAME, ACCOUNT_BALANCE));

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(accountDAO).getAccounts(with(equal(cashflow)));
				will(returnValue(expectedAccounts));
			}
		});

		List<Account> actualAccounts = store.getAccounts();

		assertEquals(expectedAccounts, actualAccounts);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountStore#getAccounts()} method. Ensures that when
	 * no accounts are returns, a default account is created and returned.
	 */
	@Test
	public void testGetAccountsWithNoAccounts() {
		final List<Account> expectedAccounts = new ArrayList<Account>();

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(new CashFlow()));

				oneOf(accountDAO).getAccounts(with(any(CashFlow.class)));
				will(returnValue(expectedAccounts));

				oneOf(accountDAO).createAccount(with(any(CashFlow.class)),
						with(equal(DEFAULT_ACCOUNT_NAME)), with(equal(0.0)),
						with(equal(0.0)), with(equal(0.0)));
				will(returnValue(new Account(Key.create(CashFlow.class, 1),
						DEFAULT_ACCOUNT_NAME, 0.0)));
			}
		});

		List<Account> actualAccounts = store.getAccounts();

		assertEquals(expectedAccounts, actualAccounts);

		assertEquals(1, actualAccounts.size());

		Account account = actualAccounts.get(0);

		assertEquals(DEFAULT_ACCOUNT_NAME, account.getName());
		assertEquals(0.0, account.getBalance(), 0.0);

		context.assertIsSatisfied();

	}

	/**
	 * Tests the {@link FloAccountStore#createAccount(String, double)} method.
	 * Ensures that the account data access object is called with the correct
	 * parameters.
	 */
	@Test
	public void testCreateAccount() {
		final CashFlow cashflow = getMockCashFlow();

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(new CashFlow()));

				oneOf(accountDAO).createAccount(with(any(CashFlow.class)),
						with(equal(ACCOUNT_NAME)),
						with(equal(ACCOUNT_BALANCE)), with(equal(0.0)),
						with(equal(0.0)));
				will(returnValue(new Account(cashflow.getKey(), ACCOUNT_NAME,
						ACCOUNT_BALANCE)));
			}
		});

		Account account = store.createAccount(ACCOUNT_NAME, ACCOUNT_BALANCE);

		assertEquals(ACCOUNT_NAME, account.getName());
		assertEquals(ACCOUNT_BALANCE, account.getBalance(), 0.0);

		context.assertIsSatisfied();
	}

	/**
	 * 
	 */
	@Test
	public void testDeleteAccount() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Account> key = Key.create(cashflow.getKey(), Account.class,
				ACCOUNT_KEY);

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(accountDAO).deleteAccount(with(equal(key)));
			}
		});

		store.deleteAccount(ACCOUNT_KEY);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the
	 * {@link FloAccountStore#editAccount(long, String, double, double, double)}
	 * method. Ensures the account data access object is called with the correct
	 * parameters.
	 */
	@Test
	public void testEditAccount() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Account> key = Key.create(cashflow.getKey(), Account.class,
				ACCOUNT_KEY);

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(accountDAO).editAccount(with(equal(key)),
						with(equal(ACCOUNT_NAME)),
						with(equal(ACCOUNT_BALANCE)),
						with(equal(ACCOUNT_NEGATIVE_THRESHOLD)),
						with(equal(ACCOUNT_POSITIVE_THRESHOLD)));
			}
		});

		store.editAccount(ACCOUNT_KEY, ACCOUNT_NAME, ACCOUNT_BALANCE,
				ACCOUNT_NEGATIVE_THRESHOLD, ACCOUNT_POSITIVE_THRESHOLD);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloAccountStore#getAccount(long)} method. Ensures the
	 * account data access object is called with the correct parameters.
	 */
	@Test
	public void testGetAccount() {
		final CashFlow cashflow = getMockCashFlow();
		final Key<Account> key = Key.create(cashflow.getKey(), Account.class,
				ACCOUNT_KEY);

		final Account expectedAccount = new Account(cashflow.getKey(),
				ACCOUNT_NAME, ACCOUNT_BALANCE);

		context.checking(new Expectations() {
			{
				allowing(cashflowStore).getCashFlow();
				will(returnValue(cashflow));

				oneOf(accountDAO).getAccount(key);
				will(returnValue(expectedAccount));
			}
		});

		Account account = store.getAccount(ACCOUNT_KEY);

		assertEquals(expectedAccount, account);

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
				return Key.create(CashFlow.class, CASHFLOW_KEY);
			}

		};
	}

}
