package what.whatandroid.forums.forum;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import api.forum.forum.Forum;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.forums.ForumActivity;
import what.whatandroid.forums.NumberPickerDialog;

/**
 * Displays a paged view of the posts in some forum
 */
public class ForumFragment extends Fragment implements OnLoggedInCallback, LoadingListener<Forum>,
	NumberPickerDialog.NumberPickerListener {

	private static final String FORUM_NAME = "what.whatandroid.forums.FORUM_NAME",
		PAGES = "what.whatandroid.forums.PAGES";

	private SetTitleCallback setTitle;
	private ViewPager viewPager;
	private ForumPagerAdapter pagerAdapter;
	private int pages, forum;
	private String forumName;

	/**
	 * Create a new forum fragment displaying the forum at some page
	 *
	 * @param forum forum id to display
	 */
	public static ForumFragment newInstance(int forum){
		ForumFragment f = new ForumFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.FORUM_ID, forum);
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
		setHasOptionsMenu(true);
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
		viewPager = (ViewPager)view.findViewById(R.id.pager);
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
		outState.putInt(PAGES, pages);
		if (forumName != null){
			outState.putString(FORUM_NAME, forumName);
		}
		else {
			outState.putString(FORUM_NAME, "Forums");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.forum, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.action_pick_page:
				NumberPickerDialog dialog = NumberPickerDialog.newInstance("Select Page", 1, pages);
				dialog.setTargetFragment(this, 0);
				dialog.show(getFragmentManager(), "dialog");
				return true;
			case R.id.action_pick_last_page:
				viewPager.setCurrentItem(pages - 1);
				return true;
			default:
				break;
		}
		return false;
	}

	@Override
	public void pickNumber(int number){
		viewPager.setCurrentItem(number - 1);
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}

	@Override
	public void onLoadingComplete(Forum data){
		forumName = data.getForumName();
		pages = pagerAdapter.getCount();
		setTitle.setTitle(forumName);
	}
}
