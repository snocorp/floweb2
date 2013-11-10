package net.sf.flophase.floweb.cashflow;

/**
 * This store deals with cash flow import status.
 */
public interface CashFlowImportStore {

	/**
	 * Creates a record of a cash flow import.
	 * 
	 * @return The initial status
	 */
	public CashFlowImportStatus createCashFlowImportStatus();

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
