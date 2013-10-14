package net.sf.flophase.floweb.cashflow;


/**
 * This interface defines the internal API to manipulate cash flows.
 */
public interface CashFlowTradeStore {

	/**
	 * Returns the export of the cash flow for the logged in user.
	 * 
	 * @return The cash flow export
	 */
	public CashFlowExport getCashFlowExport();

	/**
	 * Imports the data in the cash flow export into the current cash flow.
	 * 
	 * @param status
	 *            The import status
	 * @param cashFlowExport
	 *            The data
	 */
	public void importCashFlow(CashFlowImportStatus status,
			CashFlowExport cashFlowExport);
}
