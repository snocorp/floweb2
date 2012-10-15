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
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateAccountBalance() throws Exception {
		// we need to find the current balance cell for the account
		// start by finding the current row
		WebElement currentRow = helper.waitForElement(By.id("currentRow"));
		List<WebElement> currentRowCells = currentRow.findElements(By
				.tagName("td"));

		//the row should have 3 cells: a label, the date and the balance
		assertEquals(3, currentRowCells.size());

		//get the last cell
		WebElement currentBalanceCell = currentRowCells.get(2);
		String currentBalanceCellId = currentBalanceCell.getAttribute("id");

		//determine the id of the account
		String accountId = currentBalanceCellId.substring(currentBalanceCellId
				.indexOf('_') + 1);

		//use the id to find the balance input
		WebElement currbalInput = currentBalanceCell.findElement(By
				.id("currbalInput_" + accountId));

		//clear the input and send a new balance
		currbalInput.clear();
		currbalInput.sendKeys("123.45", Keys.RETURN);
		
		//refresh the page, if the balance was persisted, it will appear as the current balance
		driver.navigate().refresh();

		// wait for things to happen
		Thread.sleep(1000);
		
		//get the balance input again
		currbalInput = helper.waitForElement(By
				.id("currbalInput_" + accountId));
		
		//make sure the value is right
		assertEquals("$123.45", currbalInput.getAttribute("value"));
	}

}
