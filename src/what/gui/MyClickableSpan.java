package what.gui;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class MyClickableSpan extends ClickableSpan {

	private OnClickListener mListener;

	public MyClickableSpan(OnClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View widget) {
		if (mListener != null)
			mListener.onClick();
	}

	public interface OnClickListener {
		void onClick();
	}

	public static void clickify(TextView view, final String clickableText, final MyClickableSpan.OnClickListener listener) {

		CharSequence text = view.getText();
		String string = text.toString();
		MyClickableSpan span = new MyClickableSpan(listener);

		int start = string.indexOf(clickableText);
		int end = start + clickableText.length();
		if (start == -1)
			return;

		if (text instanceof Spannable) {
			((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			SpannableString s = SpannableString.valueOf(text);
			s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			view.setText(s);
		}

		MovementMethod m = view.getMovementMethod();
		if ((m == null) || !(m instanceof LinkMovementMethod)) {
			view.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}