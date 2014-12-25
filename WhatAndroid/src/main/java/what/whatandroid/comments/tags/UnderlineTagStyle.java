package what.whatandroid.comments.tags;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

/**
 * Applies underline formatting
 */
public class UnderlineTagStyle implements TagStyle {
    @Override
    public Spannable getStyle(CharSequence param, CharSequence text) {
        SpannableString styled = new SpannableString(text);
        styled.setSpan(new UnderlineSpan(), 0, styled.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styled;
    }
}
