package net.sf.flophase.test.xaction;

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
 * This class tests that renaming transactions works.
 */
public class DeleteTransactionTest {

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
	 * Creates two transactions for today and deletes the first one.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testDeleteTransactionToday() throws Exception {
		helper.addTransaction("Transaction 1");

		// find the transaction name cell that was created
		WebElement xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		// make sure the transaction name is correct
		assertEquals("Transaction 1", xactionNameCell.getText());
		
		//create another transaction
		helper.addTransaction("Transaction 2");
		
		//click the cell to open the edit transaction dialog
		xactionNameCell.click();
		
		//get the name input
		WebElement xactionDeleteButton = helper.waitForVisibleElement(By.id("xactionDelete"));
		
		//click the button
		xactionDeleteButton.click();

		// wait for things to happen
		Thread.sleep(2000);
		
		//get the cell again
		List<WebElement> xactionNameCells = driver.findElements(By
				.className("flo-xactionname"));

		// make sure the transaction is gone, and only one left
		assertEquals(1, xactionNameCells.size());
		
		// make sure the leftover transaction name is correct
		assertEquals("Transaction 2", xactionNameCells.get(0).getText());
	}

	/**
	 * Creates a transaction and deletes it.
	 * 
	 * @throws Exception
	 *             If an error occurs
	 */
	@Test
	public void testDeleteLastTransaction() throws Exception {
		helper.addTransaction("Transaction 1");

		// find the transaction name cell that was created
		WebElement xactionNameCell = driver.findElement(By
				.className("flo-xactionname"));

		// make sure the transaction name is correct
		assertEquals("Transaction 1", xactionNameCell.getText());
		
		//click the cell to open the edit transaction dialog
		xactionNameCell.click();
		
		//get the name input
		WebElement xactionDeleteButton = helper.waitForVisibleElement(By.id("xactionDelete"));
		
		//click the button
		xactionDeleteButton.click();

		// wait for things to happen
		Thread.sleep(2000);
		
		//get the cell again
		List<WebElement> xactionNameCells = driver.findElements(By
				.className("flo-xactionname"));

		// make sure the transaction is gone
		assertTrue(xactionNameCells.isEmpty());
	}

}
