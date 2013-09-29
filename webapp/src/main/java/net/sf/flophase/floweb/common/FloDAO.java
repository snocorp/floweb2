package net.sf.flophase.floweb.common;

import static com.googlecode.objectify.ObjectifyService.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.xaction.Transaction;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Query;

/**
 * This is the main data access object. It uses Objectify to store and retrieve
 * data.
 */
public class FloDAO implements DAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		register(CashFlow.class);
		register(Account.class);
		register(Transaction.class);
		register(Entry.class);
	}

	@Override
	public Entry editEntry(Transaction xaction, long account, double amount) {
		Entry entry = getEntries(xaction).get(account);
		if (entry == null) {
			if (amount != 0.0) {
				// create the entry
				entry = new Entry(xaction.getKey(), account, amount);

				ofy().save().entity(entry).now();
			}
		} else {
			if (amount != 0.0) {
				entry.setAmount(amount);

				// store the entry
				ofy().save().entity(entry);
			} else {
				ofy().delete().entity(entry);

				entry = null;
			}
		}

		return entry;
	}

	@Override
	public Account createAccount(CashFlow cashflow, String name, double balance) {
		// create a new account
		Account account = new Account(cashflow.getKey(), name, balance);

		// store the account
		ofy().save().entity(account).now();

		return account;
	}

	@Override
	public CashFlow createCashFlow(User user) {
		// create a new cash flow
		CashFlow cashflow = new CashFlow(user);

		// store the cash flow
		ofy().save().entity(cashflow).now();

		return cashflow;
	}

	@Override
	public Transaction createTransaction(CashFlow cashflow, String name,
			Date date) {

		// create the new transaction
		Transaction xaction = new Transaction(cashflow.getKey(), name, date);

		// store the transaction
		ofy().save().entity(xaction).now();

		return xaction;
	}

	@Override
	public Result<Void> deleteAccount(Key<Account> key) {
		// delete the account
		return ofy().delete().entity(key);
	}

	@Override
	public Result<Void> deleteTransaction(Key<Transaction> key) {
		// delete the transaction
		return ofy().delete().entity(key);
	}

	@Override
	public Account editAccount(Key<Account> key, String name, double balance,
			double negativeThreshold, double positiveThreshold) {
		// load the existing account
		Account account = ofy().load().key(key).now();

		// set the new name
		account.setName(name);

		// set the new balance
		account.setBalance(balance);

		// set the thresholds
		account.setNegativeThreshold(negativeThreshold);
		account.setPositiveThreshold(positiveThreshold);

		// store the updated account
		ofy().save().entity(account);

		return account;
	}

	@Override
	public Transaction editTransaction(Key<Transaction> key, String name,
			Date date) {
		// load the transaction
		Transaction xaction = ofy().load().key(key).now();

		// update the transaction details
		xaction.setName(name);
		xaction.setDate(date);

		// store the transaction
		ofy().save().entity(xaction);

		return xaction;
	}

	@Override
	public Account getAccount(Key<Account> key) {
		Account account = ofy().load().key(key).now();

		return account;
	}

	@Override
	public List<Account> getAccounts(CashFlow cashflow) {
		// query for all accounts that are under the given cash flow
		return ofy().load().type(Account.class).ancestor(cashflow).list();
	}

	@Override
	public CashFlow getCashFlow(User user) {
		// find the single cash flow associated with the current user
		Query<CashFlow> query = ofy().load().type(CashFlow.class)
				.filter("user = ", user);

		return query.first().now();
	}

	@Override
	public Transaction getTransaction(Key<Transaction> key) {
		Transaction xaction = ofy().load().key(key).now();

		return xaction;
	}

	@Override
	public List<Transaction> getTransactions(CashFlow cashflow, Date startDate) {
		GregorianCalendar calendar = new GregorianCalendar();

		// begin with the start date
		calendar.setTime(startDate);

		// add one month
		calendar.add(Calendar.MONTH, 1);

		// store the end date
		Date endDate = calendar.getTime();

		// query for all transactions that are under the given cash flow that
		// are between the start date (inclusive) and
		// end date (exclusive)
		final Query<Transaction> query = ofy().load().type(Transaction.class)
				.ancestor(cashflow).filter("date >=", startDate)
				.filter("date <", endDate);

		return query.list();
	}

	@Override
	public Map<Long, Entry> getEntries(Transaction xaction) {
		QueryResultIterable<Entry> results = ofy().load().type(Entry.class)
				.ancestor(xaction).iterable();

		Map<Long, Entry> entries = new HashMap<Long, Entry>();
		for (Entry entry : results) {
			entries.put(entry.getAccount(), entry);
		}

		return entries;
	}
}
