package net.sf.flophase.floweb.cashflow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import net.sf.flophase.floweb.user.UserStore;

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
	 * The number that are done.
	 */
	private static final int DONE = 1;

	/**
	 * The total number to be done.
	 */
	private static final int TOTAL = 2;

	/**
	 * The import status id.
	 */
	private static final long STATUS_ID = 123456L;

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
	 * Sets up the test case. Creates the mock user store and the cash flow data
	 * access object. Creates the store to be tested.
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
	 * Tests the {@link FloCashFlowStore#getCashFlow()} method. Ensures the cash
	 * flow data access object is called with the correct parameters.
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
	 * Tests the {@link FloCashFlowStore#getCashFlow()} method when no cash flow
	 * already exists. Ensures the cash flow data access object is called with
	 * the correct parameters. Ensures a new cash flow is automatically created
	 * and returned.
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

	/**
	 * Tests that the status is correctly updated and the data access object is
	 * called correctly.
	 */
	@Test
	public void testUpdateCashFlowImportStatus() {
		final CashFlowImportStatus status = new CashFlowImportStatus(null, 0);

		context.checking(new Expectations() {
			{
				oneOf(cashflowDAO).updateCashFlowImportStatus(status);
			}
		});

		store.updateCashFlowImportStatus(status, DONE, TOTAL);

		assertThat(status.getDone(), is(equalTo(DONE)));
		assertThat(status.getTotal(), is(equalTo(TOTAL)));

		context.assertIsSatisfied();
	}

	/**
	 * Test that a status is properly created via the data access object.
	 */
	@Test
	public void testCreateCashFlowImportStatus() {
		final User user = new User("email@example.com", "localhost");
		final CashFlow cashflow = new CashFlow(user);

		final CashFlowImportStatus status = new CashFlowImportStatus(null, 0);

		context.checking(new Expectations() {
			{
				oneOf(userStore).getUser();
				will(returnValue(user));

				oneOf(cashflowDAO).getCashFlow(user);
				will(returnValue(cashflow));

				oneOf(cashflowDAO).createCashFlowImport(cashflow, -1);
				will(returnValue(status));
			}
		});

		CashFlowImportStatus createdStatus = store.createCashFlowImportStatus();

		assertThat(createdStatus, is(equalTo(status)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests that the import status is properly retrieved using the data access
	 * object.
	 */
	@Test
	public void testGetCashFlowImportStatus() {
		final User user = new User("email@example.com", "localhost");
		final CashFlow cashflow = new CashFlow(user);

		final CashFlowImportStatus expectedStatus = new CashFlowImportStatus(
				null, 0);

		context.checking(new Expectations() {
			{
				oneOf(userStore).getUser();
				will(returnValue(user));

				oneOf(cashflowDAO).getCashFlow(user);
				will(returnValue(cashflow));

				oneOf(cashflowDAO).getCashFlowImportStatus(cashflow, STATUS_ID);
				will(returnValue(expectedStatus));
			}
		});

		CashFlowImportStatus status = store.getCashFlowImportStatus(STATUS_ID);

		assertThat(expectedStatus, is(equalTo(status)));

		context.assertIsSatisfied();
	}

}
