package what.whatandroid.forums.forum;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import api.forum.forum.ForumThread;
import what.whatandroid.R;

/**
 * Adapter for showing a listing of forum threads,
 * light version with just name and icons
 */
public class ForumListAdapterLight extends ForumListAdapter {

    public ForumListAdapterLight(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if (convertView != null){
            holder = (ViewHolder)convertView.getTag();
        }
        else {
            convertView = inflater.inflate(R.layout.list_forum_thread_light, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.thread_name);
            holder.jumpToLastRead = (ImageButton)convertView.findViewById(R.id.go_to_last_read);
            holder.jumpToLastRead.setImageResource(R.drawable.ic_forward_24dp);
            holder.sticky = (ImageView) convertView.findViewById(R.id.sticky_thread);
            holder.jumpToLastRead.setImageResource(R.drawable.ic_star_24dp);
            holder.locked = (ImageView) convertView.findViewById(R.id.locked_thread);
            holder.jumpToLastRead.setImageResource(R.drawable.ic_lock_24dp);
            convertView.setTag(holder);
        }

        ForumThread thread = getItem(position);
        holder.name.setText(thread.getTitle());
        holder.jumpToLastRead.setTag(position);
        holder.jumpToLastRead.setOnClickListener(this);

        if (thread.isRead()){
            holder.name.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
        }
        else {
            holder.name.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
        }

        if (thread.isSticky()){
            holder.sticky.setVisibility(View.VISIBLE);
        }
        else {
            holder.sticky.setVisibility(View.GONE);
        }
        if (thread.isLocked()){
            holder.locked.setVisibility(View.VISIBLE);
        }
        else {
            holder.locked.setVisibility(View.GONE);
        }
        return convertView;
    }
}
