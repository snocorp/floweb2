package net.sf.flophase.floweb.cashflow;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * This class represents a cash flow. This is a container object for all of the
 * other objects in the system.
 */
@Entity
public class CashFlow {

	/**
	 * The cash flow identifier
	 */
	@Id
	private Long id;

	/**
	 * The user that owns the cash flow
	 */
	@Index
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
		return Key.create(CashFlow.class, id);
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
