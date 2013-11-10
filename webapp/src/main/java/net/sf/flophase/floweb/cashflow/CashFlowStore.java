package net.sf.flophase.floweb.cashflow;

/**
 * This interface defines the internal API to manipulate cash flows.
 */
public interface CashFlowStore {
	/**
	 * Returns the cash flow for the logged in user.
	 * 
	 * @return The cash flow
	 */
	public CashFlow getCashFlow();

	/**
	 * Allows the service to return the cashflow with the given id when no user
	 * is available.
	 * 
	 * @param cashflowId
	 *            The id
	 */
	public void setCashFlowId(long cashflowId);
}
