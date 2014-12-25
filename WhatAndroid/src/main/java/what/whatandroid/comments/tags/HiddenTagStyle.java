package what.whatandroid.comments.tags;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import what.whatandroid.comments.spans.HiddenTextSpan;

/**
 * Handles site hidden and mature tags
 */
public class HiddenTagStyle implements TagStyle {
    @Override
    public Spannable getStyle(CharSequence param, CharSequence text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        HiddenTextSpan hiddenSpan;
        if (param != null) {
            ssb.append(param).append(": show");
            hiddenSpan = new HiddenTextSpan(param.toString(), text.toString());
        } else {
            ssb.append("Show hidden text");
            hiddenSpan = new HiddenTextSpan(null, text.toString());
        }
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(hiddenSpan, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
