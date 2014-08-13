package what.whatandroid.forums.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import api.forum.forum.ForumThread;
import what.whatandroid.R;
import what.whatandroid.callbacks.ViewForumCallbacks;


/**
 * Adapter for showing a listing of forum threads
 */
public class ForumListAdapter extends ArrayAdapter<ForumThread> implements AdapterView.OnItemClickListener,
        View.OnClickListener  {
    protected final LayoutInflater inflater;
    protected ViewForumCallbacks viewForum;

    public ForumListAdapter(Context context){
        super(context, R.layout.list_torrent_file);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            viewForum = (ViewForumCallbacks)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement ViewForumCallbacks");
        }
    }

    /**
     * This listener handles clicks on the image buttons to jump to the last read post
     */
    @Override
    public void onClick(View v){
        Integer position = (Integer)v.getTag();
        ForumThread thread = getItem(position);
        viewForum.viewThread(thread.getTopicId().intValue(), thread.getLastReadPostId().intValue());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        viewForum.viewThread(getItem(position).getTopicId().intValue());
    }

    protected static class ViewHolder {
        public TextView name, replies, author, lastPostAuthor, lastPostTime;
        public ImageView sticky, locked;
        public ImageButton jumpToLastRead;
    }
}
