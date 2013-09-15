package net.sf.flophase.floweb.xaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link EditTransactionServlet} class.
 */
public class CopyTransactionServletTest extends AbstractServletTest {

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction2";

	/**
	 * The transaction key.
	 */
	private static final String TRANSACTION_KEY = "1";

	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/xaction-copy";

	/**
	 * A calendar instance for a known date.
	 */
	private final Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);

	/**
	 * A date format using ISO 8601 format.
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
		final Map<Long, Entry> entries = new HashMap<Long, Entry>();
		final FinancialTransaction finXaction = new FinancialTransaction(xaction, entries );

		final Response<FinancialTransaction> response = new Response<FinancialTransaction>(Response.RESULT_SUCCESS, finXaction);

		context.checking(new Expectations() {
			{
				oneOf(mockTransactionService).copyTransaction(TRANSACTION_KEY, TRANSACTION_NAME,
				        dateFormat.format(calendar.getTime()));
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"details\":{\"name\":\"" + TRANSACTION_NAME
		        + "\",\"date\":\"" + dateFormat.format(calendar.getTime()) + "\"},\"entries\":{}}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return CopyTransactionServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?key=" + TRANSACTION_KEY + "&name=" + TRANSACTION_NAME + "&date="
		        + dateFormat.format(calendar.getTime());
	}

}
