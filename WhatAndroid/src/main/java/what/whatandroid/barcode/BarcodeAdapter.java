package what.whatandroid.barcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import api.barcode.Barcode;
import what.whatandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying the list of user barcodes
 */
public class BarcodeAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	private List<Barcode> barcodes;

	public BarcodeAdapter(Context context){
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		barcodes = new ArrayList<Barcode>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_barcode, parent, false);
			holder = new ViewHolder();
			holder.barcodeUpc = (TextView)convertView.findViewById(R.id.barcode_title);
			convertView.setTag(holder);
		}
		Barcode b = getItem(position);
		holder.barcodeUpc.setText(b.getUpc());
		return convertView;
	}

	@Override
	public int getCount(){
		return barcodes.size();
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	public Barcode getItem(int position){
		return barcodes.get(position);
	}

	public List<Barcode> getBarcodes(){
		return barcodes;
	}

	public void addAll(List<Barcode> b){
		barcodes.addAll(b);
	}

	public void clear(){
		barcodes.clear();
	}

	private static class ViewHolder {
		public TextView barcodeUpc;
	}
}
