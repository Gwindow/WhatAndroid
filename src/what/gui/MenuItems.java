package what.gui;

import java.util.EnumMap;
import java.util.Map;

import what.barcode.BarcodeScannerActivity;
import what.bookmarks.BookmarksActivity;
import what.debug.DebugActivity;
import what.forum.ForumActivity;
import what.home.HomeActivity;
import what.inbox.InboxActivity;
import what.notifications.NotificationsActivity;
import what.search.RequestsSearchActivity;
import what.search.TorrentSearchActivity;
import what.search.UserSearchActivity;
import what.settings.SettingsActivity;
import what.top.TopTenActivity;
import api.whatstatus.WhatStatus;

/**
 * Enum thats hold menu items
 * 
 * @author Gwindow
 * @since May 25, 2012 9:44:27 AM
 */
public enum MenuItems {

	HOME("Home"), FORUM("Forum"), SEARCH("Search"),
	// search menu start
	TORRENTS("Torrents"), REQUESTS("Requests"), USERS("Users"),
	// search menu end
	INBOX("Inbox"), BARCODE_SCANNER("Barcode Scanner"), BOOKMARKS("Bookmarks"), NOTIFICATIONS("Notifications"), MORE("More"),
	// more menu start
	SETTINGS("Settings"), TOP_TEN("Top Ten"), STATUS("Status"), DEBUG("Debug");
	// more menu end

	/** The map. */
	private static Map<MenuItems, Class<?>> map;

	/** The name. */
	private final String name;

	/**
	 * Inits the menu items.
	 */
	public static void init() {
		map = new EnumMap<MenuItems, Class<?>>(MenuItems.class);
		map.put(HOME, HomeActivity.class);
		map.put(FORUM, ForumActivity.class);
		map.put(TORRENTS, TorrentSearchActivity.class);
		map.put(REQUESTS, RequestsSearchActivity.class);
		map.put(USERS, UserSearchActivity.class);
		map.put(INBOX, InboxActivity.class);
		map.put(BOOKMARKS, BookmarksActivity.class);
		map.put(NOTIFICATIONS, NotificationsActivity.class);
		map.put(BARCODE_SCANNER, BarcodeScannerActivity.class);
		map.put(SETTINGS, SettingsActivity.class);
		map.put(TOP_TEN, TopTenActivity.class);
		map.put(STATUS, WhatStatus.class);
		map.put(DEBUG, DebugActivity.class);
	}

	/**
	 * 
	 * 
	 * @param name
	 *            the title to be displayed
	 */
	private MenuItems(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Contains key.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public static boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * Gets the.
	 * 
	 * @param key
	 *            the key
	 * @return the class
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public static Class<?> get(Object key) {
		return map.get(key);
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 * @see java.util.Map#size()
	 */
	public static int size() {
		return map.size();
	}
}
