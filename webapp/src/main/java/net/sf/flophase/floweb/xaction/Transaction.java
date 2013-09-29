package net.sf.flophase.floweb.xaction;

import java.util.Date;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class represents a transaction.
 */
@Entity
public class Transaction {
	/**
	 * The transaction identifier.
	 */
	@Id
	private Long key;

	/**
	 * The cash flow for this transaction.
	 */
	@Parent
	private Key<CashFlow> cashflow;

	/**
	 * The transaction name.
	 */
	private String name;

	/**
	 * The transaction date.
	 */
	@Index
	private Date date;

	/**
	 * Creates a new Transaction instance.
	 */
	public Transaction() {
		// empty constructor
	}

	/**
	 * Creates a new Transaction instance.
	 * 
	 * @param cashflow
	 *            The parent cash flow key
	 * @param name
	 *            The transaction name.
	 * @param date
	 *            The date of the transaction.
	 */
	public Transaction(Key<CashFlow> cashflow, String name, Date date) {
		this.cashflow = cashflow;
		this.name = name;
		this.date = date;
	}

	/**
	 * Returns the transaction date.
	 * 
	 * @return The date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the transaction key.
	 * 
	 * @return The key
	 */
	public Key<Transaction> getKey() {
		return Key.create(cashflow, Transaction.class, key);
	}

	/**
	 * Returns the transaction name.
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the transaction date.
	 * 
	 * @param date
	 *            The date.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the transaction name.
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
