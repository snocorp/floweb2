package net.sf.flophase.floweb.cashflow;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Response;

import com.google.gson.Gson;
import com.google.inject.Singleton;

/**
 * This servlet returns a Response containing a list of accounts formatted as JSON.
 */
@Singleton
public class CashFlowQueryServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The account service to handle the logic.
	 */
	@Inject
	private CashFlowService cashflowService;

	/**
	 * JSON formatter.
	 */
	@Inject
	private Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response<CashFlow> response = cashflowService.getCashFlow();

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
