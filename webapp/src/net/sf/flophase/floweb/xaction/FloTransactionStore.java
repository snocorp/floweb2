package net.sf.flophase.floweb.xaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.sf.flophase.floweb.cashflow.CashFlow;
import net.sf.flophase.floweb.cashflow.CashFlowStore;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryStore;

import com.googlecode.objectify.Key;

/**
 * The transaction store contains the non-data specific business logic. Data access is delegated to the Transaction DAO.
 */
public class FloTransactionStore implements TransactionStore {

	/**
	 * The transaction data access object.
	 */
	private final TransactionDAO dao;

	/**
	 * The entry store.
	 */
	private final EntryStore entryStore;

	/**
	 * The cash flow store.
	 */
	private final CashFlowStore cashflowStore;

	/**
	 * Creates a new {@link FloTransactionStore} instance.
	 * 
	 * @param xactionDAO
	 *            The transaction data access object
	 * @param entryStore
	 *            The entry store
	 * @param cashflowStore
	 *            The cash flow store
	 */
	@Inject
	public FloTransactionStore(TransactionDAO xactionDAO, EntryStore entryStore, CashFlowStore cashflowStore) {
		super();

		this.dao = xactionDAO;
		this.entryStore = entryStore;
		this.cashflowStore = cashflowStore;
	}

	@Override
	public Transaction createTransaction(String name, Date date) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		Transaction xaction = dao.createTransaction(cashflow, name, date);

		return xaction;
	}

	@Override
	public void deleteTransaction(long id) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		// delete the account with the given key
		dao.deleteTransaction(new Key<Transaction>(cashflow.getKey(), Transaction.class, id));
	}

	@Override
	public Transaction editTransaction(long id, String name, Date date) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		Key<Transaction> key = new Key<Transaction>(cashflow.getKey(), Transaction.class, id);

		Transaction xaction = dao.editTransaction(key, name, date);

		return xaction;
	}

	@Override
	public List<FinancialTransaction> getTransactions(Date startDate) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		List<Transaction> transactions = dao.getTransactions(cashflow, startDate);
		List<FinancialTransaction> financialTransactions = new ArrayList<FinancialTransaction>(transactions.size());

		Map<Long, Entry> entries;
		for (Transaction xaction : transactions) {
			entries = entryStore.getEntries(xaction);

			financialTransactions.add(new FinancialTransaction(xaction, entries));
		}

		return financialTransactions;
	}

	@Override
	public Transaction getTransaction(long id) {
		// get the user's cash flow
		CashFlow cashflow = cashflowStore.getCashFlow();

		Key<Transaction> key = new Key<Transaction>(cashflow.getKey(), Transaction.class, id);

		return dao.getTransaction(key);
	}

}
