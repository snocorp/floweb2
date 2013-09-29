package net.sf.flophase.floweb.xaction;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Date;

import net.sf.flophase.floweb.cashflow.CashFlow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * This class tests the {@link Transaction} class.
 */
public class TransactionTest {

	/**
	 * A helper class to allow app engine calls.
	 */
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

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
	 * Tests the {@link Transaction#setName(String)} method.
	 */
	@Test
	public void testSetName() {
		Transaction xaction = new Transaction();

		xaction.setName("Transaction1");

		assertThat(xaction.getName(), is(equalTo("Transaction1")));
	}

	/**
	 * Tests the {@link Transaction#setDate(Date)} method.
	 */
	@Test
	public void testSetDate() {
		Transaction xaction = new Transaction();
		Date date = new Date();

		xaction.setDate(date);

		assertThat(xaction.getDate(), is(equalTo(date)));
	}

	/**
	 * Tests the {@link Transaction#getKey()} method.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Test
	public void testGetKey() throws Exception {
		Key<CashFlow> cashflow = Key.create(CashFlow.class, 1);
		Transaction xaction = new Transaction(cashflow, "Transaction1",
				new Date());

		// set the key using reflection
		Field keyField = Transaction.class.getDeclaredField("key");
		keyField.setAccessible(true);
		keyField.set(xaction, 2L);

		// validate the elements of the key
		assertEquals(cashflow, xaction.getKey().getParent());
		assertEquals(Transaction.class.getSimpleName(), xaction.getKey()
				.getKind());
		assertEquals(2L, xaction.getKey().getId());
	}

}
