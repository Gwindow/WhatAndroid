package what.whatandroid.comments;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Handles site color tags like [color=blue] or [color=#hexcode]
 */
public class ColorTag implements ParameterizedTag {
	@Override
	public SpannableString getStyle(String param, String text){
		SpannableString styled = new SpannableString(text);
		//Default to secondary text dark if it's some unsupported color
		int color = 0xffbebebe;
		//If it's a site hex color then parse the hex value otherwise lookup the color name
		if (param.charAt(0) == '#'){
			color = Integer.parseInt(param.substring(1), 16);
			//Android colors are AARRGGBB but site colors are RRGGBB so stick on the alpha bits and make
			//the color fully opaque
			color |= 0xff << 24;
		}
		//Otherwise it's just color name
		else if (param.equalsIgnoreCase("red")){
			color = 0xffff4444;
		}
		else if (param.equalsIgnoreCase("green")){
			color = 0xff99cc00;
		}
		else if (param.equalsIgnoreCase("blue")){
			color = 0xff33b5e5;
		}
		else if (param.equalsIgnoreCase("purple")){
			color = 0xffaa66cc;
		}
		else if (param.equalsIgnoreCase("yellow")){
			color = 0xffffee33;
		}
		else if (param.equalsIgnoreCase("orange")){
			color = 0xffffbb33;
		}
		styled.setSpan(new ForegroundColorSpan(color), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
