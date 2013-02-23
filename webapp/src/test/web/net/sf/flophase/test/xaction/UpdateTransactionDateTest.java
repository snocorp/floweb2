package net.sf.flophase.test.xaction;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.sf.flophase.test.util.FloCalendarHelper;
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
 * This class tests that updating the date of transactions works.
 */
public class UpdateTransactionDateTest {

	/**
	 * The selenium web driver
	 */
	private static WebDriver driver;

	/**
	 * Helper class for web unit tests.
	 */
	private static FloWebHelper helper;

	private FloCalendarHelper calendarHelper = new FloCalendarHelper();

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
	 * Creates a transaction for today and sets its date to yesterday.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testUpdateDateFromYesterdayToTomorrow() throws Exception {
		Calendar cal = Calendar.getInstance();
		calendarHelper.setToMidnight(cal);

		// make sure today isn't the first or last day of the month
		assumeThat(cal.get(Calendar.DATE), is(greaterThan(1)));
		assumeThat(cal.get(Calendar.DATE), is(lessThan(cal.getActualMaximum(Calendar.DATE))));

		// point the calendar to yesterday
		cal.add(Calendar.DATE, -1);

		// add the transaction
		helper.addTransaction("Transaction 1", cal);

		// find the transaction row that was created
		WebElement xactionRow = driver
				.findElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='historicBody']/tr"));
		
		assertNotNull("Expected transaction row in historic section", xactionRow);

		String rowCellId = xactionRow.getAttribute("id");
		String accountId = rowCellId.substring(rowCellId.indexOf('_') + 1);

		WebElement dateInput = helper.waitForElement(By.id("dateInput_"
				+ accountId));
		dateInput.click();

		// select tomorrow by the keyboard
		dateInput.sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.RETURN);

		// wait for things to happen
		Thread.sleep(1000);

		// make sure the row moved to the historic section
		xactionRow = helper
				.waitForElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='upcomingBody']/tr[@id='"
								+ rowCellId + "']"));
		
		assertNotNull("Expected transaction row in upcoming section", xactionRow);

		// get the input again
		dateInput = helper.waitForElement(By.id("dateInput_" + accountId));

		// point the calendar to today
		cal.add(Calendar.DATE, 2);

		// make sure the transaction name is changed
		assertEquals(
				new SimpleDateFormat(FloCalendarHelper.ARIANOW_DATE_FORMAT).format(cal
						.getTime()), dateInput.getAttribute("aria-valuenow"));
	}

	/**
	 * Creates a transaction for today and sets its date to yesterday.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testUpdateDateFromTomorrowToToday() throws Exception {
		Calendar cal = Calendar.getInstance();
		calendarHelper.setToMidnight(cal);

		// make sure today isn't the first or last day of the month
		assumeThat(cal.get(Calendar.DATE), is(greaterThan(1)));
		assumeThat(cal.get(Calendar.DATE), is(lessThan(cal.getActualMaximum(Calendar.DATE))));

		// point the calendar to tomorrow
		cal.add(Calendar.DATE, 1);

		// add the transaction
		helper.addTransaction("Transaction 1", cal);

		// point the calendar to today
		cal.add(Calendar.DATE, -1);

		// find the transaction row that was created
		WebElement xactionRow = driver
				.findElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='upcomingBody']/tr"));
		
		assertNotNull("Expected transaction row in upcoming section", xactionRow);

		String rowCellId = xactionRow.getAttribute("id");
		String accountId = rowCellId.substring(rowCellId.indexOf('_') + 1);

		WebElement dateInput = helper.waitForElement(By.id("dateInput_"
				+ accountId));
		dateInput.click();

		// select today by the keyboard
		dateInput.sendKeys(Keys.ARROW_LEFT, Keys.RETURN);

		// wait for things to happen
		Thread.sleep(1000);

		// make sure the row moved to the historic section
		xactionRow = helper
				.waitForElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='historicBody']/tr[@id='"
								+ rowCellId + "']"));
		
		assertNotNull("Expected transaction row in historic section", xactionRow);

		// get the input again
		dateInput = helper.waitForElement(By.id("dateInput_" + accountId));

		// make sure the transaction name is changed
		assertEquals(
				new SimpleDateFormat(FloCalendarHelper.ARIANOW_DATE_FORMAT).format(cal
						.getTime()), dateInput.getAttribute("aria-valuenow"));
	}

	/**
	 * Creates a transaction for today and sets its date to yesterday.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testUpdateDateFromTodayToLastMonth() throws Exception {
		Calendar cal = Calendar.getInstance();
		calendarHelper.setToMidnight(cal);

		// add the transaction
		helper.addTransaction("Transaction 1", cal);

		// point the calendar to last month
		cal.add(Calendar.MONTH, -1);

		// find the transaction row that was created
		WebElement xactionRow = driver
				.findElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='historicBody']/tr"));
		
		assertNotNull("Expected transaction row in historic section", xactionRow);

		String rowCellId = xactionRow.getAttribute("id");
		String xactionId = rowCellId.substring(rowCellId.indexOf('_') + 1);

		WebElement dateInput = helper.waitForElement(By.id("dateInput_"
				+ xactionId));
		dateInput.click();

		// wait for things to happen
		Thread.sleep(500);

		int month = cal.get(Calendar.MONTH);

		// open the month selector
		driver.findElement(
				By.cssSelector("#widget_dateInput_" + xactionId + "_dropdown span.dijitArrowButtonInner"))
				.click();

		// wait for things to happen
		Thread.sleep(500);

		driver.findElement(
				By.xpath("//div[@id='dateInput_" + xactionId + "_popup_mddb_mdd']/div[@month='"
						+ month + "']")).click();

		// wait for things to happen
		Thread.sleep(500);

		// find the week and day of the week for today
		int week = cal.get(Calendar.WEEK_OF_MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		// find the date on the calendar
		driver.findElement(
				By.xpath("//table[@id='dateInput_"+xactionId+"_popup']/tbody/tr["
						+ week + "]/td[" + day + "]/span")).click();

		// wait for things to happen
		Thread.sleep(1000);

		// get the input again
		List<WebElement> dateInputsAfterUpdate = driver.findElements(By.id("dateInput_" + xactionId));
		
		assertTrue("Expected transaction to be out of range", dateInputsAfterUpdate.isEmpty());
		
		driver.findElement(By.id("loadEarlier")).click();
		
		//wait a little bit
		Thread.sleep(250);

		// get the input again
		dateInput = helper.waitForElement(By.id("dateInput_" + xactionId));

		// make sure the transaction date is changed
		assertEquals(
				new SimpleDateFormat(FloCalendarHelper.ARIANOW_DATE_FORMAT).format(cal
						.getTime()), dateInput.getAttribute("aria-valuenow"));
	}

	/**
	 * Creates a transaction for today and sets its date to yesterday.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testUpdateDateFromTomorrowToYesterday() throws Exception {
		Calendar cal = Calendar.getInstance();
		calendarHelper.setToMidnight(cal);

		// make sure today isn't the first or last day of the month
		assumeThat(cal.get(Calendar.DATE), is(greaterThan(1)));
		assumeThat(cal.get(Calendar.DATE), is(lessThan(cal.getActualMaximum(Calendar.DATE))));

		// point the calendar to tomorrow
		cal.add(Calendar.DATE, 1);

		// add the transaction
		helper.addTransaction("Transaction 1", cal);

		// point the calendar to yesterday
		cal.add(Calendar.DATE, -2);

		// find the transaction row that was created
		WebElement xactionRow = driver
				.findElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='upcomingBody']/tr"));
		
		assertNotNull("Expected transaction row in upcoming section", xactionRow);

		String rowCellId = xactionRow.getAttribute("id");
		String accountId = rowCellId.substring(rowCellId.indexOf('_') + 1);

		WebElement dateInput = helper.waitForElement(By.id("dateInput_"
				+ accountId));
		dateInput.click();

		// select yesterday by the keyboard
		dateInput.sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.RETURN);

		// wait for things to happen
		Thread.sleep(1000);

		// make sure the row moved to the historic section
		xactionRow = helper
				.waitForElement(By
						.xpath("//table[@id='cashFlowTable']/tbody[@id='historicBody']/tr[@id='"
								+ rowCellId + "']"));
		
		assertNotNull("Expected transaction row in historic section", xactionRow);

		// get the input again
		dateInput = helper.waitForElement(By.id("dateInput_" + accountId));

		// make sure the transaction name is changed
		assertEquals(
				new SimpleDateFormat(FloCalendarHelper.ARIANOW_DATE_FORMAT).format(cal
						.getTime()), dateInput.getAttribute("aria-valuenow"));
	}
}
