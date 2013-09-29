package net.sf.flophase.floweb.common;

import static com.googlecode.objectify.ObjectifyService.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryDAO;
import net.sf.flophase.floweb.xaction.Transaction;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This class tests the method of the {@link FloDAO} class that come from
 * {@link EntryDAO}.
 */
public class FloEntryDAOTest extends AbstractDAOTestCase {

	/**
	 * The data access object to be tested.
	 */
	private FloDAO dao;

	/**
	 * The cash flow.
	 */
	private CashFlow cashflow;

	/**
	 * The account.
	 */
	private Account account;

	/**
	 * The transaction.
	 */
	private Transaction xaction;

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
		account = dao.createAccount(cashflow, "Account1", 1.23);
		xaction = dao.createTransaction(cashflow, "Transaction1", new Date());
	}

	/**
	 * Tests the {@link FloDAO#editEntry(Transaction, long, double)} method.
	 * Adds an entry and ensures its details are correct.
	 */
	@Test
	public void testEditEntryCreatesNew() {

		Entry entry = dao.editEntry(xaction, account.getKey().getId(), 2.34);

		assertThat(entry.getAmount(), is(equalTo(2.34)));

		Map<Long, Entry> entries = dao.getEntries(xaction);

		assertThat(entries.get(account.getKey().getId()).getKey(),
				is(equalTo(entry.getKey())));
	}

	/**
	 * Tests the {@link FloDAO#editEntry(Transaction, long, double)} method.
	 * Adds an entry with zero amount and ensures it is not created.
	 */
	@Test
	public void testEditEntryWithZeroAmount() {

		Entry entry = dao.editEntry(xaction, account.getKey().getId(), 0.0);

		assertThat(entry, is(nullValue()));

		Map<Long, Entry> entries = dao.getEntries(xaction);

		assertThat(entries.get(account.getKey()), is(nullValue()));
	}

	/**
	 * Tests the {@link FloDAO#editEntry(Transaction, long, double)} method.
	 * Adds an entry to the data store then updates it and ensures the details
	 * are updated correctly.
	 */
	@Test
	public void testEditEntryWithExisting() {
		Entry entry = new Entry(xaction.getKey(), account.getKey().getId(),
				2.34);

		ofy().save().entity(entry).now();

		Entry updatedEntry = dao.editEntry(xaction, account.getKey().getId(),
				3.45);

		assertThat(updatedEntry.getAmount(), is(equalTo(3.45)));
	}

	/**
	 * Tests the {@link FloDAO#editEntry(Transaction, long, double)} method.
	 * Adds an entry to the data store then updates it to have zero amount and
	 * ensures the entry is removed.
	 */
	@Test
	public void testEditEntryWithExistingToZeroAmount() {
		Entry entry = new Entry(xaction.getKey(), account.getKey().getId(),
				2.34);

		ofy().save().entity(entry).now();

		Entry updatedEntry = dao.editEntry(xaction, account.getKey().getId(),
				0.0);

		assertThat(updatedEntry, is(nullValue()));

		Map<Long, Entry> entries = dao.getEntries(xaction);

		assertThat(entries.get(account.getKey()), is(nullValue()));
	}

}
