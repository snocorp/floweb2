package net.sf.flophase.floweb.common;

import javax.inject.Inject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

/**
 * The user store allows access to the logged in user.
 */
public class FloUserStore implements UserStore {

	/**
	 * The logged in user.
	 */
	private final User user;

	/**
	 * Creates a mew FlowUserStore instance.
	 * 
	 * @param userService
	 *            The user service
	 */
	@Inject
	public FloUserStore(UserService userService) {
		this.user = userService.getCurrentUser();
	}

	@Override
	public User getUser() {
		return user;
	}

}
