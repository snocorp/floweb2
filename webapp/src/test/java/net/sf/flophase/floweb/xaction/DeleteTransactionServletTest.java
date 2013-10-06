package net.sf.flophase.floweb.xaction;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTestCase;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link DeleteTransactionServlet} class.
 */
public class DeleteTransactionServletTest extends AbstractServletTestCase {

	/**
	 * The transaction key.
	 */
	private static final String TRANSACTION_KEY = "1";

	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/xaction-delete";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final TransactionService mockTransactionService = testServletContextListener.getModule()
		        .getTransactionService();

		final Response<Void> response = new Response<Void>(Response.RESULT_SUCCESS);

		context.checking(new Expectations() {
			{
				oneOf(mockTransactionService).deleteTransaction(TRANSACTION_KEY);
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[]}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return DeleteTransactionServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?key=" + TRANSACTION_KEY;
	}

}
