package net.sf.flophase.floweb.account;

import java.util.List;

/**
 * This interface defines the internal API to manipulate accounts.
 */
public interface AccountStore {
	/**
	 * Returns a list of the accounts for the logged in user.
	 * 
	 * @return The accounts.
	 */
	public List<Account> getAccounts();

	/**
	 * Creates a new account for the logged in user. Sets positive and negative
	 * thresholds to 0.
	 * 
	 * @param name
	 *            The name
	 * @param balance
	 *            The balance
	 * @return The new account
	 */
	public Account createAccount(String name, double balance);

	/**
	 * Creates a new account for the logged in user.
	 * 
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
	public Account createAccount(String name, double balance,
			double negativeThreshold, double positiveThreshold);

	/**
	 * Deletes the account with the given key.
	 * 
	 * @param id
	 *            The key
	 */
	public void deleteAccount(long id);

	/**
	 * Edits the account with the given key.
	 * 
	 * @param id
	 *            The key
	 * @param name
	 *            The name
	 * @param balance
	 *            The balance
	 * @param negativeThreshold
	 *            The threshold at which the account is considered negative
	 * @param positiveThreshold
	 *            The threshold at which the account is considered positive
	 * @return The updated account
	 */
	public Account editAccount(long id, String name, double balance,
			double negativeThreshold, double positiveThreshold);

	/**
	 * Returns the account with the given id.
	 * 
	 * @param id
	 *            The id
	 * @return The account
	 */
	public Account getAccount(long id);
}
