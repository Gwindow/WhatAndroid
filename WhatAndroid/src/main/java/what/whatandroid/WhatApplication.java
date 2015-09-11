package what.whatandroid;

import android.app.Application;

import api.soup.MySoup;

/**
 * Created by fatih on 11.9.2015.
 */
public class WhatApplication extends Application{

	//TODO: Developers put your local Gazelle install IP here instead of testing on the live site
	//I recommend setting up with Vagrant: https://github.com/dr4g0nnn/VagrantGazelle
	public static final String DEFAULT_SITE = "https://what.cd/";

	@Override
	public void onCreate() {
		super.onCreate();

		initSoup(DEFAULT_SITE);
	}

	/**
	 * Initialize MySoup so that we can start making API requests
	 */
	public void initSoup(String site){
		MySoup.setSite(site, true);
		MySoup.setUserAgent("WhatAndroid Android");
	}
}
