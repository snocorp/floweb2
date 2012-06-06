package net.sf.flophase.floweb.entry;

import net.sf.flophase.floweb.common.Response;

/**
 * This interface defines the public API for manipulating entries.
 */
public interface EntryService {
	/**
	 * Adds an entry to the given transaction for the given account.
	 * 
	 * @param account
	 *            The account key
	 * @param xaction
	 *            The transaction key
	 * @param amount
	 *            The amount of the entry
	 * @return A response containing the new entry
	 */
	public Response<Entry> editEntry(String account, String xaction, String amount);
}
