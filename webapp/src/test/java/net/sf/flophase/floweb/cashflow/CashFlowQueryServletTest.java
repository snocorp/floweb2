package net.sf.flophase.floweb.cashflow;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTestCase;

import org.jmock.Expectations;
import org.junit.Test;

import com.google.appengine.api.users.User;

/**
 * This class tests the {@link CashFlowQueryServlet} class.
 */
public class CashFlowQueryServletTest extends AbstractServletTestCase {

	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/cashflow-q";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final CashFlowService mockCashFlowService = testServletContextListener.getModule().getCashFlowService();

		User user = new User("email@example.com", "localhost");
		CashFlow cashflow = new CashFlow(user);
		final Response<CashFlow> response = new Response<CashFlow>(Response.RESULT_SUCCESS, cashflow);

		context.checking(new Expectations() {
			{
				oneOf(mockCashFlowService).getCashFlow();
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"user\":{\"email\":\"email@example.com\",\"authDomain\":\"localhost\"}}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return CashFlowQueryServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

}
