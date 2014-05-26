package what.whatandroid.comments;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
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
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.imgloader.HtmlImageHider;
import what.whatandroid.imgloader.ImageLoadFailTracker;
import what.whatandroid.imgloader.ImageLoadingListener;
import what.whatandroid.settings.SettingsActivity;

import java.util.Date;
import java.util.List;

/**
 * Adapter for displaying a list of user comments
 */
public class CommentsAdapter extends ArrayAdapter<SimpleComment> {
	private final LayoutInflater inflater;
	private final HtmlImageHider imageGetter;
	private ViewUserCallbacks viewUser;
	private ImageLoadFailTracker imageFailTracker;
	private boolean imagesEnabled;
	private WhatBBParser whatBBParser;

	public CommentsAdapter(Context context){
		super(context, R.layout.list_user_comment);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageGetter = new HtmlImageHider(context);
		imagesEnabled = SettingsActivity.imagesEnabled(context);
		imageFailTracker = new ImageLoadFailTracker();
		whatBBParser = new WhatBBParser();
		try {
			viewUser = (ViewUserCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewUserCallbacks");
		}
	}

	public CommentsAdapter(Context context, List<? extends SimpleComment> comments){
		super(context, R.layout.list_user_comment);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageGetter = new HtmlImageHider(context);
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
			holder.artContainer = convertView.findViewById(R.id.art_container);
			holder.image = (ImageView)convertView.findViewById(R.id.image);
			holder.spinner = (ProgressBar)convertView.findViewById(R.id.loading_indicator);
			holder.listener = new ImageLoadingListener(holder.spinner, holder.artContainer,
				imageFailTracker, R.drawable.no_avatar);
			holder.userClickListener = new UserClickListener();
			View header = convertView.findViewById(R.id.user_header);
			header.setOnClickListener(holder.userClickListener);
			convertView.setTag(holder);
		}
		SimpleComment comment = getItem(position);
		holder.userClickListener.userId = comment.getAuthorId();
		holder.username.setText(comment.getAuthor());
		holder.postDate.setText(DateUtils.getRelativeTimeSpanString(comment.getTimePosted().getTime(),
			new Date().getTime(), 0, DateUtils.FORMAT_ABBREV_ALL));
		//The RequestComments don't have the bbBody field
		if (comment.getBBbody() != null){
			holder.commentText.setText(whatBBParser.parsebb(comment.getBBbody()));
		}
		else {
			holder.commentText.setText(Html.fromHtml(comment.getBody(), imageGetter, new HTMLListTagHandler()));
		}
		holder.commentText.setMovementMethod(LinkMovementMethod.getInstance());

		holder.image.setOnClickListener(holder.userClickListener);
		String imgUrl = comment.getAvatar();
		if (imagesEnabled){
			if (imgUrl != null && !imgUrl.isEmpty() && !imageFailTracker.failed(imgUrl)){
				ImageLoader.getInstance().displayImage(imgUrl, holder.image, holder.listener);
			}
			else {
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.no_avatar, holder.image, holder.listener);
			}
		}
		else {
			holder.image.setVisibility(View.GONE);
			holder.spinner.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView username, postDate, commentText;
		public View artContainer;
		public ImageView image;
		public ProgressBar spinner;
		public ImageLoadingListener listener;
		public UserClickListener userClickListener;
	}

	private class UserClickListener implements View.OnClickListener {
		public int userId;

		@Override
		public void onClick(View v){
			viewUser.viewUser(userId);
		}
	}
}
