package what.settings;

import android.content.Context;
import android.content.SharedPreferences;

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
		settings = c.getSharedPreferences("settings", Context.MODE_PRIVATE);
		settingsEditor = settings.edit();
	}

	public static void saveQuickSearch(boolean b) {
		settingsEditor.putBoolean("quickSearch", b);
	}

	public static boolean getQuickSearch() {
		try {
			return settings.getBoolean("quickSearch", true);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public static void saveNotificationRate(long rate) {
		settingsEditor.putLong("notificationRefreshRate", rate);
	}

	public static long getNotificationRefreshRate() {
		// default is 1 hour
		try {
			return settings.getLong("notificationRefreshRate", 3600000);
		} catch (Exception e) {
			e.printStackTrace();
			return 3600000;
		}
	}

	public static void saveAnnoucnementRefreshRate(long rate) {
		settingsEditor.putLong("anouncementRefreshRate", rate);
	}

	public static long getAnouncementRefreshRate() {
		// default is 1 hour
		try {
			return settings.getLong("anouncementRefreshRate", 3600000);
		} catch (Exception e) {
			e.printStackTrace();
			return 3600000;
		}
	}

	public static void saveInboxRefreshRate(long rate) {
		settingsEditor.putLong("inboxRefreshRate", rate);
	}

	public static long getInboxRefreshRate() {
		// default is 1 hour
		try {
			return settings.getLong("inboxRefreshRate", 3600000);
		} catch (Exception e) {
			e.printStackTrace();
			return 3600000;
		}
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
		try {
			return settings.getString("username", "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void savePassword(String password) {
		settingsEditor.putString("password", password);
	}

	public static String getPassword() {
		try {
			return settings.getString("password", "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	public static void saveRememberMe(boolean b) {
		settingsEditor.putBoolean("rememberMe", b);
	}

	public static boolean getRememberMe() {
		try {
			return settings.getBoolean("rememberMe", false);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void saveSSL(boolean b) {
		settingsEditor.putBoolean("useSSL", b);
	}

	public static Boolean getSSL() {
		try {
			return settings.getBoolean("useSSL", false);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void saveSessionId(String id) {
		settingsEditor.putString("sessionId", id);
	}

	public static String getSessionId() {
		try {
			return settings.getString("sessionId", "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void saveAuthKey(String key) {
		settingsEditor.putString("authKey", key);
	}

	public static String getAuthKey() {
		try {
			return settings.getString("authKey", "");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
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
