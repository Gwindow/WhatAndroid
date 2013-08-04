package what.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import api.barcode.Barcode;
import what.gui.BundleKeys;
import what.gui.R;
import what.search.RequestsSearchActivity;
import what.search.TorrentSearchActivity;

/**
 * Adapter class for the BarcodeListFragment to setup views
 * for the barcodes nicely
 */
public class BarcodeAdapter extends ArrayAdapter<Barcode> {
	private final Context context;
	private List<Barcode> items;

	public BarcodeAdapter(Context context, int resource, List<Barcode> items){
		super(context, resource, items);
		this.context = context;
		this.items = items;
	}

	public List<Barcode> getItems(){
		return items;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View barcodeView = inflater.inflate(R.layout.barcode_info, parent, false);

		//If we have search terms we want to show them as the title, if not show the upc as title
		//and hide the second view
		TextView title = (TextView)barcodeView.findViewById(R.id.barcode_title);
		TextView detail = (TextView)barcodeView.findViewById(R.id.barcode_detail);
		Button searchTorrents = (Button)barcodeView.findViewById(R.id.barcode_search_torrents);
		Button searchRequests = (Button)barcodeView.findViewById(R.id.barcode_search_requests);

		Barcode b = items.get(pos);
		if (b.hasSearchTerms()){
			title.setText(b.getSearchTerms());
			detail.setText("UPC: " + b.getUpc());
			searchTorrents.setTag(b.getSearchTerms());
			searchRequests.setTag(b.getSearchTerms());
			searchTorrents.setOnClickListener(new LaunchTorrentSearch());
			searchRequests.setOnClickListener(new LaunchRequestSearch());
		}
		else {
			title.setText("UPC: " + b.getUpc());
			detail.setVisibility(View.GONE);
			searchTorrents.setVisibility(View.GONE);
			searchRequests.setVisibility(View.GONE);
		}
		//Setup the delete button
		ImageButton remove = (ImageButton)barcodeView.findViewById(R.id.barcode_delete);

		//TODO: Is there not a better way to have all the stuff be clickable?
		//Would we instead have another custom click listener for the list item?
		remove.setFocusable(false);
		remove.setFocusableInTouchMode(false);
		searchTorrents.setFocusable(false);
		searchTorrents.setFocusableInTouchMode(false);
		searchRequests.setFocusable(false);
		searchRequests.setFocusableInTouchMode(false);
		//We set the button's tag to its item index
		remove.setTag(pos);
		remove.setOnClickListener(new RemoveItemListener());

		return barcodeView;
	}

	/**
	 * A custom on click listener to remove entries from the list
	 */
	private class RemoveItemListener implements View.OnClickListener {
		@Override
		public void onClick(View v){
			Integer pos = (Integer)v.getTag();
			remove(items.get(pos));
			notifyDataSetChanged();
		}
	}

	/**
	 * Launch a torrent search with the barcode's search terms (stored in the tag)
	 */
	private class LaunchTorrentSearch implements View.OnClickListener {
		@Override
		public void onClick(View v){
			Intent intent = new Intent(context, TorrentSearchActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(BundleKeys.SEARCH_STRING, v.getTag().toString());
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}

	/**
	 * Launch a request search with the barcode's search terms (stored in the tag)
	 */
	private class LaunchRequestSearch implements View.OnClickListener {
		@Override
		public void onClick(View v){
			Intent intent = new Intent(context, RequestsSearchActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(BundleKeys.SEARCH_STRING, v.getTag().toString());
			intent.putExtras(bundle);
			context.startActivity(intent);
		}
	}
}
