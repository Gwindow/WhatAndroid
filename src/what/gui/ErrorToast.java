package what.gui;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Gwindow
 * @since May 10, 2012 7:01:40 PM
 */
public class ErrorToast {
	public static void show(Context context, Class<?> c) {
		Toast.makeText(context, "Could not load " + c.getSimpleName(), Toast.LENGTH_LONG).show();
	}
}
