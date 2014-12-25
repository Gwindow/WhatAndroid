package what.whatandroid.torrentgroup.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import api.torrents.torrents.Group;
import what.whatandroid.R;
import what.whatandroid.callbacks.LoadingListener;
import what.whatandroid.comments.HTMLListTagHandler;
import what.whatandroid.imgloader.HtmlImageHider;

/**
 * Fragment for holding the description of a torrent (or request)
 */
public class DescriptionFragment extends Fragment implements LoadingListener<Group> {
    private TextView descriptionView, tagsView;
    private Group group;

	public DescriptionFragment() {
		//Required empty ctor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description_tags, container, false);
        descriptionView = (TextView) view.findViewById(R.id.description);
        tagsView = (TextView) view.findViewById(R.id.tags);
        descriptionView.setMovementMethod(LinkMovementMethod.getInstance());
        if (group != null) {
            descriptionView.setText(Html.fromHtml(group.getWikiBody(), new HtmlImageHider(getActivity()), new HTMLListTagHandler()));
            String tagList = group.getTags().toString();
            tagList = tagList.substring(tagList.indexOf('[') + 1, tagList.lastIndexOf(']'));
            tagsView.setText(tagList);
        }
		return view;
	}

	@Override
    public void onLoadingComplete(Group data) {
        group = data;
        if (isAdded()) {
            descriptionView.setText(Html.fromHtml(group.getWikiBody(), new HtmlImageHider(getActivity()), new HTMLListTagHandler()));
            String tagList = group.getTags().toString();
            tagList = tagList.substring(tagList.indexOf('[') + 1, tagList.lastIndexOf(']'));
            tagsView.setText(tagList);
        }
	}
}
