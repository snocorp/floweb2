package net.sf.flophase.floweb.xaction;

import java.util.List;

import net.sf.flophase.floweb.common.Response;

/**
 * This interface defines the public API for manipulating transactions.
 */
public interface TransactionService {

	/**
	 * Returns all the transactions for the given month.
	 * 
	 * @param month
	 *            The month in the format yyyy-mm
	 * @return The transactions for the month
	 */
	public abstract Response<List<FinancialTransaction>> getTransactions(String month);

	/**
	 * Creates a new transaction.
	 * 
	 * @param name
	 *            The name of the transaction
	 * @param date
	 *            The date of the transaction in ISO 8601 format
	 * @return A response containing the new transaction.
	 */
	public abstract Response<FinancialTransaction> addTransaction(String name, String date);

	/**
	 * Copies a transaction.
	 * 
	 * @param key
	 *            The key of the transaction to be copied
	 * @param name
	 *            The new name
	 * @param date
	 *            The new date in ISO 8601 format
	 * @return A response containing the copy of the transaction.
	 */
	public abstract Response<FinancialTransaction> copyTransaction(String key, String name, String date);

	/**
	 * Deletes a transaction.
	 * 
	 * @param key
	 *            The transaction key
	 * @return An empty response
	 */
	public abstract Response<Void> deleteTransaction(String key);

	/**
	 * Edits a transaction.
	 * 
	 * @param key
	 *            The transaction key
	 * @param name
	 *            The name
	 * @param date
	 *            The date in ISO 8601 format
	 * @return A response containing the updated transaction.
	 */
	public abstract Response<Transaction> editTransaction(String key, String name, String date);

}
