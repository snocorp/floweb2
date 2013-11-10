package net.sf.flophase.floweb.cashflow;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

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
	public abstract CashFlowImportStatus createCashFlowImportStatus(
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
	 * Deletes the cash flow import status.
	 * 
	 * @param status
	 *            The import status
	 * @return The result
	 */
	public abstract Result<Void> deleteCashFlowImportStatus(
			CashFlowImportStatus status);

	/**
	 * Updates the cash flow import status.
	 * 
	 * @param status
	 *            The import status
	 * @return The result
	 */
	public abstract Result<Key<CashFlowImportStatus>> updateCashFlowImportStatus(
			CashFlowImportStatus status);

	/**
	 * Retrieves the cash flow with the given key.
	 * 
	 * @param key
	 *            The key
	 * @return The cash flow
	 */
	public abstract CashFlow getCashFlow(Key<CashFlow> key);

}
