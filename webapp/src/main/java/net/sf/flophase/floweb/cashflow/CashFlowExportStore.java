package net.sf.flophase.floweb.cashflow;

/**
 * This interface defines the internal API to manipulate cash flows.
 */
public interface CashFlowExportStore {

	/**
	 * Returns the export of the cash flow for the logged in user.
	 * 
	 * @return The cash flow export
	 */
	public CashFlowExport getCashFlowExport();
}
