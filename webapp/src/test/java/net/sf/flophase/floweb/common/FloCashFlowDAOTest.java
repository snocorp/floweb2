package net.sf.flophase.floweb.common;

import static org.junit.Assert.*;
import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowDAO;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This class tests the method of the {@link FloDAO} class that come from {@link CashFlowDAO}.
 */
public class FloCashFlowDAOTest extends AbstractDAOTestCase {

	/**
	 * The data access object to be tested.
	 */
	private FloDAO dao;

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

		dao = new FloDAO();
	}

	/**
	 * Tests the {@link FloDAO#getCashFlow(User)} method. Adds a new cash flow using the Objectify engine and loads it
	 * back using the data access object.
	 */
	@Test
	public void testGetCashFlow() {
		User user = UserServiceFactory.getUserService().getCurrentUser();

		CashFlow actualCashFlow = dao.getCashFlow(user);

		// ensure there is no cash flow at first
		assertNull(actualCashFlow);

		// add a cash flow
		CashFlow expectedCashFlow = new CashFlow(user);
		dao.ofy().put(expectedCashFlow);

		// get the cash flow again
		actualCashFlow = dao.getCashFlow(user);

		// ensure the cash flows match
		assertEquals(expectedCashFlow.getKey(), actualCashFlow.getKey());
		assertEquals(user, actualCashFlow.getUser());
	}

	/**
	 * Tests the {@link FloDAO#createCashFlow(User)} method. Adds a new cash flow using the data access object.
	 */
	@Test
	public void testCreateCashFlow() {
		User user = UserServiceFactory.getUserService().getCurrentUser();

		CashFlow cashflow = dao.createCashFlow(user);

		// ensure the cash flows match
		assertEquals(dao.getCashFlow(user).getKey(), cashflow.getKey());
	}

	/**
	 * Tests the {@link FloDAO#createCashFlow(User)} method. Adds two new cash flows using the data access object.
	 */
	@Test
	public void testCreateTwoCashFlows() {
		User user1 = UserServiceFactory.getUserService().getCurrentUser();

		User user2 = new User("email2@example.com", "localhost");

		CashFlow cashflow1 = dao.createCashFlow(user1);

		// ensure the cash flows match
		assertEquals(dao.getCashFlow(user1).getKey(), cashflow1.getKey());

		assertNull(dao.getCashFlow(user2));

		CashFlow cashflow2 = dao.createCashFlow(user2);

		// ensure the cash flows match
		assertEquals(dao.getCashFlow(user2).getKey(), cashflow2.getKey());

		// ensure the two cashflows are different
		assertThat(cashflow2.getKey(), Matchers.is(Matchers.not(Matchers.equalTo(cashflow1.getKey()))));

	}

}