package what.gui;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * http://stackoverflow.com/questions/1697084/handle-textview-link-click-in-my-android-app 2
 * 
 * @author Gwindow
 * @since Aug 3, 2012 6:25:57 PM
 */
public class MyTextView extends TextView {
	private static final String BASE = "http://ssl.what.cd/";
	private static final String SMILEY = "static/common/smileys/";

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setText(String html) {
		Document d = Jsoup.parse(html);
		Elements imgs = d.getElementsByTag("img");
		final HashMap<String, String> smileys = new HashMap<String, String>();
		final HashMap<String, String> images = new HashMap<String, String>();

		for (Element e : imgs) {
			String src = (e.attributes().get("src"));
			if (src.startsWith(SMILEY)) {
				// src = BASE + src;
				src = src.substring(src.lastIndexOf("/") + 1);
				smileys.put(e.toString(), src);
			} else {
				images.put(e.toString(), src);
			}
		}
		for (final String s : smileys.keySet()) {
			html = html.replace(s, smileys.get(s));
		}
		for (final String s : images.keySet()) {
			html = html.replace(s, images.get(s));
		}
		setText(Html.fromHtml(html, null, null));
		for (final String s : images.keySet()) {
			MyClickableSpan.clickify(this, images.get(s), new MyClickableSpan.OnClickListener() {
				@Override
				public void onClick() {
					new ImageLightBoxDialog(MyTextView.this.getContext(), images.get(s));
				}
			});
		}
		// Linkify.addLinks(this, Linkify.ALL);
	}
}
