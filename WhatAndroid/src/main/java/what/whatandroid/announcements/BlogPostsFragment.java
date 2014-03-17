package what.whatandroid.announcements;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import api.announcements.BlogPost;
import what.whatandroid.R;

import java.util.List;

/**
 * BlogPostsFragment displays a list of the blog posts and snippets of the body text
 * re-uses the layouts from the Announcements
 */
public class BlogPostsFragment extends ListFragment {
	private List<BlogPost> posts;
	private BlogPostsAdapter adapter;

    /**
     * Use this factory method to create a new instance of the fragment displaying the list of posts
     * @param posts The blog posts to display
     * @return A BlogPostsFragment displaying a list of the blog posts passed
     */
    public static BlogPostsFragment newInstance(List<BlogPost> posts) {
        BlogPostsFragment fragment = new BlogPostsFragment();
		fragment.posts = posts;
        return fragment;
    }
    public BlogPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		adapter = new BlogPostsAdapter(getActivity(), R.layout.list_announcement, posts);
		setListAdapter(adapter);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		getListView().setOnItemClickListener(adapter);
	}
}
