package what.whatandroid.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import api.torrents.artist.SimilarArtists;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewArtistCallbacks;

/**
 * Adapter for displaying the list of similar artists
 */
public class SimilarArtistAdapter extends ArrayAdapter<SimilarArtists> implements AdapterView.OnItemClickListener {
	private final LayoutInflater inflater;
	private final ViewArtistCallbacks viewArtist;

	public SimilarArtistAdapter(Context context){
		super(context, R.layout.list_similar_artist);
		inflater = LayoutInflater.from(context);
		try {
			viewArtist = (ViewArtistCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewArtistCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_similar_artist, parent, false);
			holder = new ViewHolder();
			holder.artist = (TextView)convertView.findViewById(R.id.artist);
			convertView.setTag(holder);
		}
		holder.artist.setText(getItem(position).getName());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		//Account for the header view throwing off position by 1
		viewArtist.viewArtist(getItem(position - 1).getArtistId().intValue());
	}

	private static class ViewHolder {
		public TextView artist;
	}
}
