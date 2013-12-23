package net.sf.flophase.floweb.cashflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.sf.flophase.floweb.account.Account;
import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.entry.Entry;
import net.sf.flophase.floweb.entry.EntryStore;
import net.sf.flophase.floweb.xaction.FinancialTransaction;
import net.sf.flophase.floweb.xaction.Transaction;
import net.sf.flophase.floweb.xaction.TransactionStore;

/**
 * The cash flow store contains the non-data specific business logic. Data
 * access is delegated to the Cash Flow DAO.
 */
public class FloCashFlowTradeStore implements CashFlowTradeStore {

	/**
	 * The cash flow import store.
	 */
	private final CashFlowImportStore cashflowStore;

	/**
	 * The account store.
	 */
	private final AccountStore accountStore;

	/**
	 * The transaction store.
	 */
	private final TransactionStore xactionStore;

	/**
	 * The entry store.
	 */
	private final EntryStore entryStore;

	/**
	 * Creates a new FloCashFlowStore instance
	 * 
	 * @param cashflowStore
	 *            The cash flow store
	 * @param accountStore
	 *            The account store
	 * @param xactionStore
	 *            The transaction store
	 * @param entryStore
	 *            The entry store
	 */
	@Inject
	protected FloCashFlowTradeStore(CashFlowImportStore cashflowStore,
			AccountStore accountStore, TransactionStore xactionStore,
			EntryStore entryStore) {
		this.cashflowStore = cashflowStore;
		this.accountStore = accountStore;
		this.xactionStore = xactionStore;
		this.entryStore = entryStore;
	}

	@Override
	public CashFlowExport getCashFlowExport() {
		CashFlowExport export = new CashFlowExport();

		export.setAccounts(accountStore.getAccounts());
		export.setTransactions(xactionStore.getTransactions());

		return export;
	}

	@Override
	public void importCashFlow(CashFlowImportStatus status,
			CashFlowExport cashflowExport) {

		int total = cashflowExport.getAccounts().size()
				+ cashflowExport.getTransactions().size();
		int done = 0;

		cashflowStore.updateCashFlowImportStatus(status, done, total);

		Map<Long, Long> accountIdMap = new HashMap<Long, Long>();
		Map<String, Account> accountNameMap = new HashMap<String, Account>();

		// try to match existing accounts by name
		List<Account> existingAccounts = accountStore.getAccounts();
		for (Account account : existingAccounts) {
			accountNameMap.put(account.getName(), account);
		}

		// import accounts
		Account newAccount;
		for (Account account : cashflowExport.getAccounts()) {
			if (accountNameMap.containsKey(account.getName())) {
				newAccount = accountNameMap.get(account.getName());

				// update the account based on the imported data
				accountStore.editAccount(newAccount.getKey().getId(),
						newAccount.getName(), account.getBalance(),
						account.getNegativeThreshold(),
						account.getPositiveThreshold());
			} else {
				newAccount = accountStore.createAccount(account.getName(),
						account.getBalance(), account.getNegativeThreshold(),
						account.getPositiveThreshold());
			}

			accountIdMap.put(account.getKey().getId(), newAccount.getKey()
					.getId());

			done++;

			cashflowStore.updateCashFlowImportStatus(status, done, total);
		}

		// import transactions
		for (FinancialTransaction transaction : cashflowExport
				.getTransactions()) {
			Transaction xaction = xactionStore
					.createTransaction(transaction.getDetails().getName(),
							transaction.getDetails().getDate());

			long xactionId = xaction.getKey().getId();

			// import entries
			for (Entry entry : transaction.getEntries().values()) {
				entryStore.editEntry(accountIdMap.get(entry.getAccount()),
						xactionId, entry.getAmount());
			}

			done++;

			cashflowStore.updateCashFlowImportStatus(status, done, total);
		}
	}

}
