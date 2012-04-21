package net.sf.flophase.floweb.xaction;

import java.util.Date;
import java.util.List;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;

/**
 * This interface defines methods for accessing data related to transactions.
 */
public interface TransactionDAO {
	/**
	 * Creates a new transaction under the given cash flow.
	 * 
	 * @param cashflow
	 *            The parent cash fflow
	 * @param name
	 *            The name of the transaction
	 * @param date
	 *            The transaction date
	 * @return The new transaction
	 */
	public abstract Transaction createTransaction(CashFlow cashflow, String name, Date date);

	/**
	 * Deletes the transaction with the given key.
	 * 
	 * @param key
	 *            The transaction key
	 */
	public abstract void deleteTransaction(Key<Transaction> key);

	/**
	 * Updates the transaction with the given key.
	 * 
	 * @param key
	 *            The transaction key
	 * @param name
	 *            The new name
	 * @param date
	 *            The new date
	 * @return The updated transaction
	 */
	public abstract Transaction editTransaction(Key<Transaction> key, String name, Date date);

	/**
	 * Returns all the transaction in a given month.
	 * 
	 * @param cashflow
	 *            The cash flow
	 * @param startDate
	 *            The start date for the month
	 * @return The list of transactions
	 */
	public abstract List<Transaction> getTransactions(CashFlow cashflow, Date startDate);

	/**
	 * Returns the transaction with the given key.
	 * 
	 * @param key
	 *            The key
	 * @return The transaction
	 */
	public abstract Transaction getTransaction(Key<Transaction> key);
}
