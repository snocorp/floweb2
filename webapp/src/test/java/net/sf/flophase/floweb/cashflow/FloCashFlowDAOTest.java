package net.sf.flophase.floweb.cashflow;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import net.sf.flophase.floweb.test.AbstractDAOTestCase;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This class tests the method of the {@link FloCashFlowDAO} class that come
 * from {@link CashFlowDAO}.
 */
public class FloCashFlowDAOTest extends AbstractDAOTestCase {

	/**
	 * The data access object to be tested.
	 */
	private FloCashFlowDAO dao;

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

		dao = new FloCashFlowDAO();
	}

	/**
	 * Tests the {@link FloCashFlowDAO#getCashFlow(User)} method. Adds a new
	 * cash flow using the Objectify engine and loads it back using the data
	 * access object.
	 */
	@Test
	public void testGetCashFlow() {
		User user = UserServiceFactory.getUserService().getCurrentUser();

		CashFlow actualCashFlow = dao.getCashFlow(user);

		// ensure there is no cash flow at first
		assertNull(actualCashFlow);

		// add a cash flow
		CashFlow expectedCashFlow = new CashFlow(user);
		ofy().save().entity(expectedCashFlow).now();

		// get the cash flow again
		actualCashFlow = dao.getCashFlow(user);

		// ensure the cash flows match
		assertEquals(expectedCashFlow.getKey(), actualCashFlow.getKey());
		assertEquals(user, actualCashFlow.getUser());
	}

	/**
	 * Tests the {@link FloCashFlowDAO#createCashFlow(User)} method. Adds a new
	 * cash flow using the data access object.
	 */
	@Test
	public void testCreateCashFlow() {
		User user = UserServiceFactory.getUserService().getCurrentUser();

		CashFlow cashflow = dao.createCashFlow(user);

		// ensure the cash flows match
		assertEquals(dao.getCashFlow(user).getKey(), cashflow.getKey());
	}

	/**
	 * Tests the {@link FloCashFlowDAO#createCashFlow(User)} method. Adds two
	 * new cash flows using the data access object.
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
		assertThat(cashflow2.getKey(), is(not(equalTo(cashflow1.getKey()))));

	}

	/**
	 * Test creation of a cash flow import status.
	 */
	@Test
	public void testCreateCashFlowImportStatus() {
		User user = UserServiceFactory.getUserService().getCurrentUser();

		CashFlow cashflow = dao.createCashFlow(user);

		CashFlowImportStatus createdStatus = dao.createCashFlowImportStatus(
				cashflow, -1);

		CashFlowImportStatus loadedStatus = dao.getCashFlowImportStatus(
				cashflow, createdStatus.getKey().getId());

		assertThat(loadedStatus.getDone(), is(equalTo(0)));
		assertThat(loadedStatus.getTotal(), is(equalTo(-1)));
	}

	/**
	 * Tests that updating the status persists the values properly.
	 */
	@Test
	public void testUpdateCashFlowImportStatus() {
		User user = UserServiceFactory.getUserService().getCurrentUser();
		CashFlow cashflow = dao.createCashFlow(user);
		CashFlowImportStatus status = dao.createCashFlowImportStatus(cashflow,
				-1);

		status.setDone(1);
		status.setTotal(2);

		dao.updateCashFlowImportStatus(status).now();

		CashFlowImportStatus loadedStatus = dao.getCashFlowImportStatus(
				cashflow, status.getKey().getId());

		assertThat(loadedStatus.getDone(), is(equalTo(1)));
		assertThat(loadedStatus.getTotal(), is(equalTo(2)));
	}

	/**
	 * Tests that deleting the status removes the entity properly.
	 */
	@Test
	public void testDeleteCashFlowImportStatus() {
		User user = UserServiceFactory.getUserService().getCurrentUser();
		CashFlow cashflow = dao.createCashFlow(user);
		CashFlowImportStatus status = dao.createCashFlowImportStatus(cashflow,
				-1);

		dao.deleteCashFlowImportStatus(status).now();

		CashFlowImportStatus loadedStatus = dao.getCashFlowImportStatus(
				cashflow, status.getKey().getId());

		assertThat(loadedStatus, is(nullValue(CashFlowImportStatus.class)));
	}

}
