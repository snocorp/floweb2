package net.sf.flophase.floweb.test;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServlet;

import net.sf.flophase.floweb.TestServletContextListener;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

import com.google.inject.servlet.GuiceFilter;

/**
 * This abstract class provides functionality to allow simple testing of servlets using a test jetty container.
 */
public abstract class AbstractServletTest {

	/**
	 * The mock context
	 */
	protected Mockery context;

	/**
	 * A servlet context listener to set up injection.
	 */
	protected TestServletContextListener testServletContextListener;

	/**
	 * A test jetty container.
	 */
	private static ServletTester tester;

	/**
	 * The base URL for the servlets.
	 */
	private static String baseUrl;

	/**
	 * Initializes the servlet container. Adds a context listener and the Guice filter to initialize injection and
	 * create the mapping to the servlet.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void initServletContainer() throws Exception {
		// init the mock context
		context = new Mockery();

		testServletContextListener = new TestServletContextListener(context, getServletPath(), getServletClass());

		tester = new ServletTester();
		tester.addEventListener(testServletContextListener);
		tester.addFilter(GuiceFilter.class, "/*", 0);
		tester.addServlet(DefaultServlet.class, "/");
		tester.setContextPath("/");
		baseUrl = tester.createSocketConnector(true);
		tester.start();
	}

	/**
	 * Returns the class that will be use to handle the request.
	 * 
	 * @return The servlet class
	 */
	protected abstract Class<? extends HttpServlet> getServletClass();

	/**
	 * Returns the path to the servlet.
	 * 
	 * @return The servlet path.
	 */
	protected abstract String getServletPath();

	/**
	 * Returns the query that will be appended to the request URL.
	 * 
	 * @return The request query
	 */
	protected String getQuery() {
		return "";
	}

	/**
	 * Stops the Jetty container.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@After
	public void cleanupServletContainer() throws Exception {
		tester.stop();
	}

	/**
	 * Asserts that the response of the servlet is equal to the given expected content.
	 * 
	 * @param expectedContent
	 *            The expected content to be returned by the servlet response.
	 * @throws Exception
	 *             If an error occurs.
	 */
	public void assertResponseContent(String expectedContent) throws Exception {
		HttpTester request = new HttpTester();
		request.setMethod("GET");
		request.setHeader("Host", "localhost");
		request.setURI(baseUrl + getServletPath() + getQuery());
		request.setVersion("HTTP/1.0");

		HttpTester response = new HttpTester();
		response.parse(tester.getResponses(request.generate()));

		assertEquals(expectedContent, response.getContent());
	}

}
