package what.whatandroid.barcode;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.barcode.Barcode;
import api.search.crossreference.CrossReference;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewSearchCallbacks;
import what.whatandroid.search.SearchActivity;

import java.util.Date;

/**
 * Adapter for displaying the list of user barcodes
 */
public class BarcodeAdapter extends ArrayAdapter<Barcode> {
	private final LayoutInflater inflater;
	private final TextView noBarcodes;
	private final ViewSearchCallbacks viewSearch;

	public BarcodeAdapter(Context context, TextView noBarcodes){
		super(context, R.layout.list_barcode);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.noBarcodes = noBarcodes;
		try {
			viewSearch = (ViewSearchCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewSearchCallbacks");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_barcode, parent, false);
			holder = new ViewHolder();
			holder.barcodeTitle = (TextView)convertView.findViewById(R.id.barcode_title);
			holder.barcodeSecondary = (TextView)convertView.findViewById(R.id.barcode_secondary);
			holder.dateScanned = (TextView)convertView.findViewById(R.id.date_scanned);
			holder.loadTerms = (ImageButton)convertView.findViewById(R.id.load_terms);
			holder.loadTerms.setImageResource(R.drawable.ic_file_download_24dp);
			holder.loadingSpinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.delete = (ImageButton)convertView.findViewById(R.id.delete);
			holder.delete.setImageResource(R.drawable.ic_search_24dp);
			holder.searchTorrents = (Button)convertView.findViewById(R.id.search_torrents);
			holder.searchRequests = (Button)convertView.findViewById(R.id.search_requests);
			holder.loadTermsListener = new LoadTermsListener(holder);
			holder.deleteClickListener = new DeleteClickListener();
			holder.torrentsListener = new SearchClickListener(SearchActivity.TORRENT);
			holder.requestsListener = new SearchClickListener(SearchActivity.REQUEST);
			convertView.setTag(holder);
		}
		Barcode b = getItem(position);
		holder.loadTermsListener.setBarcode(b);
		holder.dateScanned.setText(DateUtils.getRelativeTimeSpanString(b.getAdded().getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

		if (!b.getSearchTerms().isEmpty()){
			holder.barcodeTitle.setText(b.getSearchTerms());
			holder.barcodeSecondary.setText(b.getUpc());
			holder.barcodeSecondary.setVisibility(View.VISIBLE);
			holder.loadTerms.setVisibility(View.GONE);
			holder.searchTorrents.setVisibility(View.VISIBLE);
			holder.searchRequests.setVisibility(View.VISIBLE);
		}
		else {
			holder.barcodeTitle.setText(b.getUpc());
			holder.barcodeSecondary.setVisibility(View.INVISIBLE);
			holder.searchTorrents.setVisibility(View.GONE);
			holder.searchRequests.setVisibility(View.GONE);
			if (holder.loadingSpinner.getVisibility() != View.VISIBLE){
				holder.loadTerms.setVisibility(View.VISIBLE);
			}
			else {
				holder.loadTerms.setVisibility(View.GONE);
			}
		}
		holder.deleteClickListener.setBarcode(b);
		holder.torrentsListener.setBarcode(b);
		holder.requestsListener.setBarcode(b);

		holder.loadTerms.setOnClickListener(holder.loadTermsListener);
		holder.delete.setOnClickListener(holder.deleteClickListener);
		holder.searchTorrents.setOnClickListener(holder.torrentsListener);
		holder.searchRequests.setOnClickListener(holder.requestsListener);
		return convertView;
	}

	private static class ViewHolder {
		public TextView barcodeTitle, barcodeSecondary, dateScanned;
		private ImageButton loadTerms, delete;
		private ProgressBar loadingSpinner;
		private Button searchTorrents, searchRequests;
		private LoadTermsListener loadTermsListener;
		private DeleteClickListener deleteClickListener;
		private SearchClickListener torrentsListener, requestsListener;
	}

	private class DeleteClickListener implements View.OnClickListener {
		private Barcode barcode;

		public void setBarcode(Barcode barcode){
			this.barcode = barcode;
		}

		@Override
		public void onClick(View v){
			remove(barcode);
			notifyDataSetChanged();
			new DeleteBarcodeTask(getContext()).execute(barcode);
			if (getCount() == 0){
				noBarcodes.setVisibility(View.VISIBLE);
			}
		}
	}

	private class SearchClickListener implements View.OnClickListener {
		private final int type;
		private Barcode barcode;

		public SearchClickListener(int type){
			this.type = type;
		}

		public void setBarcode(Barcode barcode){
			this.barcode = barcode;
		}

		@Override
		public void onClick(View v){
			viewSearch.startSearch(type, barcode.getSearchTerms(), barcode.getSearchTags());
		}
	}

	/**
	 * Listener for the load terms button, will launch an async task to load the
	 * terms for the barcode
	 */
	public class LoadTermsListener implements View.OnClickListener {
		/**
		 * The view holder containing the views to update depending on the loading result
		 */
		private ViewHolder holder;
		/**
		 * Barcode to load terms for
		 */
		private Barcode barcode;
		private LoadTermsTask loadTermsTask;

		public LoadTermsListener(ViewHolder holder){
			this.holder = holder;
		}

		public void setBarcode(Barcode barcode){
			if (loadTermsTask != null){
				holder.loadTerms.setVisibility(View.VISIBLE);
				holder.loadingSpinner.setVisibility(View.GONE);
				loadTermsTask.cancel(true);
			}
			this.barcode = barcode;
		}

		@Override
		public void onClick(View v){
			loadTermsTask = new LoadTermsTask();
			loadTermsTask.execute(barcode);
		}

		private class LoadTermsTask extends AsyncTask<Barcode, Void, String> {
			private Barcode b;

			@Override
			protected String doInBackground(Barcode... params){
				b = params[0];
				return CrossReference.termsFromUpc(b.getUpc());
			}

			@Override
			protected void onPreExecute(){
				holder.loadingSpinner.setVisibility(View.VISIBLE);
				holder.loadTerms.setVisibility(View.GONE);
			}

			@Override
			protected void onPostExecute(String s){
				if (s != null && !s.isEmpty()){
					b.setSearchTerms(s);
					holder.barcodeTitle.setText(s);
					holder.barcodeSecondary.setText(b.getUpc());
					holder.searchRequests.setVisibility(View.VISIBLE);
					holder.searchTorrents.setVisibility(View.VISIBLE);
					new UpdateBarcodeTask(getContext()).execute(b);
				}
				else {
					holder.barcodeSecondary.setText("No terms found");
				}
				holder.barcodeSecondary.setVisibility(View.VISIBLE);
				holder.loadingSpinner.setVisibility(View.GONE);
			}
		}
	}
}
