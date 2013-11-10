package net.sf.flophase.floweb.cashflow;

import javax.inject.Inject;

import net.sf.flophase.floweb.user.UserStore;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

/**
 * The cash flow store contains the non-data specific business logic. Data
 * access is delegated to the Cash Flow DAO.
 * 
 * This store has state and must be scoped.
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
	 * The key to use to get the cash flow if there is no user.
	 */
	private Key<CashFlow> key;

	/**
	 * The cash flow cached to save look ups.
	 */
	private CashFlow cashflow;

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
		if (cashflow == null) {
			User user = userStore.getUser();

			if (user != null) {
				// get the cash flow for the user
				cashflow = dao.getCashFlow(user);

				// if there is no cash flow
				if (cashflow == null) {
					// create a new cash flow
					cashflow = dao.createCashFlow(user);
				}
			} else if (key != null) {
				cashflow = dao.getCashFlow(key);
			}
		}

		return cashflow;
	}

	@Override
	public void setCashFlowId(long cashflowId) {
		key = Key.create(CashFlow.class, cashflowId);
	}

}
