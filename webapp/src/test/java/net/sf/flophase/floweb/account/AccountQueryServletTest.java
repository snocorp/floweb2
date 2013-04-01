package net.sf.flophase.floweb.account;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link AccountQueryServlet} class.
 */
public class AccountQueryServletTest extends AbstractServletTest {

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
	public static final String SERVLET_PATH = "/account-q";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final AccountService mockAccountService = testServletContextListener
				.getModule().getAccountService();

		final List<Account> accountList = new ArrayList<Account>();
		accountList.add(new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE));

		final Response<List<Account>> response = new Response<List<Account>>(
				Response.RESULT_SUCCESS, accountList);

		context.checking(new Expectations() {
			{
				one(mockAccountService).getAccounts();
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":[{\"name\":\""
				+ ACCOUNT_NAME
				+ "\",\"balance\":"
				+ ACCOUNT_BALANCE
				+ ",\"negativeThreshold\":0.0,\"positiveThreshold\":0.0}]}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return AccountQueryServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

}
