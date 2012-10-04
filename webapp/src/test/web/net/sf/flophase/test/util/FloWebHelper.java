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
	
	private WebElement waitForElement(By by, boolean waitForVisible, boolean waitForEnabled, long milliseconds) {
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
			if (element.isDisplayed() || System.currentTimeMillis() > startTime + milliseconds) {
				break;
			}
		}
		while (waitForEnabled) {
			if (element.isEnabled() || System.currentTimeMillis() > startTime + milliseconds) {
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
		WebElement logoutLink = driver.findElement(By.id("logoutLink"));
		logoutLink.click();
	}
}
