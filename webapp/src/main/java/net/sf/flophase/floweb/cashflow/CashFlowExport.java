package net.sf.flophase.floweb.cashflow;

import java.util.ArrayList;
import java.util.List;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.xaction.FinancialTransaction;

/**
 * This class represents a cash flow and all the underlying data.
 */
public class CashFlowExport {

	/**
	 * The list of transactions
	 */
	private List<FinancialTransaction> transactions = new ArrayList<FinancialTransaction>();

	/**
	 * The list of accounts
	 */
	private List<Account> accounts = new ArrayList<Account>();

	/**
	 * Creates a new CashFlow instance.
	 */
	public CashFlowExport() {
		// empty constructor
	}

	/**
	 * Returns the list of transactions.
	 * 
	 * @return The transactions
	 */
	public List<FinancialTransaction> getTransactions() {
		return transactions;
	}

	/**
	 * Sets the list of transactions.
	 * 
	 * @param transactions
	 *            The transactions
	 */
	public void setTransactions(List<FinancialTransaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * Returns the list of accounts.
	 * 
	 * @return The accounts
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * Sets the list of accounts.
	 * 
	 * @param accounts
	 *            The accounts
	 */
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
}
