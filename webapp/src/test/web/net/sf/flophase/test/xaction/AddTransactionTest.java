package net.sf.flophase.test.xaction;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.sf.flophase.test.util.FloWebHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * This class tests that adding transactions works.
 */
public class AddTransactionTest {

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
	 * Creates a transaction and verifies the name and date.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testAddTransactionBeforeToday() throws Exception {
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

		// find the date on the calendar
		driver.findElement(
				By.xpath("//table[@id='newTransactionCalendar']/tbody/tr["
						+ week + "]/td[" + day + "]/span")).click();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(cal.getTime());

		// ensure the hidden input was updated
		WebElement newTransactionDateInput = helper.waitForElement(By
				.id("newTransactionDate"));
		assertEquals(today, newTransactionDateInput.getAttribute("value"));

		// click the ok button
		driver.findElement(By.id("addTransactionOk")).click();

		// wait for things to happen
		Thread.sleep(2000);

		// find the transaction name cell that was created
		WebElement xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		// make sure the transaction name is correct
		assertEquals("Transaction 1", xactionNameCell.getText());

		// use the id to get the transaction id
		String xactionNameCellId = xactionNameCell.getAttribute("id");
		String xactionId = xactionNameCellId.substring(xactionNameCellId
				.indexOf('_') + 1);

		//get the hidden input next to the calendar dropdown
		WebElement xactionDateInput = helper.waitForElement(By
				.cssSelector("#dateInput_" + xactionId + " + input"));
		
		//make sure the value is today as selected above
		assertEquals(today, xactionDateInput.getAttribute("value"));
	}

}
