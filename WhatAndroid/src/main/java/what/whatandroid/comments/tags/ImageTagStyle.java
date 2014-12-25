package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;

import what.whatandroid.comments.spans.ShowImageSpan;

/**
 * Handles image tags on the site, instead of loading them inline we show
 * a clickable span to open a pop-up containing the image
 */
public class ImageTagStyle implements TagStyle {
    @Override
    public Spannable getStyle(CharSequence param, CharSequence text) {
        SpannableString styled;
        String url;
        if (param != null) {
            url = param.toString();
        } else {
            url = text.toString();
        }
        styled = new SpannableString(url);
        styled.setSpan(new ShowImageSpan(url), 0, styled.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styled;
    }
}
