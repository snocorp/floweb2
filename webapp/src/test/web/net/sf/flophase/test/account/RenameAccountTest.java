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
 * This class tests that renaming accounts works.
 */
public class RenameAccountTest {

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
	 * Renames the initially created account using the entry header.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testRenameUsingEntryHeader() throws Exception {
		// we need to find the header cell for the account
		// start by finding the header row
		WebElement accountHeaderRow = helper.waitForElement(By
				.id("accountHeaderRow"));

		// get the first the cell
		WebElement accountEntryHeaderCell = accountHeaderRow.findElement(By
				.tagName("th"));

		// click it to open the edit account dialog
		accountEntryHeaderCell.click();

		// get the name input
		WebElement accountNameInput = helper.waitForElement(By
				.id("accountName"));

		// wait for things to happen
		Thread.sleep(500);

		// make sure the original account name is as expected
		assertEquals("My Account", accountNameInput.getAttribute("value"));

		// clear the name and type a new one
		accountNameInput.clear();
		accountNameInput.sendKeys("New Account Name", Keys.RETURN);

		// reload the first cell
		accountEntryHeaderCell = accountHeaderRow.findElement(By.tagName("th"));

		// wait for things to happen
		Thread.sleep(500);

		// make sure the name has been updated
		assertEquals("New Account Name", accountEntryHeaderCell.getText());
	}

	/**
	 * Renames the initially created account using the entry header.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testRenameUsingBalanceHeader() throws Exception {
		WebElement accountHeaderRow = helper.waitForElement(
				By.id("accountHeaderRow"), 5000);
		List<WebElement> accountHeaderCells = accountHeaderRow.findElements(By
				.tagName("th"));

		// ensure there are exactly two header cells
		assertEquals(2, accountHeaderCells.size());

		// the balance header cell is the second one
		WebElement accountBalanceHeaderCell = accountHeaderCells.get(1);

		accountBalanceHeaderCell.click();

		WebElement accountNameInput = helper.waitForElement(
				By.id("accountName"), 5000);

		Thread.sleep(500);

		assertEquals("My Account", accountNameInput.getAttribute("value"));

		accountNameInput.clear();

		accountNameInput.sendKeys("New Account Name", Keys.RETURN);

		accountBalanceHeaderCell = accountHeaderRow.findElement(By
				.tagName("th"));

		Thread.sleep(500);

		assertEquals("New Account Name", accountBalanceHeaderCell.getText());
	}

}
