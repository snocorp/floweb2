package net.sf.flophase.std.xaction;

import static org.junit.Assert.*;
import net.sf.flophase.std.util.FloWebHelper;

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
 * This class tests that renaming transactions works.
 */
public class RenameTransactionTest {

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
	 * Creates a transaction and renames it.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testRenameTransaction() throws Exception {
		helper.addTransaction("Transaction 1");

		// find the transaction name cell that was created
		WebElement xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		// make sure the transaction name is correct
		assertEquals("Transaction 1", xactionNameCell.getText());
		
		//click the cell to open the edit transaction dialog
		xactionNameCell.click();
		
		//get the name input
		WebElement xactionNameInput = helper.waitForVisibleElement(By.id("xactionName"));
		
		//clear the value and enter a new name
		xactionNameInput.clear();
		xactionNameInput.sendKeys("Renamed Transaction", Keys.RETURN);

		// wait for things to happen
		Thread.sleep(2000);
		
		//get the cell again
		xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		// make sure the transaction name is changed
		assertEquals("Renamed Transaction", xactionNameCell.getText());
	}

}
