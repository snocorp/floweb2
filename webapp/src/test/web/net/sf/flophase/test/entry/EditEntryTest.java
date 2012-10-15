package net.sf.flophase.test.entry;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
		// we need to time this case to allow a timeout if certain interactions
		// fail
		final long startTime = System.currentTimeMillis();

		// get the add transaction button
		WebElement addTransactionButton = helper.waitForElement(By
				.id("addTransaction"));

		// while the button is marked as disabled (css class)
		while (addTransactionButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				fail("Add transaction button did not become enabled");
			}
		}

		// click the add transaction button
		addTransactionButton.click();

		// get the transaction name input
		WebElement newTransactionNameInput = helper
				.waitForVisibleEnabledElement(By.id("newTransactionName"));

		// clear the value and input a new name
		newTransactionNameInput.clear();
		newTransactionNameInput.sendKeys("Transaction 1");

		// find the week and day of the week for today
		Calendar cal = Calendar.getInstance();
		int week = cal.get(Calendar.WEEK_OF_MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		//find the date on the calendar
		driver.findElement(
				By.xpath("//table[@id='newTransactionCalendar']/tbody/tr["
						+ week + "]/td[" + day + "]/span")).click();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(cal.getTime());

		// ensure the hidden input was updated
		WebElement newTransactionDateInput = helper.waitForElement(By
				.id("newTransactionDate"));
		assertEquals(today, newTransactionDateInput.getAttribute("value"));

		//click the ok button
		driver.findElement(By.id("addTransactionOk")).click();
		
		//wait for things to happen
		Thread.sleep(2000);

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
