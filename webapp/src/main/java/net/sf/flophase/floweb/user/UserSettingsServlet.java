package net.sf.flophase.floweb.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
 * This servlet returns a Response containing a list of accounts formatted as
 * JSON.
 */
@Singleton
public class UserSettingsServlet extends HttpServlet {

	private static final String KEYS_DELIM = ",";

	private static final String PARM_KEYS = "keys";

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 9209614462585301347L;

	/**
	 * The account service to handle the logic.
	 */
	@Inject
	private UserService userService;

	/**
	 * JSON formatter.
	 */
	@Inject
	private Gson gson;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String[] keys = getKeys(req);

		Response<Map<String, String>> response = userService.getSettings(keys);

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

	private String[] getKeys(HttpServletRequest req) {
		String keys = req.getParameter(PARM_KEYS);
		StringTokenizer tokenizer = new StringTokenizer(keys, KEYS_DELIM);
		String[] keyArray = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreElements()) {
			keyArray[i] = tokenizer.nextToken();
		}

		return keyArray;
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> settings = getSettings(req);

		Response<Void> response = userService.putSettings(settings);

		String output = gson.toJson(response);

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}

	private Map<String, String> getSettings(HttpServletRequest req) {
		Map<String, String> settings = new HashMap<String, String>();
		Map<?, ?> parmMap = req.getParameterMap();
		for (Map.Entry<?, ?> entry : parmMap.entrySet()) {
			settings.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return settings;
	}

}
