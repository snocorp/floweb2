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
	 * Creates a record of a cash flow import.
	 * 
	 * @param total
	 *            The total number of things to be imported
	 * @return The initial status
	 */
	public CashFlowImportStatus createCashFlowImport(int total);

	/**
	 * Retrieves the cash flow import status with the given id.
	 * 
	 * @param id
	 *            The import status id
	 * @return The status.
	 */
	public CashFlowImportStatus getCashFlowImportStatus(long id);

	/**
	 * Updates the status of the import.
	 * 
	 * @param status
	 *            The import status
	 * @param done
	 *            How many are done
	 * @param total
	 *            The total to be imported.
	 */
	public void updateCashFlowImportStatus(CashFlowImportStatus status,
			int done, int total);
}
