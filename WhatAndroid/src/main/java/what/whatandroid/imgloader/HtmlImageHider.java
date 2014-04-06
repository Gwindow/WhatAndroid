package what.whatandroid.imgloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * Hide any images encountered in the html by returning a transparent drawable
 */
public class HtmlImageHider implements Html.ImageGetter {
	private final Context context;
	private final Resources resources;

	public HtmlImageHider(Context context){
		this.context = context;
		resources = context.getResources();
	}

	@Override
	public Drawable getDrawable(String source){
		return new ColorDrawable(Color.TRANSPARENT);
	}
}
