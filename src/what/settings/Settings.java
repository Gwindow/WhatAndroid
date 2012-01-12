package what.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	private static SharedPreferences settings;
	private static SharedPreferences.Editor settingsEditor;

	/**
	 * Initialize the settings reader and writer, should only be done once
	 * 
	 * @param c
	 *            Context
	 */
	public static void init(Context c) {
		settings = PreferenceManager.getDefaultSharedPreferences(c);
		settingsEditor = settings.edit();

	}

	public static boolean getQuickSearch() {
		return settings.getBoolean("quickSearch_preference", true);
	}

	public static void saveNumberOfAnnouncements(int i) {
		settingsEditor.putInt("numberOfA", i);
	}

	public static int getNumberOfAnnouncements() {
		try {
			return settings.getInt("numberOfA", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void saveNumberOfBlogPosts(int i) {
		settingsEditor.putInt("numberOfB", i);
	}

	public static int getNumberOfBlogPosts() {
		try {
			return settings.getInt("numberOfB", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void saveMessage(String message) {
		settingsEditor.putString("message", message);
	}

	public static String getMessage() {
		try {
			return settings.getString("message", "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void saveUsername(String username) {
		settingsEditor.putString("username", username);
	}

	public static String getUsername() {
		return settings.getString("username", "");
	}

	public static void savePassword(String password) {
		settingsEditor.putString("password", password);
	}

	public static String getPassword() {
		return settings.getString("password", "");

	}

	public static void saveRememberMe(boolean b) {
		settingsEditor.putBoolean("rememberMe", b);
	}

	public static boolean getRememberMe() {
		return settings.getBoolean("rememberMe", false);
	}

	public static void saveSSL(boolean b) {
		settingsEditor.putBoolean("useSSL", b);
	}

	public static Boolean getSSL() {
		return settings.getBoolean("useSSL", false);
	}

	public static void saveSessionId(String id) {
		settingsEditor.putString("sessionId", id);
	}

	public static String getSessionId() {
		return settings.getString("sessionId", "");
	}

	public static void saveAuthKey(String key) {
		settingsEditor.putString("authKey", key);
	}

	public static String getAuthKey() {
		return settings.getString("authKey", "");
	}

	public static void commit() {
		settingsEditor.commit();
	}

	/**
	 * @return the settings
	 */
	public static SharedPreferences getSettings() {
		return settings;
	}

	/**
	 * @return the settingsEditor
	 */
	public static SharedPreferences.Editor getSettingsEditor() {
		return settingsEditor;
	}

}
