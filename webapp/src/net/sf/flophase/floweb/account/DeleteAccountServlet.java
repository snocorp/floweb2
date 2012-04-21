package net.sf.flophase.floweb.account;

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
 * This servlet returns an empty Response formatted as JSON.
 */
@Singleton
public class DeleteAccountServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The key parameter.
	 */
	private static final String PARM_KEY = "key";

	/**
	 * The account service to handle the logic.
	 */
	@Inject
	AccountService acctService;

	/**
	 * JSON formatter.
	 */
	@Inject
	Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = req.getParameter(PARM_KEY);

		Response<Void> response = acctService.deleteAccount(key);

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
