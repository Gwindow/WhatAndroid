package what.widget;

import java.text.DecimalFormat;

import what.gui.R;
import what.settings.Settings;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import api.soup.MySoup;
import api.user.User;
import api.util.CouldNotLoadException;

public class UserStatsWidgetProvider extends AppWidgetProvider {
	private RemoteViews remoteViews;
	private ComponentName userStatsWidget;
	private int id = Integer.parseInt(MySoup.getIndex().getResponse().getId());
	private String username;
	private String up, down, ratio;
	private String seeding, leeching;
	private User user;
	private DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		remoteViews = new RemoteViews(context.getPackageName(), R.layout.userstatswidget);
		userStatsWidget = new ComponentName(context, UserStatsWidgetProvider.class);

		new LoadProfile().execute();

		appWidgetManager.updateAppWidget(userStatsWidget, remoteViews);
	}

	private void populateUserStats() {
		username = user.getProfile().getUsername();
		up = toGBString(user.getProfile().getRanks().getUploaded().toString() + " GB");
		down = toGBString(user.getProfile().getRanks().getDownloaded().toString() + " GB");
		ratio = user.getProfile().getStats().getRatio().toString();
		seeding = user.getProfile().getCommunity().getSeeding();
		leeching = user.getProfile().getCommunity().getLeeching();

		remoteViews.setTextViewText(R.id.username, username);
		remoteViews.setTextViewText(R.id.upvalue, up);
		remoteViews.setTextViewText(R.id.downvalue, down);
		remoteViews.setTextViewText(R.id.ratiovalue, ratio);
		remoteViews.setTextViewText(R.id.seedingvalue, seeding);
		remoteViews.setTextViewText(R.id.leechingvalue, leeching);
	}

	private String toGBString(String s) {
		double d = Double.parseDouble(s) / Math.pow(1024, 3);
		return df.format(d);
	}

	private class LoadProfile extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (MySoup.isLoggedIn() == true) {
				user = User.userFromId(id);
				return user.getStatus();
			}
			if (MySoup.isLoggedIn() == false) {
				try {
					MySoup.login("login.php", Settings.getUsername(), Settings.getPassword());
					user = User.userFromId(id);
					return user.getStatus();
				} catch (CouldNotLoadException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateUserStats();
			}
			if (status == false) {

			}
		}
	}
}