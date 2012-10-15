package net.sf.flophase.floweb.cashflow;

import javax.persistence.Id;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

/**
 * This class represents a cash flow. This is a container object for all of the other objects in the system.
 */
public class CashFlow {

	/**
	 * The cash flow identifier
	 */
	@Id
	private Long key;

	/**
	 * The user that owns the cash flow
	 */
	private User user;

	/**
	 * Creates a new CashFlow instance.
	 * 
	 * @param user
	 *            The user
	 */
	public CashFlow(User user) {
		this.user = user;
	}

	/**
	 * Creates a new CashFlow instance.
	 */
	public CashFlow() {
		// empty constructor
	}

	/**
	 * Returns the cash flow key.
	 * 
	 * @return The key
	 */
	public Key<CashFlow> getKey() {
		return new Key<CashFlow>(CashFlow.class, key);
	}

	/**
	 * Returns the user that owns the cash flow.
	 * 
	 * @return The user
	 */
	public User getUser() {
		return user;
	}
}
