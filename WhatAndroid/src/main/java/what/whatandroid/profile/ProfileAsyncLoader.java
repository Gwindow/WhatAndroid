package what.whatandroid.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.user.UserProfile;

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
			while (true){
				profile = UserProfile.fromId(userId);
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (!profile.getStatus() && profile.getError().equalsIgnoreCase("rate limit exceeded")){
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
