package net.sf.flophase.floweb.xaction;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.flophase.floweb.common.Response;

import com.google.gson.Gson;
import com.google.inject.Singleton;

/**
 * This servlet returns a Response containing a list of transactions formatted as JSON.
 */
@Singleton
public class TransactionQueryServlet extends HttpServlet {

	/**
	 * The month parameter.
	 */
	private static final String PARM_MONTH = "month";

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The transaction service to execute the logic.
	 */
	@Inject
	TransactionService xactionService;

	/**
	 * The JSON formatter.
	 */
	@Inject
	Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String month = req.getParameter(PARM_MONTH);

		Response<List<FinancialTransaction>> response = xactionService.getTransactions(month);

		String output = gson.toJson(response);

		resp.setContentType("application/json");
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
