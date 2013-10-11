package net.sf.flophase.floweb.cashflow;

import net.sf.flophase.floweb.common.Response;

/**
 * This interface defines the public API for manipulating cash flow.
 */
public interface CashFlowService {
	/**
	 * Returns a list of the accounts for the logged in user.
	 * 
	 * @return A response containing the accounts.
	 */
	public Response<CashFlow> getCashFlow();

	/**
	 * Returns an export of the full cash flow.
	 * 
	 * @return The export
	 */
	public Response<CashFlowExport> getCashFlowExport();

	/**
	 * Imports the given cash flow.
	 * 
	 * @param cashflow
	 *            The cash flow in JSON format.
	 * @return The initial status.
	 */
	public Response<CashFlowImportStatus> importCashFlow(String cashflow);

	/**
	 * Imports the given cash flow.
	 * 
	 * @param key
	 *            The import status key
	 * @param cashflow
	 *            The cash flow in JSON format.
	 * @return The final status.
	 */
	public Response<CashFlowImportStatus> importCashFlow(String key,
			String cashflow);

	/**
	 * Returns the status of a cash flow import task.
	 * 
	 * @param key
	 *            The import status key
	 * 
	 * @return The status
	 */
	public Response<CashFlowImportStatus> getCashFlowImportStatus(String key);
}
