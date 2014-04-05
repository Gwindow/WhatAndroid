package what.whatandroid.comments;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import api.comments.SimpleComment;
import com.nostra13.universalimageloader.core.ImageLoader;
import what.whatandroid.R;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

import java.util.Date;
import java.util.List;

/**
 * Adapter for displaying a list of user comments
 */
public class CommentsAdapter extends ArrayAdapter<SimpleComment> {
	private final LayoutInflater inflater;

	public CommentsAdapter(Context context){
		super(context, R.layout.list_user_comment);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public CommentsAdapter(Context context, List<? extends SimpleComment> comments){
		super(context, R.layout.list_user_comment);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//Constructor doesn't take a ? extends type but add all does
		addAll(comments);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder)convertView.getTag();
		}
		else {
			convertView = inflater.inflate(R.layout.list_user_comment, parent, false);
			holder = new ViewHolder();
			holder.username = (TextView)convertView.findViewById(R.id.username);
			holder.postDate = (TextView)convertView.findViewById(R.id.post_date);
			holder.commentText = (TextView)convertView.findViewById(R.id.comment_text);
			holder.image = (ImageView)convertView.findViewById(R.id.image);
			holder.spinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.listener = new ImageLoadingListener(holder.spinner);
			convertView.setTag(holder);
		}
		SimpleComment comment = getItem(position);
		holder.username.setText(comment.getAuthor());
		holder.postDate.setText(DateUtils.getRelativeTimeSpanString(comment.getTimePosted().getTime(),
			new Date().getTime(), DateUtils.WEEK_IN_MILLIS));
		holder.commentText.setText(comment.getBody());

		String imgUrl = comment.getAvatar();
		if (SettingsActivity.imagesEnabled(getContext()) && imgUrl != null && !imgUrl.isEmpty()){
			ImageLoader.getInstance().displayImage(imgUrl, holder.image, holder.listener);
		}
		else {
			holder.image.setVisibility(View.GONE);
			holder.spinner.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView username, postDate, commentText;
		public ImageView image;
		public ProgressBar spinner;
		public ImageLoadingListener listener;
	}
}
