package net.sf.flophase.floweb.entry;

import net.sf.flophase.floweb.xaction.Transaction;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class represents an entry in a transaction. There may be one entry per
 * account in each transaction representing the money debited or credited to the
 * account during that transaction.
 */
@Entity
public class Entry {
	/**
	 * The entry identifier.
	 */
	@Id
	private Long key;

	/**
	 * The parent transaction key.
	 */
	@Parent
	private Key<Transaction> xaction;

	/**
	 * The account for the entry.
	 */
	@Index
	private long account;

	/**
	 * The entry amount.
	 */
	private double amount;

	/**
	 * Creates a new entry instance.
	 * 
	 * @param xaction
	 *            The transaction key
	 * @param account
	 *            The account id
	 * @param amount
	 *            The entry amount
	 */
	public Entry(Key<Transaction> xaction, long account, double amount) {
		this.xaction = xaction;
		this.account = account;
		this.amount = amount;
	}

	/**
	 * Creates a new Entry instance.
	 */
	public Entry() {
		// empty constructor
	}

	/**
	 * Returns the account for the transaction.
	 * 
	 * @return The account
	 */
	public long getAccount() {
		return account;
	}

	/**
	 * Returns the entry amount.
	 * 
	 * @return The amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * Sets the amount of the entry.
	 * 
	 * @param amount
	 *            The amount.
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Returns the key of the entry.
	 * 
	 * @return The key
	 */
	public Key<Entry> getKey() {
		return Key.create(xaction, Entry.class, key);
	}
}
