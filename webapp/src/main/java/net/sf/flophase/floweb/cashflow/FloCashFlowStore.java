package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import net.sf.flophase.floweb.common.UserStore;

import com.google.appengine.api.users.User;

/**
 * The cash flow store contains the non-data specific business logic. Data access is delegated to the Cash Flow DAO.
 */
public class FloCashFlowStore implements CashFlowStore {

	/**
	 * The cash flow data access object.
	 */
	private final CashFlowDAO dao;

	/**
	 * The user store.
	 */
	private final UserStore userStore;

	/**
	 * Creates a new FloCashFlowStore instance
	 * 
	 * @param userStore
	 *            The user store
	 * @param cashflowDAO
	 *            The cash flow data access object
	 */
	@Inject
	protected FloCashFlowStore(UserStore userStore, CashFlowDAO cashflowDAO) {
		this.userStore = userStore;
		this.dao = cashflowDAO;
	}

	@Override
	public CashFlow getCashFlow() {
		User user = userStore.getUser();

		// get the cash flow for the user
		CashFlow cashflow = dao.getCashFlow(user);

		// if there is no cash flow
		if (cashflow == null) {
			// create a new cash flow
			cashflow = dao.createCashFlow(user);
		}

		return cashflow;
	}

}
