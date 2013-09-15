package net.sf.flophase.floweb.cashflow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import net.sf.flophase.floweb.account.FloAccountService;
import net.sf.flophase.floweb.common.Response;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.UserService;

/**
 * This class tests the {@link FloAccountService} class.
 */
public class FloCashFlowServiceTest {

	/**
	 * The message returned when permission is denied.
	 */
	private static final String MSG_PERMISSION_DENIED = "Permission denied";

	/**
	 * The mock context.
	 */
	private final Mockery context = new Mockery();

	/**
	 * The class under test.
	 */
	private FloCashFlowService service;

	/**
	 * The user service that the service depends upon.
	 */
	private UserService userService;

	/**
	 * The account store that the service depends upon.
	 */
	private CashFlowStore cashFlowStore;

	/**
	 * Sets up the test case. Creates mock user service and account store. Creates the service to be tested.
	 */
	@Before
	public void setUp() {
		userService = context.mock(UserService.class);
		cashFlowStore = context.mock(CashFlowStore.class);

		service = new FloCashFlowService(userService, cashFlowStore);
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlow()} method.
	 */
	@Test
	public void testGetCashFlow() {
		final CashFlow cashflow = new CashFlow();

		context.checking(new Expectations() {
			{
				// there is a user that is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(true));

				// return the list of accounts
				oneOf(cashFlowStore).getCashFlow();
				will(returnValue(cashflow));
			}
		});

		Response<CashFlow> response = service.getCashFlow();

		assertThat(response.getContent(), is(equalTo(cashflow)));

		context.assertIsSatisfied();
	}

	/**
	 * Tests the {@link FloCashFlowService#getCashFlow()} method when there is no logged in user.
	 */
	@Test
	public void testGetCashFlowWithNoLoggedInUser() {
		context.checking(new Expectations() {
			{
				// no user is logged in
				oneOf(userService).isUserLoggedIn();
				will(returnValue(false));
			}
		});

		Response<CashFlow> response = service.getCashFlow();

		assertThat(response.getResult(), is(equalTo(Response.RESULT_FAILURE)));

		assertThat(MSG_PERMISSION_DENIED, isIn(response.getMessages()));

		context.assertIsSatisfied();
	}
}
