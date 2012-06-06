package net.sf.flophase.floweb.account;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;

import net.sf.flophase.floweb.cashflow.CashFlow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link Account} class.
 */
public class AccountTest {

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	/**
	 * Sets up the test case. Sets up the app engine helper.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	/**
	 * Tears down the test case. Tears down the app engine helper.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	/**
	 * Tests the {@link Account#getKey()} method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testGetKey() throws Exception {
		Key<CashFlow> cashflow = new Key<CashFlow>(CashFlow.class, 1);
		Account account = new Account(cashflow, "Account1", 1.23);

		// set the key using reflection
		Field keyField = Account.class.getDeclaredField("key");
		keyField.setAccessible(true);
		keyField.set(account, 2L);

		// validate the elements of the key
		assertEquals(cashflow, account.getKey().getParent());
		assertEquals(Account.class.getSimpleName(), account.getKey().getKind());
		assertEquals(2L, account.getKey().getId());
	}

	/**
	 * Tests the {@link Account#setName(String)} method.
	 */
	@Test
	public void testSetName() {
		Account account = new Account();

		account.setName("Account1");

		assertThat(account.getName(), is(equalTo("Account1")));
	}

	/**
	 * Tests the {@link Account#setBalance(double)} method.
	 */
	@Test
	public void testSetBalance() {
		Account account = new Account();

		account.setBalance(1.23);

		assertThat(account.getBalance(), is(equalTo(1.23)));
	}

}
