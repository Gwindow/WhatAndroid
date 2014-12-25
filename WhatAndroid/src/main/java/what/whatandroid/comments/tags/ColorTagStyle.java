package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * Handles site color tags like [color=blue] or [color=#hexcode]
 */
public class ColorTagStyle implements TagStyle {
    @Override
    public Spannable getStyle(CharSequence param, CharSequence text) {
        SpannableString styled = new SpannableString(text);
        //Default to secondary text dark if it's some unsupported color
        int color;
        //If it's a site hex color then parse the hex value otherwise lookup the color name
        switch (param.charAt(0)) {
            case '#':
                color = Integer.parseInt(param.toString().substring(1), 16);
                //Android colors are AARRGGBB but site colors are RRGGBB so stick on the alpha bits and make
                //the color fully opaque
                color |= 0xff << 24;
                break;
            case 'r':
                color = 0xffff4444;
                break;
            case 'g':
                color = 0xff99cc00;
                break;
            case 'b':
                color = 0xff33b5e5;
                break;
            case 'p':
                color = 0xffaa66cc;
                break;
            case 'y':
                color = 0xffffee33;
                break;
            case 'o':
                color = 0xffffbb33;
                break;
            default:
                color = 0xffbebebe;
        }
        styled.setSpan(new ForegroundColorSpan(color), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styled;
    }
}
