package net.sf.flophase.floweb.common;

import static com.googlecode.objectify.ObjectifyService.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountDAO;
import net.sf.flophase.floweb.cashflow.CashFlow;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This class tests the method of the {@link FloDAO} class that come from
 * {@link AccountDAO}.
 */
public class FloAccountDAOTest extends AbstractDAOTestCase {

	/**
	 * The data access object to be tested.
	 */
	private FloDAO dao;

	/**
	 * The cash flow
	 */
	private CashFlow cashflow;

	/**
	 * Sets up the test case. Creates the data acces object.
	 * 
	 * @throws Exception
	 *             If an error occurs.
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		dao = new FloDAO();

		User user = UserServiceFactory.getUserService().getCurrentUser();

		cashflow = dao.createCashFlow(user);
	}

	/**
	 * Tests the {@link FloDAO#getAccounts(CashFlow)} method. Adds two accounts
	 * to the data store. Ensures that both accounts are returned.
	 */
	@Test
	public void testGetAccounts() {
		Account account1 = new Account(cashflow.getKey(), "Account1", 1.23);
		Account account2 = new Account(cashflow.getKey(), "Account2", 2.34);

		ofy().save().entity(account1).now();
		ofy().save().entity(account2).now();

		List<Account> accounts = dao.getAccounts(cashflow);

		assertThat(accounts.size(), is(equalTo(2)));

		boolean account1Found = false, account2Found = false;
		for (Account account : accounts) {
			if (account.getKey().equals(account1.getKey())) {
				assertThat(account.getName(), is(equalTo("Account1")));
				assertThat(account.getBalance(), is(equalTo(1.23)));

				account1Found = true;
			} else if (account.getKey().equals(account2.getKey())) {
				assertThat(account.getName(), is(equalTo("Account2")));
				assertThat(account.getBalance(), is(equalTo(2.34)));

				account2Found = true;
			}
		}

		assertTrue("Account 1 Found", account1Found);
		assertTrue("Account 2 Found", account2Found);
	}

	/**
	 * Tests the {@link FloDAO#createAccount(CashFlow, String, double)} method.
	 * Creates an account and validates it was created correctly.
	 */
	@Test
	public void testCreateAccount() {
		Account account = dao.createAccount(cashflow, "Account1", 1.23);

		assertThat(account.getName(), is(equalTo("Account1")));
		assertThat(account.getBalance(), is(equalTo(1.23)));

		account = dao.getAccount(account.getKey());

		// make sure we got the account back out of the data store
		assertThat(account, is(not(nullValue())));
	}

	/**
	 * Tests the {@link FloDAO#deleteAccount(com.googlecode.objectify.Key)}
	 * method. Adds an account to the data store then deletes it and ensures the
	 * account cannot be retrieved.
	 */
	@Test
	public void testDeleteAccount() {
		Account account = new Account(cashflow.getKey(), "Account1", 1.23);

		ofy().save().entity(account).now();

		dao.deleteAccount(account.getKey()).now();

		assertNull(dao.getAccount(account.getKey()));
	}

	/**
	 * Tests the
	 * {@link FloDAO#editAccount(com.googlecode.objectify.Key, String, double, double, double)}
	 * method. Adds an account to the data store then updates it and ensures the
	 * details are updated.
	 */
	@Test
	public void testEditAccount() {
		Account account = new Account(cashflow.getKey(), "Account1", 1.23);

		ofy().save().entity(account).now();

		Account updatedAccount = dao.editAccount(account.getKey(), "Account2",
				2.34, 99.0, 1000.0);

		assertThat(updatedAccount.getName(), is(equalTo("Account2")));
		assertThat(updatedAccount.getBalance(), is(equalTo(2.34)));

		updatedAccount = dao.getAccount(account.getKey());

		assertThat(updatedAccount.getName(), is(equalTo("Account2")));
		assertThat(updatedAccount.getBalance(), is(equalTo(2.34)));
		assertThat(updatedAccount.getNegativeThreshold(), is(equalTo(99.0)));
		assertThat(updatedAccount.getPositiveThreshold(), is(equalTo(1000.0)));
	}

	/**
	 * Tests the {@link FloDAO#getAccount(com.googlecode.objectify.Key)} method.
	 * Adds an account to the data store then retrieves it and ensures the
	 * details are correct.
	 */
	@Test
	public void testGetAccount() {
		Account account = new Account(cashflow.getKey(), "Account1", 1.23);

		ofy().save().entity(account).now();

		Account acct = dao.getAccount(account.getKey());

		assertThat(acct.getKey(), is(equalTo(account.getKey())));
		assertThat(acct.getName(), is(equalTo(account.getName())));
		assertThat(acct.getBalance(), is(equalTo(account.getBalance())));
	}

}
