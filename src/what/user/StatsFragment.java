package what.user;

import what.gui.MyActivity2;
import what.gui.MyScrollView;
import what.gui.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import api.cli.Utils;
import api.user.Profile;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Gwindow
 * @since Jun 3, 2012 11:48:38 AM
 */
public class StatsFragment extends SherlockFragment {

	private LinearLayout scrollLayout;
	private Profile profile;
	private MyScrollView scrollView;

	public StatsFragment() {
		super();
	}

	public StatsFragment(Profile profile) {
		this.profile = profile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.generic_endless_scrollview, container, false);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populateStats(view);

		return view;
	}

	// TODO secondary classes
	private void populateStats(View view) {
		addToListSection("Stats");
		addToList("Class", profile.getPersonal().getUserClass());
		addToList("Joined", profile.getStats().getJoinedDate());
		addToList("Last Seen", profile.getStats().getLastAccess());
		if (profile.getStats().getUploaded() != null) {
			addToList("Uploaded", Utils.toHumanReadableSize(profile.getStats().getUploaded().longValue()));
		}
		if (profile.getStats().getDownloaded() != null) {
			addToList("Downloaded", Utils.toHumanReadableSize(profile.getStats().getDownloaded().longValue()));
		}
		addToList("Ratio", profile.getStats().getRatio());
		addToList("Required Ratio", profile.getStats().getRequiredRatio());

		addToListSection("Percentile Rankings");
		addToList("Data Uploaded", profile.getRanks().getUploaded());
		addToList("Data Downloaded", profile.getRanks().getDownloaded());
		addToList("Requests Filled", profile.getRanks().getRequests());
		addToList("Bounty Spent", profile.getRanks().getBounty());
		addToList("Posts Made", profile.getRanks().getPosts());
		addToList("Artists Added", profile.getRanks().getArtists());
		addToList("Overall Rank", profile.getRanks().getOverall());

		addToListSection("Personal");
		addToList("Paranoia", profile.getPersonal().getParanoiaText());

		addToListSection("Community");
		addToList("Forum Posts", profile.getCommunity().getPosts());
		addToList("Torrent Comments ", profile.getCommunity().getTorrentComments());
		addToList("Collages Started", profile.getCommunity().getCollagesStarted());
		addToList("Collages Contributed To ", profile.getCommunity().getCollagesContrib());
		addToList("Requests Filled", profile.getCommunity().getRequestsFilled());
		addToList("Requests Voted", profile.getCommunity().getRequestsVoted());
		addToList("Uploaded", profile.getCommunity().getUploaded());
		addToList("Unique Groups", profile.getCommunity().getGroups());
		addToList("Perfect Flacs", profile.getCommunity().getPerfectFlacs());
		addToList("Seeding", profile.getCommunity().getSeeding());
		addToList("Leeching", profile.getCommunity().getLeeching());
		addToList("Snatched", profile.getCommunity().getSnatched());
		addToList("Invited", profile.getCommunity().getInvited());
	}

	private void addToList(String type, Object value) {
		TextView title = (TextView) View.inflate(getSherlockActivity(), R.layout.user_stats_title, null);
		if (value != null) {
			String displayed_string = type + ": " + value;
			title.setText(displayed_string);
			scrollLayout.addView(title);
		}
	}

	private void addToListSection(String s) {
		TextView header = (TextView) View.inflate(getSherlockActivity(), R.layout.user_stats_header, null);
		header.setText(s);
		scrollLayout.addView(header);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			return ((MyActivity2) getSherlockActivity()).homeIconJump(scrollView);
		}
		return super.onOptionsItemSelected(item);
	}
}
