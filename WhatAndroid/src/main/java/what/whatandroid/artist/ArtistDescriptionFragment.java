package what.whatandroid.artist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;

import api.torrents.artist.Artist;
import api.torrents.artist.Tags;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.comments.HTMLListTagHandler;
import what.whatandroid.imgloader.HtmlImageHider;

/**
 * Fragment to display artist description, tags and similar artists listing
 */
public class ArtistDescriptionFragment extends Fragment implements LoadingListener<Artist> {
	/**
	 * The artist being shown
	 */
	private Artist artist;
	private TextView description, tags, noContent;
	private SimilarArtistAdapter adapter;

	public ArtistDescriptionFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		noContent = (TextView)view.findViewById(R.id.no_content_notice);
		View header = inflater.inflate(R.layout.header_artist_description, list, false);
		description = (TextView)header.findViewById(R.id.description);
		tags = (TextView)header.findViewById(R.id.tags);
		noContent.setText("none");
		list.addHeaderView(header, null, false);
		list.setHeaderDividersEnabled(false);
		adapter = new SimilarArtistAdapter(getActivity());
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);

		if (artist != null){
			populateViews();
		}
		return view;
	}

	private void populateViews(){
		if (artist.getResponse().getSimilarArtists().isEmpty()){
			noContent.setVisibility(View.VISIBLE);
		}
		else if (adapter.isEmpty()){
			adapter.addAll(artist.getResponse().getSimilarArtists());
			adapter.notifyDataSetChanged();
		}
		if (!artist.getResponse().getBody().isEmpty()){
			description.setText(Html.fromHtml(artist.getResponse().getBody(),
				new HtmlImageHider(getActivity()), new HTMLListTagHandler()));
		}
		else {
			description.setVisibility(View.GONE);
		}
		//Sort the tags in descending order
		Collections.sort(artist.getResponse().getTags(),
			Collections.reverseOrder(new Tags.TagComparator()));
		String tagList = artist.getResponse().getTags().toString();
		tagList = tagList.substring(tagList.indexOf('[') + 1, tagList.lastIndexOf(']'));
		tags.setText(tagList);
	}

	@Override
	public void onLoadingComplete(Artist data){
		artist = data;
		if (isAdded()){
			populateViews();
		}
	}
}
