package what.whatandroid.artist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import api.torrents.artist.Artist;
import what.whatandroid.search.ArtistSearchFragment;

/**
 * AsyncLoader to load an artist from id or name, or if use search then just return the
 * loaded artist info in the search fragment
 */
public class ArtistAsyncLoader extends AsyncTaskLoader<Artist> {
	private Artist artist;
	private int artistId;
	private String artistName;
	private boolean useSearch;

	public ArtistAsyncLoader(Context context, Bundle args){
		super(context);
		artistId = args.getInt(ArtistActivity.ARTIST_ID, -1);
		artistName = args.getString(ArtistActivity.ARTIST_NAME);
		useSearch = args.getBoolean(ArtistActivity.USE_SEARCH, false);
	}

	@Override
	public Artist loadInBackground(){
		if (artist == null){
			while (true){
				if (useSearch){
					//No need to check rate limit if the artist fragment's already loaded for us
					artist = ArtistSearchFragment.getArtist();
					return artist;
				}
				else if (artistName != null){
					artist = Artist.fromName(artistName);
				}
				else {
					artist = Artist.fromId(artistId);
				}
				//If we get rate limited wait and retry. It's very unlikely the user has used all 5 of our
				//requests per 10s so don't wait the whole time initially
				if (!artist.getStatus() && artist.getError().equalsIgnoreCase("rate limit exceeded")){
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
		return artist;
	}

	@Override
	protected void onStartLoading(){
		if (artist != null){
			deliverResult(artist);
		}
		else {
			forceLoad();
		}
	}
}
