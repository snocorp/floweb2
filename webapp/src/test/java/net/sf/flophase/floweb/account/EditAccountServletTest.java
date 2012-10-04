package net.sf.flophase.floweb.account;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link EditAccountServlet} class.
 */
public class EditAccountServletTest extends AbstractServletTest {

	/**
	 * The account key.
	 */
	private static final String ACCOUNT_KEY = "1";

	/**
	 * The account balance.
	 */
	private static final double ACCOUNT_BALANCE = 1.23;

	/**
	 * The account name.
	 */
	private static final String ACCOUNT_NAME = "Account1";
	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/account-add";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final AccountService mockAccountService = testServletContextListener.getModule().getAccountService();

		final Account account = new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE);

		final Response<Account> response = new Response<Account>(Response.RESULT_SUCCESS, account);

		context.checking(new Expectations() {
			{
				one(mockAccountService).editAccount(ACCOUNT_KEY, ACCOUNT_NAME, String.valueOf(ACCOUNT_BALANCE));
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"name\":\"" + ACCOUNT_NAME
		        + "\",\"balance\":" + String.valueOf(ACCOUNT_BALANCE) + "}}");
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?key=" + ACCOUNT_KEY + "&name=" + ACCOUNT_NAME + "&balance=" + String.valueOf(ACCOUNT_BALANCE);
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return EditAccountServlet.class;
	}

}