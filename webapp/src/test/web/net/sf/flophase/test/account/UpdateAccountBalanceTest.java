package net.sf.flophase.test.account;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.flophase.test.util.FloWebHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * This class tests that updating the account balance works.
 */
public class UpdateAccountBalanceTest {

	/**
	 * The selenium web driver
	 */
	private static WebDriver driver;

	/**
	 * Helper class for web unit tests.
	 */
	private static FloWebHelper helper;

	/**
	 * The username for login
	 */
	private String username;

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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

	/**
	 * Sets up the test case.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		username = helper.getEmail();
		helper.start();
		helper.login(username);
	}

	@After
	public void tearDown() throws Exception {
		helper.logout();
	}

	/**
	 * Logs in a user.
	 * @throws Exception 
	 */
	@Test
	public void testUpdateAccountBalance() throws Exception {
		
		WebElement currentRow = helper.waitForElement(By.id("currentRow"), 5000);
		List<WebElement> currentRowCells = currentRow.findElements(By.tagName("td"));
		
		assertEquals(3, currentRowCells.size());
		
		WebElement currentBalanceCell = currentRowCells.get(2);
		String currentBalanceCellId = currentBalanceCell.getAttribute("id");
		
		String accountId = currentBalanceCellId.substring(currentBalanceCellId.indexOf('_')+1);
		
		WebElement currbalInput = currentBalanceCell.findElement(By.id("currbalInput_" + accountId));
		
		currbalInput.clear();
		
		currbalInput.sendKeys("123.45", Keys.RETURN);
	}

}
