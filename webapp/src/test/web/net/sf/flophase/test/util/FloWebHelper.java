package net.sf.flophase.test.util;

import java.util.UUID;

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
		driver.findElement(By.linkText("login")).click();
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
	
	public WebElement waitForElement(By by, long milliseconds) {
		long startTime = System.currentTimeMillis();
		while (true) {
			try {
				return driver.findElement(by);
			} catch (NoSuchElementException e) {
				if (System.currentTimeMillis() > startTime + milliseconds) {
					throw e;
				}
			}
		}
	}

	public void logout() {
		WebElement logoutLink = driver.findElement(By.id("logoutLink"));
		logoutLink.click();
	}
}
