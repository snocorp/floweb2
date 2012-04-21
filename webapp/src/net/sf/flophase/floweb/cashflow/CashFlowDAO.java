package net.sf.flophase.floweb.cashflow;

import com.google.appengine.api.users.User;

/**
 * This interface defines the data access object for the cash flow entity.
 */
public interface CashFlowDAO {

	/**
	 * Gets the cash flow for the given user.
	 * 
	 * @param user
	 *            The user
	 * @return The cash flow
	 */
	public abstract CashFlow getCashFlow(User user);

	/**
	 * Creates a cash flow for the given user.
	 * 
	 * @param user
	 *            The user
	 * @return The new cash flow
	 */
	public abstract CashFlow createCashFlow(User user);

}
