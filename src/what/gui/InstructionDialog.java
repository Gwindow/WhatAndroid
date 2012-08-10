package what.gui;

import what.settings.Settings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * @author Gwindow
 * @since Jun 3, 2012 9:43:25 AM
 */
public class InstructionDialog extends AlertDialog.Builder implements OnClickListener {
	public static final int HOME = 0;
	public static final int FORUM = 1;
	private Context context;
	private int tag;

	public InstructionDialog(Context context, int tag) {
		super(context);
		this.tag = tag;
		init();
	}

	private void init() {
		setCancelable(true);
		String message = null;
		switch (tag) {
			case HOME:
				if (!Settings.getFirstHome()) {
					message =
							"Touching the icon in the top left will scroll you to the top of a page, touching it again will take you back a page. Double tapping the title of the page will bring you back home. Long touching posts, search results, etc will bring up more options. Please take a moment to configure settings. they can be found through the menu on the top right.";
				}
				break;
			case FORUM:
				if (!Settings.getFirstForum()) {
					message =
							"If a thread has mutliple pages just keep scrolling, pages will autoload. Long touching a post will give you more options. Touching a url to an image will open the image in a popup. ";
				}
				break;

		}
		if (message != null) {
			create();
			show();
		}
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}
