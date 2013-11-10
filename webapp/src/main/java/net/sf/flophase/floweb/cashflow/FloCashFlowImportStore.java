package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import com.google.inject.Provider;

/**
 * This is the default implementation of the {@link CashFlowImportStore}.
 */
public class FloCashFlowImportStore implements CashFlowImportStore {

	/**
	 * The cash flow data access object.
	 */
	private final CashFlowDAO dao;

	/**
	 * The cash flow store.
	 */
	private final Provider<CashFlowStore> cashFlowStore;

	/**
	 * Creates a new FloCashFlowImportStore instance
	 * 
	 * @param cashFlowStore
	 *            The cash flow store
	 * @param cashflowDAO
	 *            The cash flow data access object
	 */
	@Inject
	protected FloCashFlowImportStore(Provider<CashFlowStore> cashFlowStore,
			CashFlowDAO cashflowDAO) {
		this.cashFlowStore = cashFlowStore;
		this.dao = cashflowDAO;
	}

	@Override
	public CashFlowImportStatus createCashFlowImportStatus() {
		// use -1 to indicate an uninitialized status
		return dao.createCashFlowImportStatus(
				cashFlowStore.get().getCashFlow(),
				CashFlowImportStatus.NOT_STARTED);
	}

	@Override
	public CashFlowImportStatus getCashFlowImportStatus(long id) {
		CashFlowImportStatus status = dao.getCashFlowImportStatus(cashFlowStore
				.get().getCashFlow(), id);

		if (status.getTotal() > -1 && status.getDone() == status.getTotal()) {
			dao.deleteCashFlowImportStatus(status);
		}

		return status;
	}

	@Override
	public void updateCashFlowImportStatus(CashFlowImportStatus status,
			int done, int total) {
		status.setDone(done);
		status.setTotal(total);

		dao.updateCashFlowImportStatus(status);
	}

}
