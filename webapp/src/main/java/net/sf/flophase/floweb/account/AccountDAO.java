package net.sf.flophase.floweb.account;

import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

/**
 * This interface defines the data access object for the account entity.
 * 
 */
public interface AccountDAO {

	/**
	 * Returns a list of the accounts for the given cash flow.
	 * 
	 * @param cashflow
	 *            The cash flow
	 * @return The accounts.
	 */
	public abstract List<Account> getAccounts(CashFlow cashflow);

	/**
	 * Creates a new account in the given cash flow.
	 * 
	 * @param cashflow
	 *            the cash flow
	 * @param name
	 *            The name
	 * @param balance
	 *            The balance
	 * @param negativeThreshold
	 *            The threshold at which the account is considered negative
	 * @param positiveThreshold
	 *            The threshold at which the account is considered positive
	 * @return The new account
	 */
	public abstract Account createAccount(CashFlow cashflow, String name,
			double balance, double negativeThreshold, double positiveThreshold);

	/**
	 * Deletes the account with the given key.
	 * 
	 * @param key
	 *            The account key
	 * @return The result
	 */
	public abstract Result<Void> deleteAccount(Key<Account> key);

	/**
	 * Edits the account with the given key.
	 * 
	 * @param key
	 *            The key
	 * @param name
	 *            The new name
	 * @param balance
	 *            The new balance
	 * @param negativeThreshold
	 *            The threshold at which the account is considered negative,
	 *            default is 0
	 * @param positiveThreshold
	 *            The threshold at which the account is considered positive,
	 *            default is 0
	 * @return The updated account
	 */
	public abstract Account editAccount(Key<Account> key, String name,
			double balance, double negativeThreshold, double positiveThreshold);

	/**
	 * Returns the account with the given key.
	 * 
	 * @param key
	 *            The key
	 * @return The account
	 */
	public abstract Account getAccount(Key<Account> key);
}
