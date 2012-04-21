package net.sf.flophase.floweb.common;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * This abstract test class provides a pre-configured helper for the app engine.
 */
public class AbstractDAOTestCase {

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
	        new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail("email@example.com")
	        .setEnvAuthDomain("localhost");

	/**
	 * Sets up the test case. Sets up the app engine helper.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	/**
	 * Tears down the test case. Tears down the app engine helper.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

}