package net.sf.flophase.floweb.common;

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
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

/**
 * This is the main data access object. It uses Objectify to store and retrieve data.
 */
public class FloDAO extends DAOBase implements DAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		ObjectifyService.register(CashFlow.class);
		ObjectifyService.register(Account.class);
		ObjectifyService.register(Transaction.class);
		ObjectifyService.register(Entry.class);
	}

	@Override
	public Entry editEntry(Transaction xaction, long account, double amount) {
		Objectify ofy = ofy();

		Entry entry = getEntries(xaction).get(account);
		if (entry == null) {
			if (amount != 0.0) {
				// create the entry
				entry = new Entry(xaction.getKey(), account, amount);

				ofy.put(xaction, entry);
			}
		} else {
			if (amount != 0.0) {
				entry.setAmount(amount);

				// store the entry
				ofy.put(entry);
			} else {
				ofy.delete(entry);

				entry = null;
			}
		}

		return entry;
	}

	@Override
	public Account createAccount(CashFlow cashflow, String name, double balance) {
		Objectify ofy = ofy();

		// create a new account
		Account account = new Account(cashflow.getKey(), name, balance);

		// store the account
		ofy.put(account);

		return account;
	}

	@Override
	public CashFlow createCashFlow(User user) {
		Objectify ofy = ofy();

		// create a new cash flow
		CashFlow cashflow = new CashFlow(user);

		// store the cash flow
		ofy.put(cashflow);

		return cashflow;
	}

	@Override
	public Transaction createTransaction(CashFlow cashflow, String name, Date date) {
		Objectify ofy = ofy();

		// create the new transaction
		Transaction xaction = new Transaction(cashflow.getKey(), name, date);

		// store the transaction
		ofy.put(xaction);

		return xaction;
	}

	@Override
	public void deleteAccount(Key<Account> key) {
		Objectify ofy = ofy();

		// delete the account
		ofy.delete(key);
	}

	@Override
	public void deleteTransaction(Key<Transaction> key) {
		Objectify ofy = ofy();

		// delete the transaction
		ofy.delete(key);
	}

	@Override
	public Account editAccount(Key<Account> key, String name, double balance, double negativeThreshold, double positiveThreshold) {
		Objectify ofy = ofy();

		// load the existing account
		Account account = ofy.get(key);

		// set the new name
		account.setName(name);

		// set the new balance
		account.setBalance(balance);
		
		//set the thresholds
		account.setNegativeThreshold(negativeThreshold);
		account.setPositiveThreshold(positiveThreshold);

		// store the updated account
		ofy.put(account);

		return account;
	}

	@Override
	public Transaction editTransaction(Key<Transaction> key, String name, Date date) {
		Objectify ofy = ofy();

		// load the transaction
		Transaction xaction = ofy.get(key);

		// update the transaction details
		xaction.setName(name);
		xaction.setDate(date);

		// store the transaction
		ofy.put(xaction);

		return xaction;
	}

	@Override
	public Account getAccount(Key<Account> key) {
		Objectify ofy = ofy();

		Account account = ofy.get(key);

		return account;
	}

	@Override
	public List<Account> getAccounts(CashFlow cashflow) {
		Objectify ofy = ofy();

		// query for all accounts that are under the given cash flow
		QueryResultIterable<Account> results = ofy.query(Account.class).ancestor(cashflow).fetch();

		// add the results to a list
		List<Account> accounts = new ArrayList<Account>();
		for (Account account : results) {
			accounts.add(account);
		}

		return accounts;
	}

	@Override
	public CashFlow getCashFlow(User user) {
		Objectify ofy = ofy();

		// find the single cash flow associated with the current user
		Query<CashFlow> query = ofy.query(CashFlow.class).filter("user = ", user);

		return query.get();
	}

	@Override
	public Transaction getTransaction(Key<Transaction> key) {
		Objectify ofy = ofy();

		Transaction xaction = ofy.get(key);

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

		Objectify ofy = ofy();

		// query for all transactions that are under the given cash flow that are between the start date (inclusive) and
		// end date (exclusive)
		QueryResultIterable<Transaction> results = ofy.query(Transaction.class).ancestor(cashflow)
		        .filter("date >=", startDate).filter("date <", endDate).fetch();

		// add the results to a list
		List<Transaction> xactions = new ArrayList<Transaction>();
		for (Transaction xaction : results) {
			xactions.add(xaction);
		}

		return xactions;
	}

	@Override
	public Map<Long, Entry> getEntries(Transaction xaction) {
		Objectify ofy = ofy();

		QueryResultIterable<Entry> results = ofy.query(Entry.class).ancestor(xaction).fetch();

		Map<Long, Entry> entries = new HashMap<Long, Entry>();
		for (Entry entry : results) {
			entries.put(entry.getAccount(), entry);
		}

		return entries;
	}
}
