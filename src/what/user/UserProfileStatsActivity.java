package what.user;

import java.util.LinkedList;

import what.gui.MyActivity;
import what.gui.R;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.user.User;

/**
 * 
 *
 */
public class UserProfileStatsActivity extends MyActivity {
	private TextView username;
	private LinkedList<TextView> detailList = new LinkedList<TextView>();
	private LinearLayout scrollLayout;
	private int counter;
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.generic_list, false);
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);
		username = (TextView) this.findViewById(R.id.title);
		user = UserProfileTabActivity.getUser();
		populateLayout();
	}

	private void populateLayout() {
		username.setText(user.getProfile().getUsername());

	}

	private void addToList(String s) {
		if ((counter % 2) == 0) {
			detailList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
		} else {
			detailList.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
		}
		detailList.getLast().setText(s);
		scrollLayout.addView(detailList.getLast());
		counter++;
	}

	private void addToListSection(String s) {
		detailList.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		detailList.getLast().setText(s);
		scrollLayout.addView(detailList.getLast());
		counter++;
	}
}