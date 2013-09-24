package net.sf.flophase.floweb.ui;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.flophase.floweb.common.Constants;
import net.sf.flophase.floweb.user.UserStore;

/**
 * This servlet is the starting point for the user interface.
 */
@Singleton
public class UserInterfaceServlet extends HttpServlet {

	/**
	 * Serialization identifier
	 */
	private static final long serialVersionUID = 4667900059644886931L;

	@Inject
	private UserStore userStore;

	@Inject
	private UserInterfaceService uiService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType(Constants.HTML_CONTENT_TYPE);
		uiService.writeApp(req.getRequestURI(), resp.getWriter());
	}
}
