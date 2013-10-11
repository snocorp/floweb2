package net.sf.flophase.floweb.account;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static com.googlecode.objectify.ObjectifyService.register;

import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

/**
 * This is the main data access object. It uses Objectify to store and retrieve
 * data.
 */
public class FloAccountDAO implements AccountDAO {

	/**
	 * Register the objects to be stored and loaded.
	 */
	static {
		register(Account.class);
	}

	@Override
	public Result<Void> deleteAccount(Key<Account> key) {
		// delete the account
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
	public Account createAccount(CashFlow cashflow, String name,
			double balance, double negativeThreshold, double positiveThreshold) {
		// create a new account
		Account account = new Account(cashflow.getKey(), name, balance);

		account.setNegativeThreshold(negativeThreshold);
		account.setPositiveThreshold(positiveThreshold);

		// store the account
		ofy().save().entity(account).now();

		return account;
	}
}
