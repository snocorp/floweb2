package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import net.sf.flophase.floweb.account.AccountStore;
import net.sf.flophase.floweb.xaction.TransactionStore;

/**
 * The cash flow store contains the non-data specific business logic. Data
 * access is delegated to the Cash Flow DAO.
 */
public class FloCashFlowExportStore implements CashFlowExportStore {

	/**
	 * The account store.
	 */
	private final AccountStore accountStore;

	/**
	 * The transaction store.
	 */
	private final TransactionStore xactionStore;

	/**
	 * Creates a new FloCashFlowStore instance
	 * 
	 * @param accountStore
	 *            The account store
	 * @param xactionStore
	 *            The transaction store
	 */
	@Inject
	protected FloCashFlowExportStore(AccountStore accountStore,
			TransactionStore xactionStore) {
		this.accountStore = accountStore;
		this.xactionStore = xactionStore;
	}

	@Override
	public CashFlowExport getCashFlowExport() {
		CashFlowExport export = new CashFlowExport();

		export.setAccounts(accountStore.getAccounts());
		export.setTransactions(xactionStore.getTransactions());

		return export;
	}

}
