package net.sf.flophase.test.util;

import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This is a helper class for web unit tests.
 */
public class FloWebHelper {
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
		driver.findElement(By.id("email")).clear();
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
}
