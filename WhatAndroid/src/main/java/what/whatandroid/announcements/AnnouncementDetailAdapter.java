package what.whatandroid.announcements;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import what.whatandroid.R;

import java.util.ArrayList;

/**
 * Adapter for showing the detail view of an announcement, takes the split
 * up text of the announcement and displays it
 */
public class AnnouncementDetailAdapter extends ArrayAdapter<String> {
	private final LayoutInflater inflater;

	public AnnouncementDetailAdapter(Context context, ArrayList<String> text){
		super(context, R.layout.list_announcement_text, text);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_announcement_text, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView)convertView.findViewById(R.id.announcement_text);
			convertView.setTag(holder);
		}
		holder.text.setText(Html.fromHtml(getItem(position)));
		return convertView;
	}

	private static class ViewHolder {
		public TextView text;
	}
}
