package net.sf.flophase.floweb.cashflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.test.AbstractServletTestCase;
import net.sf.flophase.floweb.xaction.FinancialTransaction;
import net.sf.flophase.floweb.xaction.Transaction;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link CashFlowQueryServlet} class.
 */
public class CashFlowExportServletTest extends AbstractServletTestCase {

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
	private static final String SERVLET_PATH = "/cashflow-export";

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

		final List<Account> accountList = new ArrayList<Account>();
		accountList.add(new Account(null, ACCOUNT_NAME, ACCOUNT_BALANCE));

		Calendar calendar = new GregorianCalendar(2012, 2, 26, 8, 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				Constants.ISO_DATE_FORMAT);

		Transaction xaction = new Transaction(null, TRANSACTION_NAME,
				calendar.getTime());

		Map<Long, Entry> entryMap = new HashMap<Long, Entry>();
		entryMap.put(ACCOUNT_ID, new Entry(null, ACCOUNT_ID, ENTRY_AMOUNT));

		final List<FinancialTransaction> xactionList = new ArrayList<FinancialTransaction>();
		xactionList.add(new FinancialTransaction(xaction, entryMap));

		CashFlowExport cashflow = new CashFlowExport();
		cashflow.setAccounts(accountList);
		cashflow.setTransactions(xactionList);

		final Response<CashFlowExport> response = new Response<CashFlowExport>(
				Response.RESULT_SUCCESS, cashflow);

		context.checking(new Expectations() {
			{
				oneOf(mockCashFlowService).getCashFlowExport();
				will(returnValue(response));
			}
		});

		// ensure the response is a JSON formatted version of the response
		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"transactions\":[{\"details\":{\"name\":\""
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
				+ ",\"negativeThreshold\":0.0,\"positiveThreshold\":0.0}]}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return CashFlowExportServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

}
