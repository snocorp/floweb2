package net.sf.flophase.floweb.account;

import javax.persistence.Id;

import net.sf.flophase.floweb.cashflow.CashFlow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * This class represents an account.
 */
public class Account {
	/**
	 * The account identifier.
	 */
	@Id
	private Long key;

	/**
	 * The cash flow for this account.
	 */
	@Parent
	private Key<CashFlow> cashflow;

	/**
	 * The account name.
	 */
	@Unindexed
	private String name;

	/**
	 * The account balance.
	 */
	@Unindexed
	private double balance;

	/**
	 * Creates a new Account instance.
	 * 
	 * @param cashflow
	 *            The cash flow this account is in
	 * @param name
	 *            The account name
	 * @param balance
	 *            The account balance
	 */
	public Account(Key<CashFlow> cashflow, String name, double balance) {
		super();
		this.cashflow = cashflow;
		this.name = name;
		this.balance = balance;
	}

	/**
	 * Creates a new Account instance.
	 */
	public Account() {
		// empty constructor
	}

	/**
	 * Returns the account key.
	 * 
	 * @return The key
	 */
	public Key<Account> getKey() {
		return new Key<Account>(cashflow, Account.class, key);
	}

	/**
	 * Returns the account name.
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the account name.
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the account balance.
	 * 
	 * @return The balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * Sets the account balance.
	 * 
	 * @param balance
	 *            The balance.
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
