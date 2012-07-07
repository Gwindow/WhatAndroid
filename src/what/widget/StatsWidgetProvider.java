package what.widget;

import what.gui.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * The Class StatsWidgetProvider.
 * 
 * @author Gwindow
 * @since Jul 6, 2012 2:28:49 PM
 */
public class StatsWidgetProvider extends AppWidgetProvider {

	private RemoteViews remoteViews;
	private ComponentName componentName;

	/**
	 * On update.
	 * 
	 * @param context
	 *            the context
	 * @param appWidgetManager
	 *            the app widget manager
	 * @param appWidgetIds
	 *            the app widget ids
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.stats_widget);
		componentName = new ComponentName(context, StatsWidgetProvider.class);

		appWidgetManager.updateAppWidget(componentName, remoteViews);
	}
}