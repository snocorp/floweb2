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

	/**
	 * Stores a record of the cash flow import to allow asynchronous completion.
	 * 
	 * @param cashflow
	 *            The cash flow
	 * @param total
	 *            The number of things to be imported
	 * @return The initial status
	 */
	public abstract CashFlowImportStatus createCashFlowImport(
			CashFlow cashflow, int total);

	/**
	 * Retrieves the current cash flow import status for the given id.
	 * 
	 * @param cashflow
	 *            The parent cash flow
	 * @param id
	 *            The id
	 * @return The status
	 */
	public abstract CashFlowImportStatus getCashFlowImportStatus(
			CashFlow cashflow, long id);

	/**
	 * Updates the cash flow import status.
	 * 
	 * @param status
	 *            The import status
	 */
	public abstract void updateCashFlowImportStatus(CashFlowImportStatus status);

}
