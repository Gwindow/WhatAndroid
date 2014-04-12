package what.whatandroid.barcode;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import api.barcode.Barcode;
import api.search.crossreference.CrossReference;
import what.whatandroid.R;

import java.util.Date;

/**
 * Adapter for displaying the list of user barcodes
 */
public class BarcodeAdapter extends ArrayAdapter<Barcode> {
	private final LayoutInflater inflater;
	private final TextView noBarcodes;

	public BarcodeAdapter(Context context, TextView noBarcodes){
		super(context, R.layout.list_barcode);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.noBarcodes = noBarcodes;
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
			holder.delete = (ImageButton)convertView.findViewById(R.id.delete);
			holder.loadTermsListener = new LoadTermsListener(holder.barcodeTitle, holder.barcodeSecondary, holder.loadTerms);
			convertView.setTag(holder);
		}
		final Barcode b = getItem(position);
		holder.loadTermsListener.setBarcode(b);
		holder.dateScanned.setText(DateUtils.getRelativeTimeSpanString(b.getAdded().getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));

		if (!b.getSearchTerms().isEmpty()){
			holder.barcodeTitle.setText(b.getSearchTerms());
			holder.barcodeSecondary.setText(b.getUpc());
			holder.barcodeSecondary.setVisibility(View.VISIBLE);
			holder.loadTerms.setVisibility(View.GONE);
		}
		else {
			holder.barcodeTitle.setText(b.getUpc());
			holder.barcodeSecondary.setVisibility(View.INVISIBLE);
			holder.loadTerms.setVisibility(View.VISIBLE);
		}

		holder.loadTerms.setOnClickListener(holder.loadTermsListener);
		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				remove(b);
				notifyDataSetChanged();
				new DeleteBarcodeTask(getContext()).execute(b);
				if (getCount() == 0){
					noBarcodes.setVisibility(View.VISIBLE);
				}
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		public TextView barcodeTitle, barcodeSecondary, dateScanned;
		private ImageButton loadTerms, delete;
		private LoadTermsListener loadTermsListener;
	}

	/**
	 * Listener for the load terms button, will launch an async task to load the
	 * terms for the barcode
	 */
	public class LoadTermsListener implements View.OnClickListener {
		private TextView title, secondary;
		private ImageButton loadTerms;
		private Barcode barcode;

		public LoadTermsListener(TextView title, TextView secondary, ImageButton loadTerms){
			this.title = title;
			this.secondary = secondary;
			this.loadTerms = loadTerms;
		}

		public void setBarcode(Barcode barcode){
			this.barcode = barcode;
		}

		@Override
		public void onClick(View v){
			new LoadTermsTask().execute(barcode);
		}

		private class LoadTermsTask extends AsyncTask<Barcode, Void, String> {
			@Override
			protected String doInBackground(Barcode... params){
				return CrossReference.termsFromUpc(barcode.getUpc());
			}

			@Override
			protected void onPostExecute(String s){
				if (s != null && !s.isEmpty()){
					barcode.setSearchTerms(s);
					title.setText(s);
					secondary.setText(barcode.getUpc());
					loadTerms.setVisibility(View.GONE);
					new UpdateBarcodeTask(getContext()).execute(barcode);
				}
				else {
					secondary.setText("No terms found");
				}
				secondary.setVisibility(View.VISIBLE);
			}
		}
	}
}
