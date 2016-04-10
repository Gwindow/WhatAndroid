package what.whatandroid.comments;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import api.comments.SimpleComment;
import what.whatandroid.R;
import what.whatandroid.WhatApplication;
import what.whatandroid.callbacks.AddQuoteCallback;
import what.whatandroid.callbacks.ViewUserCallbacks;
import what.whatandroid.imgloader.HtmlImageHider;
import what.whatandroid.imgloader.ImageLoadFailTracker;
import what.whatandroid.settings.SettingsActivity;

import java.util.Date;

/**
 * Adapter for displaying a list of user comments
 */
public class CommentsAdapter extends ArrayAdapter<SimpleComment> implements View.OnClickListener {
	private final LayoutInflater inflater;
	/**
	 * Used to hide HTML images by simply returning transparent drawables
	 */
	private final HtmlImageHider imageGetter;
	/**
	 * Tracks which images failed to load so that we can skip trying to reload
	 * them and just display the no-avatar icon
	 */
	private ImageLoadFailTracker imageFailTracker;
	private boolean imagesEnabled;
	/**
	 * Callbacks to view a user when avatar/header is clicked
	 */
	private ViewUserCallbacks viewUser;
	/**
	 * Callback to notify that the user wants to quote some text
	 * This is optional until posting is added to the other areas we show comments
	 */
	private AddQuoteCallback addQuote;
	/**
	 * Instance of the comment BBcode parser and formatter
	 */
	private WhatBBParser whatBBParser;

    private boolean isQuoteVisible = true;

    public CommentsAdapter(Context context){
		super(context, R.layout.list_user_comment);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageGetter = new HtmlImageHider(context);
		imageFailTracker = new ImageLoadFailTracker();
		imagesEnabled = SettingsActivity.imagesEnabled(context);
		whatBBParser = new WhatBBParser();
		try {
			viewUser = (ViewUserCallbacks)context;
		}
		catch (ClassCastException e){
			throw new ClassCastException(context.toString() + " must implement ViewUserCallbacks");
		}
		//TODO: No error for now as replying and quoting is only in the forums at the moment
		try {
			addQuote = (AddQuoteCallback) context;
		}
		catch (ClassCastException e){
			//Not an error at the moment
		}
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
			holder.userClickListener = new UserClickListener();
			holder.quote = (ImageButton) convertView.findViewById(R.id.reply_quote);
			holder.quote.setImageResource(R.drawable.ic_reply_24dp);

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
		if (!imagesEnabled) {
			holder.artContainer.setVisibility(View.GONE);
		} else {
			holder.artContainer.setVisibility(View.VISIBLE);
			WhatApplication.loadImage(getContext(), imgUrl, holder.image, holder.spinner, imageFailTracker, R.drawable.no_avatar);
		}
		if (addQuote != null){
			holder.quote.setVisibility(View.VISIBLE);
			holder.quote.setTag(position);
			holder.quote.setOnClickListener(this);
		}
        if(!isQuoteVisible){
            holder.quote.setVisibility(View.GONE);
        }
		return convertView;
	}

	@Override
	public void onClick(View v){
		if (v.getId() == R.id.reply_quote && addQuote != null){
			addQuote.quote(getItem((Integer) v.getTag()).getQuote());
		}
	}

    public boolean isQuoteVisible() {
        return isQuoteVisible;
    }

    public void setQuoteVisible(boolean isQuoteVisible) {
        this.isQuoteVisible = isQuoteVisible;
    }

	private static class ViewHolder {
		public TextView username, postDate, commentText;
		public View artContainer;
		public ImageView image;
		public ProgressBar spinner;
		public UserClickListener userClickListener;
		public ImageButton quote;
	}

	private class UserClickListener implements View.OnClickListener {
		public int userId;

		@Override
		public void onClick(View v){
			viewUser.viewUser(userId);
		}
	}

}
