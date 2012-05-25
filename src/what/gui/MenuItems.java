package what.gui;

import java.util.EnumMap;
import java.util.Map;

import what.forum.section.SectionActivity2;

/**
 * @author Gwindow
 * @since May 25, 2012 9:44:27 AM
 */
public enum MenuItems {
	HOME("Home"), FORUM("Forum"), SUBMENU("Submenu"), TEST("TEST");

	private static Map<MenuItems, Class<?>> map;

	private final String name;

	public static void init() {
		map = new EnumMap<MenuItems, Class<?>>(MenuItems.class);
		map.put(FORUM, SectionActivity2.class);
		map.put(TEST, SectionActivity2.class);
	}

	private MenuItems(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public static boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public static Class<?> get(Object key) {
		return map.get(key);
	}

	/**
	 * @return
	 * @see java.util.Map#size()
	 */
	public static int size() {
		return map.size();
	}
}
