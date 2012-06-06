package net.sf.flophase.floweb.entry;

import java.util.Map;

import net.sf.flophase.floweb.xaction.Transaction;

/**
 * This interface defines the internal API to manipulate entries.
 */
public interface EntryStore {

	/**
	 * Edits or creates a new entry for the given account to the given transaction.
	 * 
	 * @param accountId
	 *            The account id
	 * @param xactionId
	 *            The transaction id
	 * @param amount
	 *            The entry amount
	 * @return The new entry
	 */
	public abstract Entry editEntry(long accountId, long xactionId, double amount);

	/**
	 * Returns the entries for the given transaction.
	 * 
	 * @param xaction
	 *            The transaction
	 * @return The entry map of account id to entry
	 */
	public abstract Map<Long, Entry> getEntries(Transaction xaction);

}
