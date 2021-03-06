package net.sf.flophase.floweb.cashflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * The logger
	 */
	private static final Logger log = Logger
			.getLogger(CashFlowImportServlet.class.getName());

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

	/**
	 * A wrapper to execute a common logic flow.
	 */
	@Inject
	private ServletRequestWrapper<CashFlowImportStatus> logicWrapper;

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Response<CashFlowImportStatus> response;

		BufferedReader reader = req.getReader();
		try {
			String line;
			StringBuilder requestDataString = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				requestDataString.append(line);
			}

			String key = req.getParameter("key");
			String cashflowId = req.getParameter("cashflow");

			// execute synchronously
			response = cashflowService.importCashFlow(cashflowId, key,
					requestDataString.toString());

			String output = gson.toJson(response);

			if (response.getResult() == Response.RESULT_FAILURE) {
				log.log(Level.SEVERE, response.getMessages().toString());
			}

			resp.setContentType(Constants.JSON_CONTENT_TYPE);
			resp.setContentLength(output.length());
			resp.getWriter().write(output);
		} finally {
			reader.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		BufferedReader reader = req.getReader();
		try {
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
		} finally {
			reader.close();
		}
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
