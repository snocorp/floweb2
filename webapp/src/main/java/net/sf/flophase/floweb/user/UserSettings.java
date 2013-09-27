package net.sf.flophase.floweb.user;

/**
 * This is a whitelist of all the possible settings. This helps prevent
 * overloading the system with random settings.
 */
public enum UserSettings {
	/**
	 * Application interface mode.
	 */
	UI_APP_MODE("net.sf.flophase.floweb.ui.app.mode"),

	/**
	 * Test setting
	 */
	TEST_SETTING_A("net.sf.flophase.floweb.test.setting.a"),

	/**
	 * Test setting
	 */
	TEST_SETTING_B("net.sf.flophase.floweb.test.setting.b"),

	/**
	 * Test setting
	 */
	TEST_SETTING_C("net.sf.flophase.floweb.test.setting.c");

	private final String key;

	private UserSettings(String key) {
		this.key = key;
	}

	/**
	 * The setting key.
	 * 
	 * @return The key
	 */
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}
}
