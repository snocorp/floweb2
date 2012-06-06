package net.sf.flophase.test.basic;

import static org.junit.Assert.*;
import net.sf.flophase.test.util.FloWebHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * This class tests that logging in works.
 */
public class LoginTest {

	/**
	 * The selenium web driver
	 */
	private static WebDriver driver;

	/**
	 * Helper class for web unit tests.
	 */
	private static FloWebHelper helper;

	/**
	 * Sets up the driver and the helper.
	 * 
	 * @throws Exception
	 *             IF an error occurs.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = new FirefoxDriver();

		helper = new FloWebHelper(driver);
	}

	/**
	 * The username for login
	 */
	private String username;

	/**
	 * Sets up the test case.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		username = helper.getEmail();
	}

	/**
	 * Logs in a user.
	 */
	@Test
	public void testLogin() {
		driver.get("http://localhost:8080/");
		helper.login(username);

		WebElement userIdentifierElem = driver.findElement(By.id("userIdentifier"));
		assertEquals(username, userIdentifierElem.getText());
	}

}
