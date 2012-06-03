package what.user;

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

/**
 * @author Gwindow
 * @since Jun 3, 2012 11:48:38 AM
 */
public class StatsFragment extends SherlockFragment {

	private LinearLayout scrollLayout;
	private Profile profile;

	public StatsFragment(Profile profile) {
		this.profile = profile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.generic_scrollview, container, false);
		scrollLayout = (LinearLayout) view.findViewById(R.id.scrollLayout);
		populateStats(view, inflater);

		return view;
	}

	// TODO secondary classes
	// TODO fix nulls
	private void populateStats(View view, LayoutInflater inflater) {
		addToListSection("Stats", inflater);
		addToList("Class : " + profile.getPersonal().getUserClass(), inflater);
		addToList("Joined: " + profile.getStats().getJoinedDate(), inflater);
		addToList("Last Seen: " + profile.getStats().getLastAccess(), inflater);
		if (profile.getStats().getUploaded() != null) {
			addToList("Uploaded: " + Utils.toHumanReadableSize(profile.getStats().getUploaded().longValue()), inflater);
		}
		if (profile.getStats().getDownloaded() != null) {
			addToList("Downloaded: " + Utils.toHumanReadableSize(profile.getStats().getDownloaded().longValue()), inflater);
		}
		addToList("Ratio: " + profile.getStats().getRatio(), inflater);
		addToList("Required Ratio: " + profile.getStats().getRequiredRatio(), inflater);

		addToListSection("Percentile Rankings", inflater);
		addToList("Data Uploaded: " + profile.getRanks().getUploaded(), inflater);
		addToList("Data Downloaded: " + profile.getRanks().getDownloaded(), inflater);
		addToList("Requests Filled: " + profile.getRanks().getRequests(), inflater);
		addToList("Bounty Spent: " + profile.getRanks().getBounty(), inflater);
		addToList("Posts Made: " + profile.getRanks().getPosts(), inflater);
		addToList("Artists Added: " + profile.getRanks().getArtists(), inflater);
		addToList("Overall Rank: " + profile.getRanks().getOverall(), inflater);

		addToListSection("Personal", inflater);
		addToList("Paranoia: " + profile.getPersonal().getParanoiaText(), inflater);

		addToListSection("Community", inflater);
		addToList("Forum Posts: " + profile.getCommunity().getPosts(), inflater);
		addToList("Torrent Comments : " + profile.getCommunity().getTorrentComments(), inflater);
		addToList("Collages Started: " + profile.getCommunity().getCollagesStarted(), inflater);
		addToList("Collages Contributed To : " + profile.getCommunity().getCollagesContrib(), inflater);
		addToList("Requests Filled: " + profile.getCommunity().getRequestsFilled(), inflater);
		addToList("Requests Voted: " + profile.getCommunity().getRequestsVoted(), inflater);
		addToList("Uploaded: " + profile.getCommunity().getUploaded(), inflater);
		addToList("Unique Groups: " + profile.getCommunity().getGroups(), inflater);
		addToList("Perfect Flacs: " + profile.getCommunity().getPerfectFlacs(), inflater);
		addToList("Seeding: " + profile.getCommunity().getSeeding(), inflater);
		addToList("Leeching: " + profile.getCommunity().getLeeching(), inflater);
		addToList("Snatched: " + profile.getCommunity().getSnatched(), inflater);
		addToList("Invited: " + profile.getCommunity().getInvited(), inflater);
	}

	private void addToList(String s, LayoutInflater inflater) {
		try {
			TextView title = (TextView) inflater.inflate(R.layout.user_stats_title, null);
			title.setText(s);
			scrollLayout.addView(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addToListSection(String s, LayoutInflater inflater) {
		TextView header = (TextView) inflater.inflate(R.layout.user_stats_header, null);
		header.setText(s);
		scrollLayout.addView(header);
	}

}
