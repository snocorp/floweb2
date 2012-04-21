package net.sf.flophase.floweb.entry;

import java.util.Map;

import javax.inject.Inject;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.xaction.Transaction;
import net.sf.flophase.floweb.xaction.TransactionStore;

/**
 * The entry store contains the non-data specific business logic. Data access is delegated to the Entry DAO.
 */
public class FloEntryStore implements EntryStore {

	/**
	 * The data access object.
	 */
	private final EntryDAO dao;

	/**
	 * The account store.
	 */
	private final AccountStore accountStore;

	/**
	 * The transaction store.
	 */
	private final TransactionStore xactionStore;

	/**
	 * Creates a new {@link FloEntryStore} instance.
	 * 
	 * @param dao
	 *            The data access object.
	 * @param accountStore
	 *            The account store
	 * @param xactionStore
	 *            The transaction store
	 */
	@Inject
	public FloEntryStore(EntryDAO dao, AccountStore accountStore, TransactionStore xactionStore) {
		this.dao = dao;
		this.accountStore = accountStore;
		this.xactionStore = xactionStore;
	}

	@Override
	public Entry editEntry(long accountId, long xactionId, double amount) {
		Account account = accountStore.getAccount(accountId);
		Transaction xaction = xactionStore.getTransaction(xactionId);

		Entry entry = dao.editEntry(xaction, account.getKey().getId(), amount);

		return entry;
	}

	@Override
	public Map<Long, Entry> getEntries(Transaction xaction) {
		return dao.getEntries(xaction);
	}

}
