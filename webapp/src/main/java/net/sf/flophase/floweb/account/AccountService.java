package net.sf.flophase.floweb.account;

import java.util.List;

import net.sf.flophase.floweb.common.Response;

/**
 * This interface defines the public API for manipulating accounts.
 */
public interface AccountService {
	/**
	 * Returns a list of the accounts for the logged in user.
	 * 
	 * @return A response containing the accounts.
	 */
	public Response<List<Account>> getAccounts();

	/**
	 * Creates a new account for the logged in user.
	 * 
	 * @param name
	 *            The name
	 * @param balance
	 *            The balance
	 * @return A response containing the new account
	 */
	public Response<Account> addAccount(String name, String balance);

	/**
	 * Deletes the account with the given key.
	 * 
	 * @param key
	 *            The key
	 * @return An empty reponse.
	 */
	public Response<Void> deleteAccount(String key);

	/**
	 * Edits the account with the given key.
	 * 
	 * @param key
	 *            The key
	 * @param name
	 *            The name
	 * @param balance
	 *            The balance
	 * @return A response containing the updated account
	 */
	public Response<Account> editAccount(String key, String name, String balance);
}
