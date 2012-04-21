package net.sf.flophase.floweb.entry;

import java.util.Map;

import net.sf.flophase.floweb.xaction.Transaction;

/**
 * This interface provides the methods for accessing and manipulating data for entries.
 */
public interface EntryDAO {

	/**
	 * Edits an entry or creates a new entry of it does not exist. Will return the entry or null if the amount is 0.0.
	 * 
	 * @param xaction
	 *            The transaction to which the entry belongs
	 * @param account
	 *            The id of the entry's account
	 * @param amount
	 *            The amount
	 * @return The new entry
	 */
	public abstract Entry editEntry(Transaction xaction, long account, double amount);

	/**
	 * Returns the entries for the given transaction.
	 * 
	 * @param xaction
	 *            The transaction
	 * @return The entry map of account id to entry
	 */
	public abstract Map<Long, Entry> getEntries(Transaction xaction);

}
