package net.sf.flophase.floweb.xaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.test.AbstractServletTestCase;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link TransactionQueryServlet} class.
 */
public class TransactionQueryServletTest extends AbstractServletTestCase {

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

	/**
	 * The month that will be queried.
	 */
	private static final String QUERY_MONTH = "2012-02";

	/**
	 * The account id for the entry.
	 */
	private static final Long ACCOUNT_ID = 12345678L;

	/**
	 * The entry amount.
	 */
	private static final double ENTRY_AMOUNT = 12.34;

	/**
	 * The servlet path.
	 */
	private static final String SERVLET_PATH = "/xaction-q";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final TransactionService mockTransactionService = testServletContextListener
				.getModule().getTransactionService();

		Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Constants.ISO_DATE_FORMAT);

		Transaction xaction = new Transaction(null, TRANSACTION_NAME,
				calendar.getTime());

		Map<Long, Entry> entryMap = new HashMap<Long, Entry>();
		entryMap.put(ACCOUNT_ID, new Entry(null, ACCOUNT_ID, ENTRY_AMOUNT));

		final List<FinancialTransaction> xactionList = new ArrayList<FinancialTransaction>();
		xactionList.add(new FinancialTransaction(xaction, entryMap));

		final Response<List<FinancialTransaction>> response = new Response<List<FinancialTransaction>>(
				Response.RESULT_SUCCESS, xactionList);

		context.checking(new Expectations() {
			{
				oneOf(mockTransactionService).getTransactions(QUERY_MONTH);
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":[{\"details\":{\"name\":\""
				+ TRANSACTION_NAME
				+ "\",\"date\":\""
				+ dateFormat.format(calendar.getTime())
				+ "\"},\"entries\":{\""
				+ ACCOUNT_ID
				+ "\":{\"account\":"
				+ ACCOUNT_ID
				+ ",\"amount\":"
				+ ENTRY_AMOUNT + "}}}]}");
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
