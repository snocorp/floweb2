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
	 * Returns an export of the full cashflow.
	 * 
	 * @return The export
	 */
	public Response<CashFlowExport> getCashFlowExport();
}
