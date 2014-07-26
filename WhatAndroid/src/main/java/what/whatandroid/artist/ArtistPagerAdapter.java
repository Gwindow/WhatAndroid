package what.whatandroid.artist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import api.torrents.artist.Artist;
import what.whatandroid.callbacks.LoadingListener;

/**
 * Pager adapter for displaying two fragments showing the artist picture + releases
 * and description, tags and similar artists
 */
public class ArtistPagerAdapter extends FragmentPagerAdapter implements LoadingListener<Artist> {
	/**
	 * The fragments showing artist img + releases and artist description information
	 */
	private ArtistReleasesFragment releasesFragment;
	private ArtistDescriptionFragment descriptionFragment;
	/**
	 * The artist being viewed
	 */
	private Artist artist;

	public ArtistPagerAdapter(FragmentManager fm){
		super(fm);
	}

	@Override
	public Fragment getItem(int position){
		switch (position){
			case 0:
				return new ArtistReleasesFragment();
			default:
				return new ArtistDescriptionFragment();
		}
	}

	@Override
	public int getCount(){
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position){
		switch (position){
			case 0:
				return "Releases";
			default:
				return "Description";
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position){
		Fragment f = (Fragment)super.instantiateItem(container, position);
		if (position == 0){
			releasesFragment = (ArtistReleasesFragment)f;
		}
		else {
			descriptionFragment = (ArtistDescriptionFragment)f;
		}
		if (artist != null){
			((LoadingListener<Artist>)f).onLoadingComplete(artist);
		}
		return f;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		super.destroyItem(container, position, object);
		switch (position){
			case 0:
				releasesFragment = null;
				break;
			default:
				descriptionFragment = null;
				break;
		}
	}

	@Override
	public void onLoadingComplete(Artist data){
		artist = data;
		if (releasesFragment != null){
			releasesFragment.onLoadingComplete(artist);
		}
		if (descriptionFragment != null){
			descriptionFragment.onLoadingComplete(artist);
		}
	}
}
