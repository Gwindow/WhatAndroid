package what.whatandroid.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import what.whatandroid.R;

import java.util.Hashtable;

/**
 * A text view for displaying text using the FontAwesome font. Use this
 * for displaying icons and such
 */
public class TypeFacedTextView extends TextView {
	/**
	 * Font cache to avoid memory leaks on older phones and speed up loading in general
	 */
	private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

	public TypeFacedTextView(Context context, AttributeSet attrs){
		super(context, attrs);
		//Can't load fonts in edit mode
		if (!isInEditMode()){
			setFont(context, attrs);
		}
	}

	public TypeFacedTextView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		//Can't load fonts in edit mode
		if (!isInEditMode()){
			setFont(context, attrs);
		}
	}

	private void setFont(Context context, AttributeSet attrs){
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TypeFacedTextView, 0, 0);
		try {
			String fontName = a.getString(R.styleable.TypeFacedTextView_font);
			Typeface font = loadFont(context, fontName);
			setTypeface(font);
		}
		finally {
			a.recycle();
		}
	}

	/**
	 * Load a font from the cache if it's in there or load it in from the assets
	 *
	 * @param font font to load
	 * @return the font desired
	 */
	private static Typeface loadFont(Context context, String font){
		Typeface tf = fontCache.get(font);
		if (tf == null){
			tf = Typeface.createFromAsset(context.getAssets(), font);
			fontCache.put(font, tf);
		}
		return tf;
	}
}
