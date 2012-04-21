package net.sf.flophase.floweb.xaction;

import java.util.Date;
import java.util.List;

/**
 * This interface defines the internal API to manipulate transactions.
 */
public interface TransactionStore {
	/**
	 * Creates a new transaction.
	 * 
	 * @param name
	 *            The name
	 * @param date
	 *            The date
	 * @return The new transaction.
	 */
	public Transaction createTransaction(String name, Date date);

	/**
	 * Deletes the transaction with the given id.
	 * 
	 * @param id
	 *            The transaction id.
	 */
	public void deleteTransaction(long id);

	/**
	 * Updates the transaction with the given id.
	 * 
	 * @param id
	 *            The transaction id
	 * @param name
	 *            The new name
	 * @param date
	 *            The new date
	 * @return The updated transaction
	 */
	public Transaction editTransaction(long id, String name, Date date);

	/**
	 * Returns all the transactions for the month in which the given date falls.
	 * 
	 * @param startDate
	 *            The date
	 * @return The list of transactions
	 */
	public List<FinancialTransaction> getTransactions(Date startDate);

	/**
	 * Gets the transaction with the given id.
	 * 
	 * @param id
	 *            The id
	 * @return The transaction.
	 */
	public Transaction getTransaction(long id);
}
