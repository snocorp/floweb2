package net.sf.flophase.floweb.xaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link TransactionQueryServlet} class.
 */
public class TransactionQueryServletTest extends AbstractServletTest {

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * The month that will be queried.
	 */
	private static final String QUERY_MONTH = "2012-02";

	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/xaction-q";

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

		Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);

		final List<Transaction> xactionList = new ArrayList<Transaction>();
		xactionList.add(new Transaction(null, TRANSACTION_NAME, calendar.getTime()));

		final Response<List<Transaction>> response = new Response<List<Transaction>>(Response.RESULT_SUCCESS,
		        xactionList);

		context.checking(new Expectations() {
			{
				one(mockTransactionService).getTransactions(QUERY_MONTH);
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":[{\"name\":\"" + TRANSACTION_NAME
		        + "\",\"date\":\"" + dateFormat.format(calendar.getTime()) + "\"}]}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return TransactionQueryServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?month=" + QUERY_MONTH;
	}

}
