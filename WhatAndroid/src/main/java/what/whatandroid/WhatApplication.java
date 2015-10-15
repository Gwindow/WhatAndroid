package what.whatandroid;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import api.soup.MySoup;
import what.whatandroid.imgloader.ImageLoadFailTracker;

/**
 * Created by fatih on 11.9.2015.
 */
public class WhatApplication extends Application {

	//TODO: Developers put your local Gazelle install IP here instead of testing on the live site
	//I recommend setting up with Vagrant: https://github.com/dr4g0nnn/VagrantGazelle
	public static final String DEFAULT_SITE = "10.0.0.2:8080/";

	@Override
	public void onCreate() {
		super.onCreate();

		initSoup(DEFAULT_SITE);
	}

	/**
	 * Initialize MySoup so that we can start making API requests
	 */
	public void initSoup(String site) {
		MySoup.setSite(site, true);
		MySoup.setUserAgent("WhatAndroid Android");
	}

	/**
	 * Main method for all image displayin. opertaions.
	 * Fail tracking is done here insted of wherever this method is called from.
	 */

	public static void loadImage(Context context, final String url, final ImageView imageView, final ProgressBar spinner, final ImageLoadFailTracker failTracker, final Integer failImage) {

		if (failTracker != null && failTracker.failed(url)) {
			if (spinner != null) spinner.setVisibility(View.GONE);
			if (failImage != null) {
				imageView.setImageResource(failImage);
			} else {
				imageView.setImageResource(R.drawable.no_artwork);
			}
		} else {
			if (spinner != null) spinner.setVisibility(View.VISIBLE);
			Glide.with(context)
					.load(url)
					.listener(new RequestListener<String, GlideDrawable>() {
						@Override
						public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
							if (spinner != null) {
								spinner.setVisibility(View.GONE);
							}
							if (failTracker != null) {
								failTracker.addFailed(url);
							}
							if (failImage != null) {
								imageView.setImageResource(failImage);
							} else {
								imageView.setImageResource(R.drawable.no_artwork);
							}
							return true;
						}

						@Override
						public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
							if (spinner != null) {
								spinner.setVisibility(View.GONE);
							}
//                            imageView.setImageDrawable(resource);
							return false;
						}
					})
					.into(imageView);
		}

	}

}