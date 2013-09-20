package net.sf.flophase.floweb.user;

import java.util.Map;

import com.google.appengine.api.users.User;

public class FloUserService implements UserService {

	private final UserStore userStore;

	public FloUserService(UserStore userStore) {
		this.userStore = userStore;
	}

	@Override
	public Map<String, String> getSettings(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserLoggedIn() {
		return userStore.isUserLoggedIn();
	}

	@Override
	public User getCurrentUser() {
		return userStore.getUser();
	}

}
