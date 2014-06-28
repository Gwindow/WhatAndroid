package what.whatandroid.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;

import api.index.Index;
import api.soup.MySoup;
import api.user.UserProfile;
import api.util.CouldNotLoadException;
import what.whatandroid.R;

/**
 * AsyncLoader to load a user's profile and recent torrents from their id
 */
public class ProfileAsyncLoader extends AsyncTaskLoader<UserProfile> {
	private UserProfile profile;
	private int userId;

	public ProfileAsyncLoader(Context context, Bundle args){
		super(context);
		userId = args.getInt(ProfileActivity.USER_ID);
	}

	@Override
	public UserProfile loadInBackground(){
		if (profile == null){
			if (userId == MySoup.getUserId()){
				if (!refreshIndex()){
					return null;
				}
				else {
					Context context = getContext();
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
					preferences.edit()
						.putBoolean(context.getString(R.string.key_pref_new_notifications),
							MySoup.getIndex().getResponse().getNotifications().hasNewNotifications())
						.apply();
				}
			}
			while (true){
				profile = UserProfile.fromId(userId);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (!profile.getStatus() && profile.getError() != null && profile.getError().equalsIgnoreCase("rate limit exceeded")){
					try {
						Thread.sleep(3000);
					}
					catch (InterruptedException e){
						Thread.currentThread().interrupt();
					}
				}
				else {
					break;
				}
			}
		}
		return profile;
	}

	private boolean refreshIndex(){
		//Also handle the case where we've been rate limited
		while (true){
			try {
				MySoup.loadIndex();
			}
			catch (CouldNotLoadException e){
				return false;
			}
			Index index = MySoup.getIndex();
			if (index != null && !index.getStatus() && index.getError() != null && index.getError().equalsIgnoreCase("rate limit exceeded")){
				try {
					Thread.sleep(3000);
				}
				catch (InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
			else {
				break;
			}
		}
		return MySoup.getIndex() != null && MySoup.getIndex().getStatus();
	}

	@Override
	protected void onStartLoading(){
		if (profile != null){
			deliverResult(profile);
		}
		else {
			forceLoad();
		}
	}
}
