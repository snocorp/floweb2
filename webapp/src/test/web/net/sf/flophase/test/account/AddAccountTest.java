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
	 * Sets up the test case. Starts the browser at the right URL. Logs in.
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
		//we need to time this case to allow a timeout if certain interactions fail
		final long startTime = System.currentTimeMillis();
		
		//get the add account button
		WebElement addAccountButton = helper.waitForElement(By.id("addAccount"));
		
		//while the button is marked as disabled (css class)
		while (addAccountButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				fail("Add account button did not become enabled");
			}
		}
		
		//click the add account button to open the add account dialog
		addAccountButton.click();
		
		//get the input for the account name
		WebElement newAccountNameInput = helper.waitForVisibleEnabledElement(By.id("newAccountName"));
		
		//clear the input and type an account name
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account");
		
		//get the input for the account balance
		WebElement newAccountBalanceInput = driver.findElement(By.id("newAccountBalance"));
		
		//clear the input and type a new balance
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("12.34");
		
		//click the OK button
		driver.findElement(By.id("addAccountOk")).click();

		//wait for things to happen
		Thread.sleep(2000);
		
		//we need to find the header cell for the new account
		//start by finding the header row
		WebElement accountHeaderRow = helper.waitForElement(
				By.id("accountHeaderRow"), 5000);
		List<WebElement> accountEntryHeaderCells = accountHeaderRow.findElements(By
				.tagName("th"));
		
		//the second cell should be the new account
		WebElement newAccountEntryHeaderCell = accountEntryHeaderCells.get(1);

		//make sure we got the right one
		assertEquals("New Account", newAccountEntryHeaderCell.getText());
		
		//use the id of the cell to find the account id
		String headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		String accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		//use the account id to find the current balance
		WebElement currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		//make sure the balance is what we put in
		assertEquals("$12.34", currentBalanceInput.getAttribute("value"));
	}

	/**
	 * Adds two accounts and verified their names and balances.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testAddTwoAccounts() throws Exception {
		//we need to time this case to allow a timeout if certain interactions fail
		final long startTime = System.currentTimeMillis();
		
		//get the add account button
		WebElement addAccountButton = helper.waitForElement(By.id("addAccount"));
		
		//while the button is marked as disabled (css class)
		while (addAccountButton.getAttribute("class").contains("disabled")) {
			if (System.currentTimeMillis() > startTime + 5000) {
				fail("Add account button did not become enabled");
			}
		}
		
		//click the add account button to open the add account dialog
		addAccountButton.click();
		
		//get the input for the account name
		WebElement newAccountNameInput = helper.waitForVisibleEnabledElement(By.id("newAccountName"));
		
		//clear the input and type an account name
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account 1");
		
		//get the input for the account balance
		WebElement newAccountBalanceInput = driver.findElement(By.id("newAccountBalance"));
		
		//clear the input and type a new balance
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("12.34");
		
		//click the OK button
		driver.findElement(By.id("addAccountOk")).click();

		//wait for things to happen
		Thread.sleep(2000);
		
		//click the add account button to open the add account dialog
		addAccountButton.click();
		
		//make sure the name input was cleared
		assertEquals("", newAccountNameInput.getAttribute("value"));
		
		//clear the input and type a new balance
		newAccountNameInput.clear();
		newAccountNameInput.sendKeys("New Account 2");
		
		//make sure the default balance was reset to $0.00
		assertEquals("$0.00", newAccountBalanceInput.getAttribute("value"));
		
		//clear the input and type a new balance
		newAccountBalanceInput.clear();
		newAccountBalanceInput.sendKeys("56.78");
		
		//click the OK button
		driver.findElement(By.id("addAccountOk")).click();

		//wait for things to happen
		Thread.sleep(2000);
		
		//we need to find the header cell for the new account
		//start by finding the header row
		WebElement accountHeaderRow = helper.waitForElement(By.id("accountHeaderRow"));
		List<WebElement> accountEntryHeaderCells = accountHeaderRow.findElements(By
				.tagName("th"));
		
		//the second cell is the first account we created
		WebElement newAccountEntryHeaderCell = accountEntryHeaderCells.get(1);

		//make sure the cell has the right account name
		assertEquals("New Account 1", newAccountEntryHeaderCell.getText());
		
		//use the id of the cell to get the account id
		String headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		String accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		//use the account id to find the current balance
		WebElement currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		//make sure the balance is the first value we entered
		assertEquals("$12.34", currentBalanceInput.getAttribute("value"));
		
		//get the third header cell for the second account we created
		newAccountEntryHeaderCell = accountEntryHeaderCells.get(2);

		//make sure the cell has the right account name
		assertEquals("New Account 2", newAccountEntryHeaderCell.getText());
		
		//use the id of the cell to get the account id
		headerCellId = newAccountEntryHeaderCell.getAttribute("id");
		accountId = headerCellId.substring(headerCellId.indexOf('_')+1);
		
		//use the account id to find the current balance
		currentBalanceInput = driver.findElement(By.id("currbalInput_"+accountId));
		
		//make sure the balance is the first value we entered
		assertEquals("$56.78", currentBalanceInput.getAttribute("value"));
	}

}
