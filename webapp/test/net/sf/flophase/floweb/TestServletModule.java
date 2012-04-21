package net.sf.flophase.floweb;

import javax.servlet.http.HttpServlet;

import com.google.inject.servlet.ServletModule;

/**
 * This servlet module maps one servlet class to a path for testing purposes.
 */
public class TestServletModule extends ServletModule {
	/**
	 * The servlet path.
	 */
	private final String path;

	/**
	 * The servlet class.
	 */
	private final Class<? extends HttpServlet> servletClass;

	/**
	 * Creates a new {@link TestServletModule} instance.
	 * 
	 * @param path
	 *            The servlet path
	 * @param servletClass
	 *            The servlet class that will be mapped to the path
	 */
	public TestServletModule(String path, Class<? extends HttpServlet> servletClass) {
		this.path = path;
		this.servletClass = servletClass;
	}

	@Override
	protected void configureServlets() {
		serve(path).with(servletClass);
	}

}
