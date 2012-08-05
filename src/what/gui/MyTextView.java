package what.gui;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

	/*
	 * private static HashMap<String, String> smileys; static { smileys.put(":angry:", "angry.gif"); smileys.put(":D",
	 * "biggrin.gif"); smileys.put(":|", "blank.gif"); smileys.put(":blush:", "blush.gif"); smileys.put(":cool:",
	 * "cool.gif"); smileys.put(":crying:", "crying.gif"); smileys.put("&gt;.&gt;","eyesright.gif");
	 * smileys.put(":frown:", "frown.gif"); smileys.put("<3", "heart.gif"); smileys.put(":unsure:", "hmm.gif");
	 * smileys.put(":whatlove:", "ilu.gif"); smileys.put(":lol:", "laughing.gif"); smileys.put(":loveflac:",
	 * "loveflac.gif"); smileys.put(":ninja:", "ninja.gif"); smileys.put(":no:", "no.gif"); smileys.put(":nod:",
	 * "nod.gif"); smileys.put(":ohno:", "ohnoes.gif"); smileys.put(":omg:", "omg.gif"); smileys.put(":o",
	 * "ohshit.gif"); smileys.put(":paddle:", "paddle.gif"); smileys.put(":(", "sad.gif"); smileys.put(":shifty:",
	 * "shifty.gif"); smileys.put(":sick:", "sick.gif"); smileys.put(":)", "smile.gif"); smileys.put(":sorry:",
	 * "sorry.gif"); smileys.put(":thanks:", "thanks.gif"); smileys.put(":P", "tongue.gif");
	 * smileys.put(":wave:","wave.gif"); smileys.put(";-)", "wink.gif"); smileys.put(":creepy:", "creepy.gif");
	 * smileys.put(":worried:", "worried.gif"); smileys.put(":wtf:", "wtf.gif"); smileys.put(":wub:", "wub.gif");
	 * smileys.put(":a9love:", "ila9-what.gif"); smileys.put(":alderaanlove:'        , "ilalderaan-what.gif");
	 * smileys.put(":anankelove:'        , "ilananke-what.gif");
	 * smileys.put(":bionicsockslove:'     , "ilbionicsocks-what.gif");
	 * smileys.put(":changleslove:'        , "ilchangles-what.gif");
	 * smileys.put(":claptonlove:'     , "ilclapton-what.gif"); smileys.put(":emmlove:' , "ilemm-what.gif");
	 * smileys.put(":fzeroxlove:'      , "ilfzerox-what.gif"); smileys.put(":hothlove:' , "ilhoth-what.gif");
	 * smileys.put(":interstellarlove:'        , "ilinterstellar-what.gif");
	 * smileys.put(":jowalove:'        , "iljowa-what.gif"); smileys.put(":kharonlove:' , "ilkharon-what.gif");
	 * smileys.put(":kopitiamlove:'        , "ilkopitiam-what.gif"); smileys.put(":marienbadlove:",
	 * "ilmarienbad-what.gif"); smileys.put(":marigoldslove:", "ilmarigolds-what.gif");
	 * smileys.put(":mavericklove:'      , "ilmaverick-what.gif"); smileys.put(":mnlove:' , "ilmn-what.gif");
	 * smileys.put(":mre2melove:'      , "ilmre2me-what.gif"); smileys.put(":nandolove:", "ilnando-what.gif");
	 * smileys.put(":nightoathlove:", "ilnightoath-what.gif");
	 * smileys.put(":oinkmeuplove:'        , "iloinkmeup-what.gif");
	 * smileys.put(":padutchlove:'     , "ilpadutch-what.gif"); smileys.put(":paintrainlove:", "ilpaintrain-what.gif");
	 * smileys.put(":porkpielove:'     , "ilporkpie-what.gif"); smileys.put(":qmarklove:", "ilqmark-what.gif");
	 * smileys.put(":sdfflove:'        , "ilsdff-what.gif"); smileys.put(":seraphiellove:", "ilseraphiel-what.gif");
	 * smileys.put(":sisterraylove:", "ilsisterray-what.gif");
	 * smileys.put(":spacireleilove:'      , "ilspacirelei-what.gif"); smileys.put(":stwlove:' , "ilstw-what.gif");
	 * smileys.put(":theseuslove:'     , "iltheseus-what.gif"); smileys.put(":whatmanlove:' , "ilwhatman-what.gif");
	 * smileys.put(":whynotmicelove:'     , "ilwhynotmice-what.gif"); smileys.put(":wtelove:' 'ilwte-what.gif");
	 * smileys.put(":xorianlove:'      	, "ilxorian-what.gif"); smileys.put(":bashmorelove:", "ilbashmore-what.gif");
	 * smileys.put(":dumontlove:", "ildumont-what.gif"); smileys.put(":epihpronlove:", 'ilepihpron-what.gif");
	 * smileys.put(":gamutlove:", "ilgamut-what.gif"); smileys.put(":irredentialove:' 'ilirredentia-what.gif");
	 * smileys.put(":kitchenstafflove:',	 "ilkitchenstaff-what.gif"); smileys.put(":lenreklove:", "illenrek-what.gif");
	 * smileys.put(":lisbethlove:", "illisbeth-what.gif"); smileys.put(":sojlove:", "ilsoj-what.gif");
	 * smileys.put(":sinetaxlove:", "ilsinetax-what.gif"); smileys.put(":tolstoylove:", "iltolstoy-what.gif");
	 * smileys.put(":whitelightlove:", "ilwhitelight-what.gif"); smileys.put(":banananke:", "banananke-what.gif");
	 * smileys.put(":ajaxlove:", "ilajax-what.gif"); smileys.put(":athenalove:" , "ilathena-what.gif");
	 * smileys.put(":entrapmentlove:" , "ilentrapment-what.gif"); smileys.put(":iapetuslove:", "iliapetus-what.gif");
	 * smileys.put(":lesadieuxlove:","illesadieux-what.gif"); smileys.put(":lylaclove:", "illylac-what.gif"); }
	 */

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
					Intent intent = new Intent(MyTextView.this.getContext(), ImageDialogActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(BundleKeys.URL, images.get(s));
					intent.putExtras(bundle);
					MyTextView.this.getContext().startActivity(intent);
				}
			});
		}
		// Linkify.addLinks(this, Linkify.ALL);
	}
}
