package what.whatandroid.announcements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import api.announcements.BlogPost;
import what.whatandroid.R;

/**
 * Displays a detailed view of a single blog post
 */
public class BlogPostFragment extends Fragment {
	private BlogPost post;

	/**
	 * Use this factory method to create a new instance of the fragment to display the blog post
	 * @param post the blog post to display
	 * @return A BlogPostFragment displaying the post
	 */
    public static BlogPostFragment newInstance(BlogPost post) {
        BlogPostFragment fragment = new BlogPostFragment();
		fragment.post = post;
        return fragment;
    }

	public BlogPostFragment() {
        // Required empty public constructor
    }

	/**
	 * Get the post being displayed
	 * @return the displayed post
	 */
	public BlogPost getPost(){
		return post;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);
		TextView title = (TextView)view.findViewById(R.id.announcement_title);
		TextView date = (TextView)view.findViewById(R.id.announcement_date);
		TextView body = (TextView)view.findViewById(R.id.announcement_body);
		title.setText(post.getTitle());
		date.setText("By: " + post.getAuthor() + " on: " + post.getBlogTime());
		body.setText(post.getBody());
		return view;
    }


}
