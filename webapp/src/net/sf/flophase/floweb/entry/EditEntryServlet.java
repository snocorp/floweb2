package net.sf.flophase.floweb.entry;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.flophase.floweb.common.Response;

import com.google.gson.Gson;
import com.google.inject.Singleton;

/**
 * This servlet returns a Response containing newly created entry formatted as JSON.
 */
@Singleton
public class EditEntryServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The account parameter.
	 */
	private static final String PARM_ACCOUNT = "account";

	/**
	 * The xaction parameter.
	 */
	private static final String PARM_XACTION = "xaction";

	/**
	 * The amount parameter
	 */
	private static final String PARM_AMOUNT = "amount";

	/**
	 * The transaction service
	 */
	@Inject
	EntryService entryService;

	/**
	 * The JSON formatter
	 */
	@Inject
	Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// the key of the account
		String account = req.getParameter(PARM_ACCOUNT);

		// the key of the transaction
		String xaction = req.getParameter(PARM_XACTION);

		// the amount of the entry
		String amount = req.getParameter(PARM_AMOUNT);

		Response<Entry> response = entryService.editEntry(account, xaction, amount);

		String output = gson.toJson(response);

		resp.setContentType("application/json");
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
