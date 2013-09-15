package net.sf.flophase.floweb.entry;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.test.AbstractServletTest;

import org.jmock.Expectations;
import org.junit.Test;

/**
 * This class tests the {@link EditEntryServlet} class.
 */
public class EditEntryServletTest extends AbstractServletTest {

	/**
	 * The entry amount.
	 */
	private static final String ENTRY_AMOUNT = "1.23";

	/**
	 * The transaction key.
	 */
	private static final String XACTION_KEY = "1";

	/**
	 * The account key.
	 */
	private static final String ACCOUNT_KEY = "2";

	/**
	 * The servlet path.
	 */
	public static final String SERVLET_PATH = "/entry-edit";

	/**
	 * Tests the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testDoGet() throws Exception {
		final EntryService mockEntryService = testServletContextListener.getModule().getEntryService();

		final Entry entry = new Entry(null, Long.parseLong(ACCOUNT_KEY), Double.parseDouble(ENTRY_AMOUNT));

		final Response<Entry> response = new Response<Entry>(Response.RESULT_SUCCESS, entry);

		context.checking(new Expectations() {
			{
				oneOf(mockEntryService).editEntry(ACCOUNT_KEY, XACTION_KEY, ENTRY_AMOUNT);
				will(returnValue(response));
			}
		});

		assertResponseContent("{\"result\":1,\"messages\":[],\"content\":{\"account\":2,\"amount\":" + ENTRY_AMOUNT
		        + "}}");
	}

	@Override
	protected Class<? extends HttpServlet> getServletClass() {
		return EditEntryServlet.class;
	}

	@Override
	protected String getServletPath() {
		return SERVLET_PATH;
	}

	@Override
	protected String getQuery() {
		return "?xaction=" + XACTION_KEY + "&account=" + ACCOUNT_KEY + "&amount=" + ENTRY_AMOUNT;
	}

}
