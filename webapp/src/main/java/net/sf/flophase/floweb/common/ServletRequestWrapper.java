package net.sf.flophase.floweb.common;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * This class creates a wrapper to execute certain logic and handle it in a
 * generic way.
 * 
 * @param <T>
 *            The content type of the response
 */
public class ServletRequestWrapper<T> {

	/**
	 * JSON formatter.
	 */
	@Inject
	private Gson gson;

	/**
	 * Executes the given logic and sends the output to the given response.
	 * 
	 * @param logic
	 *            The logic to be executed
	 * @param type
	 *            The type that will be parsed
	 * @param resp
	 *            The response
	 * @throws IOException
	 *             If an i/o error occurs.
	 */
	public void execute(Executable<Response<T>> logic, Type type,
			HttpServletResponse resp) throws IOException {

		int status = 200;

		String output;
		Response<T> response;
		try {
			response = logic.execute();

			output = gson.toJson(response, type);
		} catch (Exception e) {
			response = new Response<T>(Response.RESULT_FAILURE);
			response.addMessage("Unexpected error");

			status = 500;

			output = gson.toJson(response, type);
		}

		resp.setContentType(Constants.JSON_CONTENT_TYPE);
		resp.setStatus(status);
		resp.setContentLength(output.length());
		resp.getWriter().write(output);
	}
}
