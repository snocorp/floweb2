package net.sf.flophase.test.util;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This is a helper class for web unit tests.
 */
public class FloWebHelper {

	public static final String BASE_URL = "http://localhost:8888/";
	/**
	 * The selenium web driver
	 */
	private final WebDriver driver;

	/**
	 * Creates a new {@link FloWebHelper} instance.
	 * 
	 * @param driver
	 *            The selenium driver
	 */
	public FloWebHelper(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Logs in the given user.
	 * 
	 * @param email
	 *            The user's email address
	 */
	public void login(String email) {
		waitForElement(By.linkText("login")).click();
		waitForElement(By.id("email"), 2000).clear();
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.name("action")).click();
	}

	/**
	 * Gets a random email address.
	 * 
	 * @return An email address
	 */
	public String getEmail() {
		return UUID.randomUUID().toString() + "@test.com";
	}

	public void start() {
		driver.get(BASE_URL);
	}

	private WebElement waitForElement(By by, boolean waitForVisible,
			boolean waitForEnabled, long milliseconds) {
		final long startTime = System.currentTimeMillis();
		WebElement element;
		while (true) {
			try {
				element = driver.findElement(by);

				break;
			} catch (NoSuchElementException e) {
				if (System.currentTimeMillis() > startTime + milliseconds) {
					throw e;
				}
			}
		}
		while (waitForVisible) {
			if (element.isDisplayed()
					|| System.currentTimeMillis() > startTime + milliseconds) {
				break;
			}
		}
		while (waitForEnabled) {
			if (element.isEnabled()
					|| System.currentTimeMillis() > startTime + milliseconds) {
				break;
			}
		}
		return element;
	}

	public WebElement waitForElement(By by, long milliseconds) {
		return waitForElement(by, false, false, milliseconds);
	}

	public WebElement waitForEnabledElement(By by) {
		return waitForElement(by, false, true, 5000);
	}

	public WebElement waitForVisibleElement(By by) {
		return waitForElement(by, true, false, 5000);
	}

	public WebElement waitForVisibleEnabledElement(By by) {
		return waitForElement(by, true, true, 5000);
	}

	public WebElement waitForElement(By by) {
		return waitForElement(by, 5000);
	}

	public void logout() {
		WebElement logoutLink = waitForElement(By.id("logoutLink"));
		logoutLink.click();
	}

	public void addAccount(String name, double balance)
			throws InterruptedException {
		// we need to time this case to allow a timeout if certain interactions
		// fail
		final long startTime = System.currentTimeMillis();

		// get the add account button
		WebElement addAccountButton = waitForElement(By.id("addAccount"));

		// while the button is marked as disabled (css class)
		while (addAccountButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				Assert.fail("Add account button did not become enabled");
			}
		}

		// click the add account button to open the add account dialog
		addAccountButton.click();

		// get the input for the account name
		WebElement newAccountNameInput = waitForVisibleEnabledElement(By
				.id("newAccountName"));

		// clear the input and type an account name
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys(name);

		// get the input for the account balance
		WebElement newAccountBalanceInput = driver.findElement(By
				.id("newAccountBalance"));

		// make sure the default balance was reset to $0.00
		assertEquals("$0.00", newAccountBalanceInput.getAttribute("value"));

		// clear the input and type a new balance
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys(String.valueOf(balance));

		// click the OK button
		driver.findElement(By.id("addAccountOk")).click();

		// wait for things to happen
		Thread.sleep(2000);
	}

	/**
	 * Adds a transaction to the system with the given name for today's date.
	 * 
	 * @param name
	 *            The transaction's name
	 * @throws Exception
	 *             If an error occurs.
	 */
	public void addTransaction(String name) throws Exception {
		Calendar cal = Calendar.getInstance();

		addTransaction(name, cal);
	}

	/**
	 * Adds a transaction to the system with the given name on the calendar's
	 * date.
	 * 
	 * @param name
	 *            The transaction name
	 * @param cal
	 *            The calendar
	 * @throws Exception
	 *             If an error occurs
	 */
	public void addTransaction(String name, Calendar cal) throws Exception {
		// we need to time this case to allow a timeout if certain interactions
		// fail
		final long startTime = System.currentTimeMillis();

		// get the add transaction button
		WebElement addTransactionButton = waitForElement(By
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
		WebElement newTransactionNameInput = waitForVisibleEnabledElement(By
				.id("newTransactionName"));

		// clear the value and input a new name
		newTransactionNameInput.clear();
		newTransactionNameInput.sendKeys(name);
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		
		WebElement displayedYear = driver.findElement(
				By.id("newTransactionCalendar_year"));
		
		int compare;
		while ((compare = year.compareTo(displayedYear.getText())) != 0) {
			WebElement yearElement = driver.findElement(By.cssSelector(compare < 0 ? "span.dijitCalendarPreviousYear" : "span.dijitCalendarNextYear"));
			yearElement.click();
			
			//wait a little bit
			Thread.sleep(200);
		}

		int month = cal.get(Calendar.MONTH);

		// open the month selector
		driver.findElement(
				By.cssSelector("#newTransactionCalendar span.dijitArrowButtonInner"))
				.click();

		// wait for things to happen
		Thread.sleep(500);

		driver.findElement(
				By.xpath("//div[@id='newTransactionCalendar_mddb_mdd']/div[@month='"
						+ month + "']")).click();

		// wait for things to happen
		Thread.sleep(500);

		// find the week and day of the week for today
		int week = cal.get(Calendar.WEEK_OF_MONTH);
		int day = cal.get(Calendar.DAY_OF_WEEK);

		// find the date on the calendar
		driver.findElement(
				By.xpath("//table[@id='newTransactionCalendar']/tbody/tr["
						+ week + "]/td[" + day + "]/span")).click();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(cal.getTime());

		// ensure the hidden input was updated
		WebElement newTransactionDateInput = waitForElement(By
				.id("newTransactionDate"));
		assertEquals(date, newTransactionDateInput.getAttribute("value"));

		// click the ok button
		driver.findElement(By.id("addTransactionOk")).click();

		// wait for things to happen
		Thread.sleep(2000);
	}
	
	public void updateTransaction(String xactionId, Calendar cal) throws Exception {

		WebElement dateInput = waitForElement(By.id("dateInput_"
				+ xactionId));
		dateInput.click();

		// wait for things to happen
		Thread.sleep(500);
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		
		WebElement displayedYear = driver.findElement(
				By.id("dateInput_" + xactionId + "_popup_year"));
		
		int compare;
		while ((compare = year.compareTo(displayedYear.getText())) != 0) {
			WebElement yearElement = driver.findElement(By.cssSelector(compare < 0 ? "span.dijitCalendarPreviousYear" : "span.dijitCalendarNextYear"));
			yearElement.click();
			
			//wait a little bit
			Thread.sleep(200);
		}

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
		Thread.sleep(500);
	}
}
