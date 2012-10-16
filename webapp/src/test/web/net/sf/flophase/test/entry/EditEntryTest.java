package net.sf.flophase.test.entry;

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
 * This class tests that editing an entry works.
 */
public class EditEntryTest {

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
	 * Creates a transaction and updates the entry amount.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testEditEntryAmount() throws Exception {
		helper.addTransaction("Transaction 1");

		//we need to get the id of the account
		//start by getting the account header cell
		WebElement accountHeaderRow = helper.waitForElement(
				By.id("accountHeaderRow"), 5000);
		List<WebElement> accountEntryHeaderCells = accountHeaderRow
				.findElements(By.tagName("th"));

		WebElement newAccountEntryHeaderCell = accountEntryHeaderCells.get(0);

		//use the id to get the account id
		String headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		String accountId = headerCellId
				.substring(headerCellId.indexOf('_') + 1);

		//find the transaction name cell that was created
		WebElement xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		//use the id to get the transaction id
		String xactionNameCellId = xactionNameCell.getAttribute("id");
		String xactionId = xactionNameCellId.substring(xactionNameCellId
				.indexOf('_') + 1);

		//use both ids to get the entry id
		String entryInputId = "entryInput_" + accountId + "_" + xactionId;

		//get the entry input
		WebElement entryInput = driver.findElement(By.id(entryInputId));
		
		//clear the value and enter a new value
		entryInput.clear();
		entryInput.sendKeys("50", Keys.RETURN);

		//wait for things to happen
		Thread.sleep(2000);

		//make sure the value was updated
		assertEquals("$50.00", entryInput.getAttribute("value"));
	}

}
