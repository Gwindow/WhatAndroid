package what.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom TextView with a border around it
 * 
 * @author Tim
 * 
 */
public class TextViewBorder extends TextView {

	public TextViewBorder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextViewBorder(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextViewBorder(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(3);
		getLocalVisibleRect(rect);
		canvas.drawRect(rect, paint);
	}

}
