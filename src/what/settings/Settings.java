package what.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import api.util.Tuple;
import what.gui.R;

import java.util.HashMap;
import java.util.Set;

public class Settings {
	private static SharedPreferences settings;
	private static SharedPreferences.Editor settingsEditor;

	protected static HashMap<String, Tuple<Integer, Integer>> themes;
	static {
		themes = new HashMap<String, Tuple<Integer, Integer>>();
		themes.put("newgroove", new Tuple<Integer, Integer>(R.style.Theme_newgroove, R.color.newgroove));
		themes.put("Schnappi", new Tuple<Integer, Integer>(R.style.Theme_schnappi, R.color.schnappi));
		themes.put("Moldy Walls by senatortom", new Tuple<Integer, Integer>(R.style.Theme_moldy_walls, R.color.moldy_walls));
		themes.put("Gwindow Loves Tom by senatortom", new Tuple<Integer, Integer>(R.style.Theme_gwindow_loves_tom,
				R.color.gwindow_loves_tom));
		themes.put("Light", new Tuple<Integer, Integer>(R.style.LightTheme, R.color.roboto));
		themes.put("Dark", new Tuple<Integer, Integer>(R.style.DarkTheme, R.color.robotoDark));
		themes.put("Old E and 4 Blunts by amxtrash", new Tuple<Integer, Integer>(R.style.Theme_old_e_and_four_blunts,
				R.color.old_e_and_four_blunts));
		themes.put("Watermelon by senatortom", new Tuple<Integer, Integer>(R.style.Theme_watermelon, R.color.watermelon));
		themes.put("Ridejckl's Barbie Convertible by ridejckl", new Tuple<Integer, Integer>(R.style.Theme_ridejkcls_barbie_convertible,
				R.color.ridejkcls_barbie_convertible));
		themes.put("Wonder Orange by Guegs", new Tuple<Integer, Integer>(R.style.Theme_wonder_orange, R.color.wonder_orange));
		themes.put("Dead Camels by Ananke", new Tuple<Integer, Integer>(R.style.Theme_dead_camels, R.color.dead_camels));
		themes.put("Mono by dr4g0n", new Tuple<Integer, Integer>(R.style.Theme_mono, R.color.mono));
		themes.put("Example by Skboud", new Tuple<Integer, Integer>(R.style.Theme_example, R.color.example));
		themes.put("Penis by Entrapment", new Tuple<Integer, Integer>(R.style.Theme_penis, R.color.penis));
		themes.put("Dark Meline by Santigasm", new Tuple<Integer, Integer>(R.style.Theme_dark_meline, R.color.white));
	}

	protected static HashMap<String, Integer> icons;
	static {
		icons = new HashMap<String, Integer>();
		icons.put("Icon A", R.drawable.icon_a);
		icons.put("Icon B", R.drawable.icon_b);
		icons.put("Icon C", R.drawable.icon_c);
		icons.put("Icon D", R.drawable.icon_d);
		icons.put("Icon E", R.drawable.icon_e);
	}

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

	public static boolean getTipsFirstRun() {
		boolean b = settings.getBoolean("tips_first_run_shown", false);
		if (!b) {
			settingsEditor.putBoolean("tips_first_run_shown", true);
			settingsEditor.commit();
		}
		return b;
	}

	public static boolean getFirstHome() {
		boolean b = settings.getBoolean("tips_home_shown", false);
		if (!b) {
			settingsEditor.putBoolean("tips_home_shown", true);
			settingsEditor.commit();
		}
		return b;
	}

	/**
	 * @return
	 */
	public static boolean getFirstForum() {
		boolean b = settings.getBoolean("tips_forum_shown", false);
		if (!b) {
			settingsEditor.putBoolean("tips_forum_shown", true);
			settingsEditor.commit();
		}
		return b;
	}

	public static boolean getCaching() {
		return settings.getBoolean("caching_preference", true);
	}

	public static boolean getShowHomeInfo() {
		return settings.getBoolean("showhomeinfo_preference", true);
	}

	public static void saveHomeInfo(Set<String> set) {
        //TODO: Android Studio tells me putStringSet requires api level 11
        //should we just ditch Android <3 ? It's a pretty small market share after all
		settingsEditor.putStringSet("homecache_set", set);
		settingsEditor.commit();
	}

	public static Set<String> getHomeInfo() {
		return settings.getStringSet("homecache_set", null);
	}

	public static void saveHomeInfoCounter(int counter) {
		settingsEditor.putInt("homecache_counter", counter);
		commit();
	}

	public static int getHomeInfoCounter() {
		return settings.getInt("homecache_counter", 0);
	}

	public static boolean getCrashReportsEnabled() {
		return settings.getBoolean("report_preference", true);
	}

	public static boolean getSubscribedToThreads() {
		return settings.getBoolean("subscribetothreads_preference", true);
	}

	public static int getHomeIconPath() {
		return settings.getInt("homeiconpath_preference", R.drawable.icon_a);
	}

	public static void saveHomeIconPath(int path) {
		settingsEditor.putInt("homeiconpath_preference", path);
		commit();
	}

	public static boolean getHomeIcon() {
		return settings.getBoolean("homeicon_preference", true);
	}

	public static boolean getBoldSetting() {
		return settings.getBoolean("boldthreads_preference", true);
	}

	public static Tuple<Integer, Integer> getTheme() {
		return new Tuple<Integer, Integer>(settings.getInt("theme_preference_a", R.style.Theme_newgroove), settings.getInt(
				"theme_preference_b", R.color.newgroove));
	}

	public static void saveTheme(int theme, int color) {
		settingsEditor.putInt("theme_preference_a", theme);
		settingsEditor.putInt("theme_preference_b", color);
		commit();
	}

	public static boolean getDebugPreference() {
		return settings.getBoolean("debug_preference", false);
	}

	public static boolean getQuickSearch() {
		return settings.getBoolean("quickSearch_preference", true);
	}

	public static boolean getSpotifyButton() {
		return settings.getBoolean("spotifyButton_preference", true);
	}

	public static boolean getLastfmButton() {
		return settings.getBoolean("lastfmButton_preference", true);
	}

	public static boolean getAnnouncementsService() {
		return settings.getBoolean("announcementsService_preference", true);
	}

	public static String getAnnouncementsServiceInterval() {
		return settings.getString("announcementsService_interval", "180");
	}

	public static boolean getInboxService() {
		return settings.getBoolean("inboxService_preference", true);
	}

	public static String getInboxServiceInterval() {
		return settings.getString("inboxService_interval", "60");
	}

	public static boolean getNotificationsService() {
		return settings.getBoolean("notificationsService_preference", false);
	}

	public static String getNotificationsServiceInterval() {
		return settings.getString("notificationsService_interval", "180");
	}

	public static boolean getCustomBackground() {
		return settings.getBoolean("customBackground_preference", false);
	}

	public static boolean getTileBackground() {
		return settings.getBoolean("tileBackground_preference", true);
	}

	public static String getCustomBackgroundPath() {
		return settings.getString("customBackground_path", "");
	}

	public static void saveQuickScannerFirstRun(boolean b) {
		settingsEditor.putBoolean("quickScannerFirstRun", b);
		commit();
	}

	public static boolean getQuickScannerFirstRun() {
		return settings.getBoolean("quickScannerFirstRun", true);
	}

	public static void saveFirstRun(boolean b) {
		settingsEditor.putBoolean("firstRun", b);
		commit();
	}

	public static boolean getFirstRun() {
		return settings.getBoolean("firstRun", true);
	}

	public static boolean getAvatarsEnabled() {
		return settings.getBoolean("avatarsEnabled_preference", true);
	}

	public static boolean getGesturesEnabled() {
		return settings.getBoolean("gesturesEnabled_preference", true);
	}

	public static boolean getSubscriptionsEnabled() {
		return settings.getBoolean("subscriptionsEnabled_preference", true);
	}

	public static void saveNumberOfAnnouncements(int i) {
		settingsEditor.putInt("numberOfA", i);
	}

	public static void saveCustomBackgroundPath(String s) {
		settingsEditor.putString("customBackground_path", s);
		commit();
	}

	public static String getHostPreference() {
		return settings.getString("host_preference", "");
	}

	public static String getPortPreference() {
		return settings.getString("port_preference", "");
	}

	public static String getPasswordPreference() {
		return settings.getString("password_preference", "");
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

	public static void saveMessageHashCode(int hash) {
		settingsEditor.putInt("messageHashCode", hash);
	}

	public static int getMessageHashCode() {
		try {
			return settings.getInt("messageHashCode", 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void saveUserId(int id) {
		settingsEditor.putInt("userId", id);
	}

	public static int getUserId() {
		return settings.getInt("userId", 0);
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
	 * Check if the user is ok with running a developer build
	 * @return true if ok with running a dev build
	 */
	public static boolean useDevBuilds(){
		return settings.getBoolean("useDevBuilds", false);
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

	public static float getGestureSensitivity() {
		return settings.getFloat("gestureSensitivity_preference", 2.5f);
	}

}
