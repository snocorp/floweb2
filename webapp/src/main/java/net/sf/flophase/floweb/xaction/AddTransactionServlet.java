package net.sf.flophase.floweb.xaction;

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
 * This servlet returns a Response containing newly created transaction formatted as JSON.
 */
@Singleton
public class AddTransactionServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The date parameter.
	 */
	private static final String PARM_DATE = "date";

	/**
	 * The name parameter.
	 */
	private static final String PARM_NAME = "name";

	/**
	 * The transaction service
	 */
	@Inject
	TransactionService xactionService;

	/**
	 * The JSON formatter
	 */
	@Inject
	Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(PARM_NAME);
		String date = req.getParameter(PARM_DATE);

		Response<FinancialTransaction> response = xactionService.addTransaction(name, date);

		String output = gson.toJson(response);

		resp.setContentType("application/json");
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
