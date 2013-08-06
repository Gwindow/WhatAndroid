package what.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
	private final LayoutInflater inflater;
	private List<Barcode> items;

	public BarcodeAdapter(Context context, int resource, List<Barcode> items){
		super(context, resource, items);
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	public List<Barcode> getItems(){
		return items;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		//Recycle views if necessary and setup the view holder
		ViewHolder holder;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.barcode_info, parent, false);
			//TODO: Why do the list items not highlight when clicked?
			convertView.setOnClickListener(new ItemClickListener());

			holder = new ViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.barcode_title);
			holder.detail = (TextView)convertView.findViewById(R.id.barcode_detail);

			holder.searchTorrents = (Button)convertView.findViewById(R.id.barcode_search_torrents);
			holder.searchRequests = (Button)convertView.findViewById(R.id.barcode_search_requests);
			holder.searchTorrents.setOnClickListener(new LaunchTorrentSearch());
			holder.searchRequests.setOnClickListener(new LaunchRequestSearch());

			holder.remove = (ImageButton)convertView.findViewById(R.id.barcode_delete);
			holder.remove.setOnClickListener(new RemoveItemListener());

			//The loading icon is only shown when we're actually loading
			holder.loading = (ProgressBar)convertView.findViewById(R.id.barcode_loading_indicator);
			holder.loading.setVisibility(View.GONE);

			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder)convertView.getTag();

		//Make sure the barcode item and remove pos tag stay up to date
		holder.barcode = items.get(pos);
		holder.remove.setTag(pos);

		if (holder.barcode.hasSearchTerms()){
			holder.title.setText(holder.barcode.getSearchTerms());
			holder.detail.setText("UPC: " + holder.barcode.getUpc());

			holder.searchTorrents.setTag(holder.barcode.getSearchTerms());
			holder.searchRequests.setTag(holder.barcode.getSearchTerms());

			//Make sure everything is visible
			holder.detail.setVisibility(View.VISIBLE);
			holder.searchTorrents.setVisibility(View.VISIBLE);
			holder.searchRequests.setVisibility(View.VISIBLE);
		}
		else {
			holder.title.setText("UPC: " + holder.barcode.getUpc());
			holder.detail.setVisibility(View.GONE);
			holder.searchTorrents.setVisibility(View.GONE);
			holder.searchRequests.setVisibility(View.GONE);
		}

		return convertView;
	}

	/**
	 * Click listener for the list items, will launch the
	 */
	private class ItemClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v){
			ViewHolder holder = (ViewHolder)v.getTag();
			//If we haven't loaded the search terms already, look them up
			if (!holder.barcode.hasSearchTerms())
				new LoadTerms(holder).execute();
		}
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

	/**
	 * Async task that takes a barcode and loads its search terms
	 * to provide some feedback
	 */
	private class LoadTerms extends AsyncTask<Void, Void, Boolean> {
		//Since we want to show/hide a spinner when loading the terms we need the holder to
		//change the item status
		private ViewHolder holder;

		/**
		 * We require the viewholder of the list item so that we can show
		 * and hide the loading indicator
		 * @param holder the list item view holder
		 */
		public LoadTerms(ViewHolder holder){
			this.holder = holder;
		}

		@Override
		protected void onPreExecute(){
			holder.loading.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(Void... params){
			holder.barcode.determineSearchTerms();
			return holder.barcode.hasSearchTerms();
		}

		@Override
		protected void onPostExecute(Boolean status){
			holder.loading.setVisibility(View.GONE);
			if (status)
				notifyDataSetChanged();
			else
				Toast.makeText(context, "Failed to load terms", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Contains information about the list view items so that we
	 * conveniently & quickly access them through their tag
	 */
	private static class ViewHolder {
		public TextView title, detail;
		public Button searchTorrents, searchRequests;
		public ImageButton remove;
		public Barcode barcode;
		public ProgressBar loading;
	}
}
