package what.torrents.torrents;

import java.util.HashMap;
import java.util.LinkedList;

import what.forum.QuoteBuffer;
import what.forum.ThreadActivity;
import what.gui.AsyncImageGetter;
import what.gui.BundleKeys;
import what.gui.Cancelable;
import what.gui.ErrorToast;
import what.gui.LoadAvatar;
import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import what.gui.ReplyActivity;
import what.gui.Scrollable;
import what.settings.Settings;
import what.user.UserActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.comments.Comments;
import api.comments.Response;
import api.comments.TorrentComments;

import com.actionbarsherlock.app.SherlockFragment;

public class CommentsFragment extends SherlockFragment implements OnClickListener, Scrollable {
	private static final int REPLY_TAG = 0;
	private static final int QUOTE_TAG = 1;
	private static final int USER_TAG = 2;

	private Response response;
	private int groupId, threadPage, width, height;
	private boolean isLoaded = true;
	private MyActivity2 mCtx;

	private LinkedList<Comments> comments;
	private HashMap<Integer, ImageView> avatarMap;

	private LinearLayout scrollLayout;
	private MyScrollView scrollView;

	public CommentsFragment(int groupId, MyActivity2 ctx) {
		this.groupId = groupId;
		this.mCtx = ctx;

		comments = new LinkedList<Comments>();
		avatarMap = new HashMap<Integer, ImageView>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollView.attachScrollable(this);

		TorrentComments comments = TorrentComments.fromIdAndPage(groupId, 1);
		if (comments.getStatus()) {
			response = comments.getResponse();
			populateComments();
		}

		return view;
	}

	public void populateComments() {
		threadPage = response.getPage().intValue();

		if (response.getComments() != null) {
			for (Comments comment : response.getComments()) {
				LinearLayout post_layout = (LinearLayout) LayoutInflater.from(mCtx).inflate(R.layout.thread_post, null);
				comments.add(comment);

				ImageView avatar = (ImageView) post_layout.findViewById(R.id.avatar);
				if (Settings.getAvatarsEnabled()) {
					avatarMap.put(comments.getLast().getPostId().intValue(), avatar);
					LoadAvatar lAv = new LoadAvatar(mCtx, avatar, comments.getLast().getUserinfo().getAuthorId().intValue(), comments.getLast().getUserinfo()
							.getAvatar());
					mCtx.attachCancelable(lAv);
					lAv.execute();
				} else {
					avatar.setVisibility(View.GONE);
				}

				TextView author = (TextView) post_layout.findViewById(R.id.author);
				author.setText(comments.getLast().getUserinfo().getAuthorName());
				author.setId(comments.getLast().getUserinfo().getAuthorId().intValue());
				author.setTag(USER_TAG);
				author.setOnClickListener(this);

				TextView date = (TextView) post_layout.findViewById(R.id.date);
				date.setText(comments.getLast().getAddedTime());

				TextView body = (TextView) post_layout.findViewById(R.id.body);
				body.setText(Html.fromHtml(comments.getLast().getBody(), new AsyncImageGetter(body, mCtx, width, height), null));
				Linkify.addLinks(body, Linkify.WEB_URLS);

				ImageView reply = (ImageView) post_layout.findViewById(R.id.replyIcon);
				reply.setTag(REPLY_TAG);
				reply.setId(comments.size() - 1);
				reply.setOnClickListener(this);

				ImageView quote = (ImageView) post_layout.findViewById(R.id.quoteIcon);
				quote.setTag(QUOTE_TAG);
				quote.setId(comments.size() - 1);
				quote.setOnClickListener(this);

				ImageView user = (ImageView) post_layout.findViewById(R.id.userIcon);
				user.setTag(USER_TAG);
				user.setId(comments.getLast().getUserinfo().getAuthorId().intValue());
				user.setOnClickListener(this);

				scrollLayout.addView(post_layout);
			}
		}
	}

	private void reply() {
		Intent intent = new Intent(mCtx, ReplyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(BundleKeys.REPLY_TYPE, BundleKeys.REPLY_TYPE_COMMENT);
		bundle.putInt(BundleKeys.GROUP_ID, groupId);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void openUser(int id) {
		Intent intent = new Intent(mCtx, UserActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BundleKeys.USER_ID, id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void onClick(View v) {
		switch (Integer.valueOf(v.getTag().toString())) {
		case REPLY_TAG:
			QuoteBuffer.add(groupId, comments.get(v.getId()).getBbBody());
			reply();
			break;
		case QUOTE_TAG:
			QuoteBuffer.add(groupId, comments.get(v.getId()).getBbBody());
			Toast.makeText(mCtx, "Quoted", Toast.LENGTH_SHORT).show();
			break;
		case USER_TAG:
			openUser(v.getId());
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scrolledToBottom() {
		nextPage();
	}

	/**
	 * Load the next page while currentPage < totalPages.
	 */
	private void nextPage() {
		if (isLoaded) {
			if (threadPage < response.getPages().intValue()) {
				threadPage++;
				new Load(true).execute();
			}
		}
	}

	private class Load extends AsyncTask<Void, Void, Boolean> implements Cancelable {
		private ProgressBar bar;
		private boolean useEmbeddedDialog;

		public Load() {
			this(false);
		}

		public Load(boolean useEmbeddedDialog) {
			this.useEmbeddedDialog = useEmbeddedDialog;
			mCtx.attachCancelable(this);
		}

		@Override
		public void cancel() {
			Log.d("cancel", "cancelled thread");
			super.cancel(true);
		}

		@Override
		protected void onPreExecute() {
			isLoaded = false;
			if (useEmbeddedDialog) {
				bar = new ProgressBar(mCtx);
				bar.setIndeterminate(true);
				scrollLayout.addView(bar);
			} else {
				mCtx.lockScreenRotation();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			TorrentComments cmt = TorrentComments.fromIdAndPage(groupId, threadPage);
			response = cmt.getResponse();

			return cmt.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			isLoaded = true;
			if (useEmbeddedDialog) {
				hideProgressBar();
			} else {
				mCtx.hideIndeterminateProgress();
				mCtx.unlockScreenRotation();
			}

			if (status) {
				populateComments();
			} else {
				ErrorToast.show(mCtx, ThreadActivity.class);
			}
		}

		private void hideProgressBar() {
			scrollLayout.removeViewAt(scrollLayout.getChildCount() - 1);
		}
	}
}