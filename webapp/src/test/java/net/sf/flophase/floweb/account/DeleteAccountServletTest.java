package net.sf.flophase.floweb.account;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link DeleteAccountServlet} class.
 */
public class DeleteAccountServletTest extends AbstractServletTest {

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

		final List<Account> accountList = new ArrayList<Account>();
		accountList.add(new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE));

		final Response<Void> response = new Response<Void>(Response.RESULT_SUCCESS);

		context.checking(new Expectations() {
			{
				oneOf(mockAccountService).deleteAccount(ACCOUNT_KEY);
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[]}");
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?key=" + ACCOUNT_KEY;
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return DeleteAccountServlet.class;
	}

}
