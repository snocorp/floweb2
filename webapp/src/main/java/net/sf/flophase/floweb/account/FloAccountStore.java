package net.sf.flophase.floweb.account;

import java.util.List;

import javax.inject.Inject;

import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowStore;

import com.googlecode.objectify.Key;

/**
 * The account store contains the non-data specific business logic. Data access
 * is delegated to the Account DAO.
 */
public class FloAccountStore implements AccountStore {

	/**
	 * The default account balance.
	 */
	private static final double DEFAULT_ACCOUNT_BALANCE = 0.0;

	/**
	 * The default account name.
	 */
	private static final String DEFAULT_ACCOUNT_NAME = "My Account";

	/**
	 * The data access object
	 */
	private final AccountDAO dao;

	/**
	 * The cash flow store.
	 */
	private final CashFlowStore cashflowStore;

	/**
	 * Creates a new FloAccountStore instance.
	 * 
	 * @param accountDAO
	 *            The account data access object
	 * @param cashflowStore
	 *            The cash flow store
	 */
	@Inject
	public FloAccountStore(AccountDAO accountDAO, CashFlowStore cashflowStore) {
		this.dao = accountDAO;
		this.cashflowStore = cashflowStore;
	}

	@Override
	public List<Account> getAccounts() {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		// get the list of accounts
		List<Account> accounts = dao.getAccounts(cashflow);

		// if there are not accounts
		if (accounts.isEmpty()) {
			// create a default account
			accounts.add(createDefaultAccount(cashflow));
		}

		return accounts;
	}

	@Override
	public Account createAccount(String name, double balance) {
		return createAccount(name, balance, 0.0, 0.0);
	}

	/**
	 * Creates a default account for the given cash flow.
	 * 
	 * @param cashflow
	 *            The cash flow
	 * @return The new account
	 */
	private Account createDefaultAccount(CashFlow cashflow) {
		return dao.createAccount(cashflow, DEFAULT_ACCOUNT_NAME,
				DEFAULT_ACCOUNT_BALANCE, 0.0, 0.0);
	}

	@Override
	public void deleteAccount(long id) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		// delete the account with the given key
		dao.deleteAccount(Key.create(cashflow.getKey(), Account.class, id));
	}

	@Override
	public Account editAccount(long id, String name, double balance,
			double negativeThreshold, double positiveThreshold) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		Key<Account> key = Key.create(cashflow.getKey(), Account.class, id);

		// update the account with the given key
		return dao.editAccount(key, name, balance, negativeThreshold,
				positiveThreshold);
	}

	@Override
	public Account getAccount(long id) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		Key<Account> key = Key.create(cashflow.getKey(), Account.class, id);

		return dao.getAccount(key);
	}

	@Override
	public Account createAccount(String name, double balance,
			double negativeThreshold, double positiveThreshold) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		// create the new account
		Account acct = dao.createAccount(cashflow, name, balance,
				negativeThreshold, positiveThreshold);

		return acct;
	}

}
