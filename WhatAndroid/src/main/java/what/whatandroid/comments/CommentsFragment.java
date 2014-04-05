package what.whatandroid.comments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import api.comments.SimpleComment;
import what.whatandroid.R;

import java.util.List;

/**
 * A fragment for displaying a listing of user comments
 */
public class CommentsFragment extends Fragment {
	private List<? extends SimpleComment> comments;
	private CommentsAdapter adapter;

	public static CommentsFragment newInstance(List<? extends SimpleComment> comments){
		CommentsFragment f = new CommentsFragment();
		f.comments = comments;
		return f;
	}

	public CommentsFragment(){
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_list_view, container, false);
		ListView list = (ListView)view.findViewById(R.id.list);
		adapter = new CommentsAdapter(getActivity(), comments);
		list.setAdapter(adapter);
		return view;
	}
}
