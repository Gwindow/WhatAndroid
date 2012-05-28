package what.gui;

import java.util.EnumMap;
import java.util.Map;

import what.forum.ForumActivity;
import what.home.HomeActivity;
import what.inbox.InboxActivity;
import what.search.TorrentSearchActivity;

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
	INBOX("Inbox"), BARCODE_SCANNER("Barcode Scanner");

	/** The map. */
	private static Map<MenuItems, Class<?>> map;

	/** The name. */
	private final String name;

	/**
	 * Inits the.
	 */
	public static void init() {
		map = new EnumMap<MenuItems, Class<?>>(MenuItems.class);
		map.put(HOME, HomeActivity.class);
		map.put(FORUM, ForumActivity.class);
		map.put(TORRENTS, TorrentSearchActivity.class);
		map.put(REQUESTS, HomeActivity.class);
		map.put(USERS, HomeActivity.class);
		map.put(INBOX, InboxActivity.class);
		map.put(BARCODE_SCANNER, HomeActivity.class);
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
