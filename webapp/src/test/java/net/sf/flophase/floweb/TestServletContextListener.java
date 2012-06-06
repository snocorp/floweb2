package net.sf.flophase.floweb;

import javax.servlet.http.HttpServlet;

import org.jmock.Mockery;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This context listener creates the injector with the proper modules for testing.
 */
public class TestServletContextListener extends GuiceServletContextListener {

	/**
	 * The test module for binding the service classes.
	 */
	private final TestModule module;

	/**
	 * The servlet module for binding the servlet to be tested.
	 */
	private final TestServletModule servletModule;

	/**
	 * Creates a new {@link TestServletContextListener} instance.
	 * 
	 * @param context
	 *            The mock context
	 * @param path
	 *            The servlet path
	 * @param servletClass
	 *            The servlet class that will be tested
	 */
	public TestServletContextListener(Mockery context, String path, Class<? extends HttpServlet> servletClass) {
		this.module = new TestModule(context);
		this.servletModule = new TestServletModule(path, servletClass);
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(this.module, this.servletModule);
	}

	/**
	 * Returns the module that has the service classes.
	 * 
	 * @return The test module
	 */
	public TestModule getModule() {
		return module;
	}

}
