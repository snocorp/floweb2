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
 * This servlet returns a Response containing an updated account formatted as
 * JSON.
 */
@Singleton
public class EditAccountServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The key parameter.
	 */
	private static final String PARM_KEY = "key";

	/**
	 * The name parameter.
	 */
	private static final String PARM_NAME = "name";

	/**
	 * The balance parameter.
	 */
	private static final String PARM_BALANCE = "balance";

	/**
	 * The negative threshold parameter.
	 */
	private static final String PARM_NEGATIVE_THRESHOLD = "neg";

	/**
	 * The positive threshold parameter.
	 */
	private static final String PARM_POSITIVE_THRESHOLD = "pos";

	/**
	 * The account service to handle the logic.
	 */
	@Inject
	AccountService acctService;

	/**
	 * The JSON formatter.
	 */
	@Inject
	Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String key = req.getParameter(PARM_KEY);
		String name = req.getParameter(PARM_NAME);
		String balance = req.getParameter(PARM_BALANCE);
		String negativeThreshold = req.getParameter(PARM_NEGATIVE_THRESHOLD);
		String positiveThreshold = req.getParameter(PARM_POSITIVE_THRESHOLD);

		Response<Account> response = acctService.editAccount(key, name,
				balance, negativeThreshold, positiveThreshold);

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

}
