package net.sf.flophase.floweb.cashflow;

import static org.junit.Assert.*;
import net.sf.flophase.floweb.common.UserStore;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;

/**
 * This class tests the {@link FloCashFlowStore} class.
 */
public class FloCashFlowStoreTest {

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The store to be tested.
	 */
	private FloCashFlowStore store;

	/**
	 * The user store.
	 */
	private UserStore userStore;

	/**
	 * The cash flow data access object.
	 */
	private CashFlowDAO cashflowDAO;

	/**
	 * Sets up the test case. Creates the mock user store and the cash flow data access object. Creates the store to be
	 * tested.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		userStore = context.mock(UserStore.class);
		cashflowDAO = context.mock(CashFlowDAO.class);

		store = new FloCashFlowStore(userStore, cashflowDAO);
	}

	/**
	 * Tests the {@link FloCashFlowStore#getCashFlow()} method. Ensures the cash flow data access object is called with
	 * the correct parameters.
	 */
	@Test
	public void testGetCashFlow() {
		final User user = new User("email@example.com", "localhost");
		final CashFlow cashflow = new CashFlow(user);

		context.checking(new Expectations() {
			{
				oneOf(userStore).getUser();
				will(returnValue(user));

				oneOf(cashflowDAO).getCashFlow(user);
				will(returnValue(cashflow));
			}
		});

		CashFlow actualCashflow = store.getCashFlow();

		assertEquals(cashflow, actualCashflow);

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowStore#getCashFlow()} method when no cash flow already exists. Ensures the cash flow
	 * data access object is called with the correct parameters. Ensures a new cash flow is automatically created and
	 * returned.
	 */
	@Test
	public void testGetCashFlowWithNoCashFlow() {
		final User user = new User("email@example.com", "localhost");
		final CashFlow cashflow = new CashFlow(user);

		context.checking(new Expectations() {
			{
				oneOf(userStore).getUser();
				will(returnValue(user));

				oneOf(cashflowDAO).getCashFlow(user);
				will(returnValue(null));

				oneOf(cashflowDAO).createCashFlow(user);
				will(returnValue(cashflow));
			}
		});

		CashFlow actualCashflow = store.getCashFlow();

		assertEquals(cashflow, actualCashflow);

		context.assertIsSatisfied();
	}

}
