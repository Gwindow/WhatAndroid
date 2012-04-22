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

		addToListSection("Stats");
		addToList("Class : " + user.getProfile().getPersonal().getUserClass());
		addToList("Joined: " + user.getProfile().getStats().getJoinedDate());
		addToList("Last Seen: " + user.getProfile().getStats().getLastAccess());
		addToList("Uploaded: " + toGBString(user.getProfile().getStats().getUploaded().toString()) + " GB");
		addToList("Downloaded: " + toGBString(user.getProfile().getStats().getDownloaded().toString()) + " GB");
		addToList("Ratio: " + user.getProfile().getStats().getRatio().toString());
		addToList("Required Ratio: " + user.getProfile().getStats().getRequiredRatio().toString());

		addToListSection("Percentile Rankings");
		addToList("Data Uploaded: " + user.getProfile().getRanks().getUploaded());
		addToList("Data Downloaded: " + user.getProfile().getRanks().getDownloaded());
		addToList("Requests Filled: " + user.getProfile().getRanks().getRequests());
		addToList("Bounty Spent: " + user.getProfile().getRanks().getBounty());
		addToList("Posts Made: " + user.getProfile().getRanks().getPosts());
		addToList("Artists Added: " + user.getProfile().getRanks().getArtists());
		addToList("Overall Rank: " + user.getProfile().getRanks().getOverall());

		addToListSection("Personal");
		addToList("Class: " + user.getProfile().getPersonal().getParanoiaText());

		addToListSection("Community");
		addToList("Forum Posts: " + user.getProfile().getCommunity().getPosts());
		addToList("Torrent Comments : " + user.getProfile().getCommunity().getTorrentComments());
		addToList("Collages Started: " + user.getProfile().getCommunity().getCollagesStarted());
		addToList("Collages Contributed To : " + user.getProfile().getCommunity().getCollagesContrib());
		addToList("Requests Filled: " + user.getProfile().getCommunity().getRequestsFilled());
		addToList("Requests Voted: " + user.getProfile().getCommunity().getRequestsVoted());
		addToList("Uploaded: " + user.getProfile().getCommunity().getUploaded());
		addToList("Unique Groups: " + user.getProfile().getCommunity().getGroups());
		// TODO add to json
		// addToList("Perfect Flacs: " + user.getProfile().getCommunity());
		addToList("Seeding: " + user.getProfile().getCommunity().getSeeding());
		addToList("Leeching: " + user.getProfile().getCommunity().getLeeching());
		addToList("Snatched: " + user.getProfile().getCommunity().getSnatched());
		addToList("Invited: " + user.getProfile().getCommunity().getInvited());

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