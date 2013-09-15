package net.sf.flophase.floweb.xaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link AddTransactionServlet} class.
 */
public class AddTransactionServletTest extends AbstractServletTest {

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * The serlvet path.
	 */
	public static final String SERVLET_PATH = "/xaction-add";

	/**
	 * A calendar to start at a known date.
	 */
	private final Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);

	/**
	 * A date formatter using the ISO 8601 date format.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ISO_DATE_FORMAT);

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

		final Transaction xaction = new Transaction(null, TRANSACTION_NAME, calendar.getTime());

		final Response<Transaction> response = new Response<Transaction>(Response.RESULT_SUCCESS, xaction);

		context.checking(new Expectations() {
			{
				oneOf(mockTransactionService).addTransaction(TRANSACTION_NAME, dateFormat.format(calendar.getTime()));
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"name\":\"" + TRANSACTION_NAME
		        + "\",\"date\":\"" + dateFormat.format(calendar.getTime()) + "\"}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return AddTransactionServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?name=" + TRANSACTION_NAME + "&date=" + dateFormat.format(calendar.getTime());
	}

}
