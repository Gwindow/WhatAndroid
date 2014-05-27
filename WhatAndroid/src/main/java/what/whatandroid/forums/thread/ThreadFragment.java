package what.whatandroid.forums.thread;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import api.forum.thread.ForumThread;
import api.soup.MySoup;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.callbacks.OnLoggedInCallback;
import what.whatandroid.callbacks.SetTitleCallback;
import what.whatandroid.forums.ForumActivity;

/**
 * Displays a paged view of posts in a thread
 */
public class ThreadFragment extends Fragment implements OnLoggedInCallback, LoadingListener<ForumThread> {
	private static final String THREAD_NAME = "what.whatandroid.forums.THREAD_NAME",
		PAGES = "what.whatandroid.forums.PAGES";

	private SetTitleCallback setTitle;
	private ThreadPagerAdapter pagerAdapter;
	private int pages, thread;
	private String threadName;

	/**
	 * Create a new thread fragment showing the thread at some page
	 *
	 * @param thread thread id to view
	 * @param page   page to view
	 */
	public static ThreadFragment newInstance(int thread, int page){
		ThreadFragment f = new ThreadFragment();
		Bundle args = new Bundle();
		args.putInt(ForumActivity.THREAD_ID, thread);
		//Ignored for now but we should jump to this page
		args.putInt(ForumActivity.PAGE, page);
		f.setArguments(args);
		return f;
	}

	public ThreadFragment(){
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
		thread = getArguments().getInt(ForumActivity.THREAD_ID);
		if (savedInstanceState != null){
			pages = savedInstanceState.getInt(PAGES);
			threadName = savedInstanceState.getString(THREAD_NAME);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		if (threadName != null){
			setTitle.setTitle(threadName);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_view_pager_strip, container, false);
		ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
		pagerAdapter = new ThreadPagerAdapter(getChildFragmentManager(), pages, thread);
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
		if (threadName != null){
			outState.putString(THREAD_NAME, threadName);
		}
		else {
			outState.putString(THREAD_NAME, "Thread");
		}
	}

	@Override
	public void onLoggedIn(){
		pagerAdapter.onLoggedIn();
	}

	@Override
	public void onLoadingComplete(ForumThread data){
		threadName = data.getResponse().getThreadTitle();
		pages = data.getPages();
		setTitle.setTitle(threadName);
	}
}
