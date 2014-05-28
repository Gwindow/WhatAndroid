package what.whatandroid.comments.spans;

import android.os.Parcel;
import android.text.style.QuoteSpan;

/**
 * A quote span that applies a larger amount of spacing than the regular one
 */
public class LargeQuoteSpan extends QuoteSpan {
	public LargeQuoteSpan(){
	}

	public LargeQuoteSpan(int color){
		super(color);
	}

	public LargeQuoteSpan(Parcel src){
		super(src);
	}

	@Override
	public int getLeadingMargin(boolean first){
		return super.getLeadingMargin(first) + 6;
	}
}
