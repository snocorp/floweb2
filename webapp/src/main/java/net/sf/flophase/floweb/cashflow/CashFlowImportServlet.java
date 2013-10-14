package net.sf.flophase.floweb.cashflow;

import java.io.BufferedReader;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.common.Executable;
import net.sf.flophase.floweb.common.Response;
import net.sf.flophase.floweb.common.ServletRequestWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;

/**
 * This servlet returns a Response containing a cash flow formatted as JSON.
 */
@Singleton
public class CashFlowImportServlet extends HttpServlet {

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

	@Inject
	private ServletRequestWrapper<CashFlowImportStatus> logicWrapper;

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Response<CashFlowImportStatus> response;

		BufferedReader reader = req.getReader();
		String line;
		StringBuilder requestDataString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			requestDataString.append(line);
		}

		String key = req.getParameter("key");

		// execute synchronously
		response = cashflowService.importCashFlow(key,
				requestDataString.toString());

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		BufferedReader reader = req.getReader();
		String line;
		final StringBuilder requestDataString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			requestDataString.append(line);
		}

		Executable<Response<CashFlowImportStatus>> logic = new Executable<Response<CashFlowImportStatus>>() {

			@Override
			public Response<CashFlowImportStatus> execute() {
				return cashflowService.importCashFlow(requestDataString
						.toString());
			}
		};

		logicWrapper.execute(logic,
				new TypeToken<Response<CashFlowImportStatus>>() {
				}.getType(), resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String key = req.getParameter("key");

		Response<CashFlowImportStatus> response = cashflowService
				.getCashFlowImportStatus(key);

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
