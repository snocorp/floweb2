package net.sf.flophase.floweb.xaction;

import java.util.Collections;
import java.util.Map;

import net.sf.flophase.floweb.entry.Entry;

/**
 * This is a wrapper object that represents a transaction that has entries
 * against one or more accounts.
 */
public class FinancialTransaction {

	/**
	 * The transaction details.
	 */
	private Transaction details;

	/**
	 * The entries for this transaction.
	 */
	private Map<Long, Entry> entries;

	/**
	 * Creates a new {@link FinancialTransaction} instance.
	 */
	public FinancialTransaction() {
		// no-arg constructor
	}

	/**
	 * Creates a new {@link FinancialTransaction} instance.
	 * 
	 * @param xaction
	 *            The transaction details
	 * @param entries
	 *            The entries
	 */
	public FinancialTransaction(Transaction xaction, Map<Long, Entry> entries) {
		this.details = xaction;
		this.entries = entries;
	}

	/**
	 * Returns the transaction details.
	 * 
	 * @return The details
	 */
	public Transaction getDetails() {
		return details;
	}

	/**
	 * The entry map.
	 * 
	 * @return The entries
	 */
	public Map<Long, Entry> getEntries() {
		return Collections.unmodifiableMap(entries);
	}
}
