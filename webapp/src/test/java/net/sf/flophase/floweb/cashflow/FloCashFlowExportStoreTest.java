package net.sf.flophase.floweb.cashflow;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.xaction.FinancialTransaction;
import net.sf.flophase.floweb.xaction.TransactionStore;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link FloCashFlowStore} class.
 */
public class FloCashFlowExportStoreTest {

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The store to be tested.
	 */
	private FloCashFlowExportStore store;

	/**
	 * The mock account store.
	 */
	private AccountStore accountStore;

	/**
	 * The mock transaction store.
	 */
	private TransactionStore xactionStore;

	/**
	 * Sets up the test case. Creates the mock user store and the cash flow data
	 * access object. Creates the store to be tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		accountStore = context.mock(AccountStore.class);
		xactionStore = context.mock(TransactionStore.class);

		store = new FloCashFlowExportStore(accountStore, xactionStore);
	}

	/**
	 * Tests the {@link FloCashFlowExportStore#getCashFlowExport()} method.
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
}
