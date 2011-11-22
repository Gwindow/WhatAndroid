package what.gui;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import api.util.CouldNotLoadException;

/**
 * Connects to the update site and checks for messages of updates
 * 
 * @author Gwindow
 * 
 */
public class Updater {
	Document doc = null;

	public Updater() throws CouldNotLoadException {
		try {
			// TODO fix update url
			doc = Jsoup.connect("http://db.tt/e0uu5bFZ").get();
		} catch (Exception e) {
			throw new CouldNotLoadException("Could not load update site");
		}
	}

	public String getMessage() {
		if (doc != null) {
			String message = doc.getElementsByTag("message").text();
			return message;
		}
		return null;
	}

	public Double getVersion() {
		if (doc != null) {
			String version = doc.getElementsByTag("version").text();
			return Double.parseDouble(version);
		}
		return null;
	}

	public String getDownloadLink() {
		if (doc != null) {
			String download = doc.getElementsByTag("link").text();
			return download;
		}
		return null;
	}
}