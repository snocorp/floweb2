package net.sf.flophase.floweb.cashflow;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class represents the task of a cash flow import.
 */
@Entity
public class CashFlowImportStatus {

	/**
	 * Value for total that represents a status that is not yet started.
	 */
	public static final int NOT_STARTED = -1;

	/**
	 * The cash flow import status identifier
	 */
	@Id
	private Long id;

	/**
	 * The cash flow for this account.
	 */
	@Parent
	private Key<CashFlow> cashflow;

	/**
	 * The number of items completed.
	 */
	private int done = 0;

	/**
	 * The total number of items to be imported.
	 */
	private int total = 0;

	/**
	 * Creates a new CashFlowImportTask instance.
	 * 
	 * @param cashflow
	 *            The cash flow into which we are importing
	 * @param total
	 *            The total number of items to be imported
	 */
	public CashFlowImportStatus(Key<CashFlow> cashflow, int total) {
		this.cashflow = cashflow;
		this.setTotal(total);
	}

	/**
	 * Creates a new CashFlowImportTask instance.
	 */
	public CashFlowImportStatus() {
		// empty constructor
	}

	/**
	 * Returns the cash flow key.
	 * 
	 * @return The key
	 */
	public Key<CashFlowImportStatus> getKey() {
		return Key.create(CashFlowImportStatus.class, id);
	}

	/**
	 * Returns the number of elements that are done.
	 * 
	 * @return The number that are done
	 */
	public int getDone() {
		return done;
	}

	/**
	 * Sets the number of elements that are done.
	 * 
	 * @param done
	 *            The number that are done.
	 */
	public void setDone(int done) {
		this.done = done;
	}

	/**
	 * Returns the total number of this to be imported.
	 * 
	 * @return The number of things to be imported
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * Sets the total number of this to be imported.
	 * 
	 * @param total
	 *            The number of things to be imported
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Returns the cash flow key
	 * 
	 * @return The cash flow key
	 */
	public Key<CashFlow> getCashflow() {
		return cashflow;
	}
}
