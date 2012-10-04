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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * This class tests that adding accounts works.
 */
public class AddAccountTest {

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
	 * Creates a single account and verifies the name and balance.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testAddSingleAccount() throws Exception {
		
		final long startTime = System.currentTimeMillis();
		
		WebElement addAccountButton = helper.waitForElement(By.linkText("add account"), 1000);
		
		while (addAccountButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				fail("Add account button did not become enabled");
			}
		}
		addAccountButton.click();
		
		WebElement newAccountNameInput = helper.waitForVisibleEnabledElement(By.id("newAccountName"));
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account");
		
		WebElement newAccountBalanceInput = driver.findElement(By.id("newAccountBalance"));
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("12.34");
		
		driver.findElement(By.id("addAccountOk")).click();

		Thread.sleep(2000);
		
		WebElement accountHeaderRow = helper.waitForElement(
				By.id("accountHeaderRow"), 5000);
		List<WebElement> accountEntryHeaderCells = accountHeaderRow.findElements(By
				.tagName("th"));
		
		WebElement newAccountEntryHeaderCell = accountEntryHeaderCells.get(1);

		assertEquals("New Account", newAccountEntryHeaderCell.getText());
		
		String headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		String accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		WebElement currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		assertEquals("$12.34", currentBalanceInput.getAttribute("value"));
	}

	/**
	 * Renames the initially created account using the entry header.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testAddTwoAccounts() throws Exception {
		final long startTime = System.currentTimeMillis();
				
		WebElement addAccountButton = helper.waitForElement(By.linkText("add account"), 1000);
		
		while (addAccountButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				fail("Add account button did not become enabled");
			}
		}
		addAccountButton.click();
		
		WebElement newAccountNameInput = helper.waitForVisibleEnabledElement(By.id("newAccountName"));
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account 1");
		
		WebElement newAccountBalanceInput = driver.findElement(By.id("newAccountBalance"));
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("12.34");
		driver.findElement(By.id("addAccountOk")).click();

		Thread.sleep(2000);
		
		addAccountButton.click();
		
		assertEquals("", newAccountNameInput.getAttribute("value"));
		
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account 2");
		
		assertEquals("$0.00", newAccountBalanceInput.getAttribute("value"));
		
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("56.78");
		driver.findElement(By.id("addAccountOk")).click();

		Thread.sleep(2000);
		
		WebElement accountHeaderRow = helper.waitForElement(
				By.id("accountHeaderRow"), 5000);
		List<WebElement> accountEntryHeaderCells = accountHeaderRow.findElements(By
				.tagName("th"));
		
		WebElement newAccountEntryHeaderCell = accountEntryHeaderCells.get(1);

		assertEquals("New Account 1", newAccountEntryHeaderCell.getText());
		
		String headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		String accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		WebElement currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		assertEquals("$12.34", currentBalanceInput.getAttribute("value"));
		
		newAccountEntryHeaderCell = accountEntryHeaderCells.get(2);

		assertEquals("New Account 2", newAccountEntryHeaderCell.getText());
		
		headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		assertEquals("$56.78", currentBalanceInput.getAttribute("value"));
	}

}
