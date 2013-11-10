package net.sf.flophase.floweb.cashflow;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTestCase;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link CashFlowImportServlet} class.
 */
public class CashFlowImportServletTest extends AbstractServletTestCase {

	/**
	 * The account balance.
	 */
	private static final double ACCOUNT_BALANCE = 1.23;

	/**
	 * The account name.
	 */
	private static final String ACCOUNT_NAME = "Account1";

	/**
	 * The transaction name.
	 */
	private static final String TRANSACTION_NAME = "Transaction1";

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
	private static final String SERVLET_PATH = "/cashflow-import";

	/**
	 * The key for the import status.
	 */
	private static final String IMPORT_STATUS_KEY = "1";

	/**
	 * The key for the cash flow.
	 */
	private static final String CASH_FLOW_KEY = "1000";

	@Override
	protected String getQuery() {
		return "?key=" + IMPORT_STATUS_KEY + "&cashflow=" + CASH_FLOW_KEY;
	}

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final CashFlowService mockCashFlowService = testServletContextListener
				.getModule().getCashFlowService();

		CashFlowImportStatus importStatus = new CashFlowImportStatus();

		final Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS, importStatus);

		context.checking(new Expectations() {
			{
				oneOf(mockCashFlowService).getCashFlowImportStatus(
						IMPORT_STATUS_KEY);
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"done\":0,\"total\":0}}");
	}

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoPost() throws Exception {
		final CashFlowService mockCashFlowService = testServletContextListener
				.getModule().getCashFlowService();

		Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Constants.ISO_DATE_FORMAT);

		final String cashflowJson = "{\"result\":1,\"messages\":[],\"content\":{\"transactions\":[{\"details\":{\"name\":\""
				+ TRANSACTION_NAME
				+ "\",\"date\":\""
				+ dateFormat.format(calendar.getTime())
				+ "\"},\"entries\":{\""
				+ ACCOUNT_ID
				+ "\":{\"account\":"
				+ ACCOUNT_ID
				+ ",\"amount\":"
				+ ENTRY_AMOUNT
				+ "}}}],\"accounts\":[{\"name\":\""
				+ ACCOUNT_NAME
				+ "\",\"balance\":"
				+ ACCOUNT_BALANCE
				+ ",\"negativeThreshold\":0.0,\"positiveThreshold\":0.0}]}}";

		CashFlowImportStatus importStatus = new CashFlowImportStatus();

		final Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS, importStatus);

		context.checking(new Expectations() {
			{
				oneOf(mockCashFlowService).importCashFlow(cashflowJson);
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("POST", cashflowJson,
				"{\"result\":1,\"messages\":[],\"content\":{\"done\":0,\"total\":0}}");
	}

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoPut() throws Exception {
		final CashFlowService mockCashFlowService = testServletContextListener
				.getModule().getCashFlowService();

		Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Constants.ISO_DATE_FORMAT);

		final String cashflowJson = "{\"result\":1,\"messages\":[],\"content\":{\"transactions\":[{\"details\":{\"name\":\""
				+ TRANSACTION_NAME
				+ "\",\"date\":\""
				+ dateFormat.format(calendar.getTime())
				+ "\"},\"entries\":{\""
				+ ACCOUNT_ID
				+ "\":{\"account\":"
				+ ACCOUNT_ID
				+ ",\"amount\":"
				+ ENTRY_AMOUNT
				+ "}}}],\"accounts\":[{\"name\":\""
				+ ACCOUNT_NAME
				+ "\",\"balance\":"
				+ ACCOUNT_BALANCE
				+ ",\"negativeThreshold\":0.0,\"positiveThreshold\":0.0}]}}";

		CashFlowImportStatus importStatus = new CashFlowImportStatus();

		final Response<CashFlowImportStatus> response = new Response<CashFlowImportStatus>(
				Response.RESULT_SUCCESS, importStatus);

		context.checking(new Expectations() {
			{
				oneOf(mockCashFlowService).importCashFlow(CASH_FLOW_KEY,
						IMPORT_STATUS_KEY, cashflowJson);
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("PUT", cashflowJson,
				"{\"result\":1,\"messages\":[],\"content\":{\"done\":0,\"total\":0}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return CashFlowImportServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

}
