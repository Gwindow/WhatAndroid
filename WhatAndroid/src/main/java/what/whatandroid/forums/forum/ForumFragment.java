package what.whatandroid.forums.forum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.forum.forum.Forum;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.forums.ForumActivity;

/**
 * Displays a paged view of the posts in some forum
 */
public class ForumFragment extends Fragment implements OnLoggedInCallback, LoadingListener<Forum> {
	private static final String FORUM_NAME = "what.whatandroid.forums.FORUM_NAME",
		PAGES = "what.whatandroid.forums.PAGES";

	private SetTitleCallback setTitle;
	private ForumPagerAdapter pagerAdapter;
	private int pages, forum;
	private String forumName;

	/**
	 * Create a new forum fragment displaying the forum at some page
	 *
	 * @param forum forum id to display
	 * @param page  page to show
	 */
	public static ForumFragment newInstance(int forum, int page){
		ForumFragment f = new ForumFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.FORUM_ID, forum);
		//Ignored for now, but will we need this? skipping to forum pages and such
		args.putInt(ForumActivity.PAGE, page);
		f.setArguments(args);
		return f;
	}

	public ForumFragment(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			setTitle = (SetTitleCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement SetTitleCallback");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		pages = 1;
		forum = getArguments().getInt(ForumActivity.FORUM_ID);
		if (savedInstanceState != null){
			pages = savedInstanceState.getInt(PAGES);
			forumName = savedInstanceState.getString(FORUM_NAME);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		if (forumName != null){
			setTitle.setTitle(forumName);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		pagerAdapter = new ForumPagerAdapter(getChildFragmentManager(), pages, forum);
		viewPager.setAdapter(pagerAdapter);
		pagerAdapter.setLoadingListener(this);
		if (MySoup.isLoggedIn()){
			onLoggedIn();
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(PAGES, pagerAdapter.getCount());
		if (forumName != null){
			outState.putString(FORUM_NAME, forumName);
		}
		else {
			outState.putString(FORUM_NAME, "Forums");
		}
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}

	@Override
	public void onLoadingComplete(Forum data){
		forumName = data.getForumName();
		setTitle.setTitle(forumName);
	}
}
