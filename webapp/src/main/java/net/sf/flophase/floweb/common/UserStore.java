package net.sf.flophase.floweb.common;

import com.google.appengine.api.users.User;

/**
 * This interface defines a store to access the user entity.
 */
public interface UserStore {
	/**
	 * Returns the currently logged in user.
	 * 
	 * @return The user.
	 */
	public abstract User getUser();
}
