package what.top;

import java.util.ArrayList;
import java.util.HashMap;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import api.top.Top;

public class TopTorrentsActivity extends MyActivity implements OnClickListener {
	private LinearLayout scrollLayout;
	private ProgressDialog dialog;
	private Intent intent;
	private Top top;
	private ArrayList<TextView> sectionTitle;
	private ArrayList<TextView> pastDay;
	private ArrayList<TextView> pastWeek;
	private ArrayList<TextView> allTime;
	private ArrayList<TextView> snatched;
	private ArrayList<TextView> transferred;
	private ArrayList<TextView> seeded;
	private HashMap<Integer, Integer> idMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.toptorrents, true);
	}

	@Override
	public void init() {
		sectionTitle = new ArrayList<TextView>();
		pastDay = new ArrayList<TextView>();
		pastWeek = new ArrayList<TextView>();
		allTime = new ArrayList<TextView>();
		snatched = new ArrayList<TextView>();
		transferred = new ArrayList<TextView>();
		seeded = new ArrayList<TextView>();
		idMap = new HashMap<Integer, Integer>();
	}

	@Override
	public void load() {
		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

	}

	@Override
	public void prepare() {
		new LoadTopTorrents().execute();
	}

	private void populateLayout() {
		int counter = 0;
		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(0).setText("Most Active Torrents Uploaded in the Past Day");
		sectionTitle.get(0).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(0));
		for (int i = 0; i < top.getResponse().get(0).getResults().size(); i++) {
			if ((i % 2) == 0) {
				pastDay.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				pastDay.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			pastDay.get(i).setText(getTitle(0, i));
			pastDay.get(i).setOnClickListener(this);
			pastDay.get(i).setId(counter);
			idMap.put(counter, getId(0, i));
			counter++;
			scrollLayout.addView(pastDay.get(i));
		}

		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(1).setText("Most Active Torrents Uploaded in the Past Week");
		sectionTitle.get(1).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(1));
		for (int i = 0; i < top.getResponse().get(1).getResults().size(); i++) {
			if ((i % 2) == 0) {
				pastWeek.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				pastWeek.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			pastWeek.get(i).setText(getTitle(1, i));
			pastWeek.get(i).setOnClickListener(this);
			pastWeek.get(i).setId(counter);
			idMap.put(counter, getId(1, i));
			counter++;
			scrollLayout.addView(pastWeek.get(i));
		}

		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(2).setText("Most Active Torrents of All Time");
		sectionTitle.get(2).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(2));
		for (int i = 0; i < top.getResponse().get(2).getResults().size(); i++) {
			if ((i % 2) == 0) {
				allTime.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				allTime.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			allTime.get(i).setText(getTitle(2, i));
			allTime.get(i).setOnClickListener(this);
			allTime.get(i).setId(counter);
			idMap.put(counter, getId(2, i));
			counter++;
			scrollLayout.addView(allTime.get(i));
		}

		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(3).setText("Most Snatched Torrents");
		sectionTitle.get(3).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(3));
		for (int i = 0; i < top.getResponse().get(3).getResults().size(); i++) {
			if ((i % 2) == 0) {
				snatched.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				snatched.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			snatched.get(i).setText(getTitle(3, i));
			snatched.get(i).setOnClickListener(this);
			snatched.get(i).setId(counter);
			idMap.put(counter, getId(3, i));
			counter++;
			scrollLayout.addView(snatched.get(i));
		}

		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(4).setText("Most Data Transferred Torrents");
		sectionTitle.get(4).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(4));
		for (int i = 0; i < top.getResponse().get(4).getResults().size(); i++) {
			if ((i % 2) == 0) {
				transferred.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				transferred.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			transferred.get(i).setText(getTitle(4, i));
			transferred.get(i).setOnClickListener(this);
			transferred.get(i).setId(counter);
			idMap.put(counter, getId(4, i));
			counter++;
			scrollLayout.addView(transferred.get(i));
		}

		sectionTitle.add((TextView) getLayoutInflater().inflate(R.layout.forum_section_title, null));
		sectionTitle.get(5).setText("Best Seeded Torrents");
		sectionTitle.get(5).setTextSize(15);
		scrollLayout.addView(sectionTitle.get(5));
		for (int i = 0; i < top.getResponse().get(5).getResults().size(); i++) {
			if ((i % 2) == 0) {
				seeded.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_even, null));
			} else {
				seeded.add((TextView) getLayoutInflater().inflate(R.layout.torrent_name_odd, null));
			}
			seeded.get(i).setText(getTitle(5, i));
			seeded.get(i).setOnClickListener(this);
			seeded.get(i).setId(counter);
			idMap.put(counter, getId(5, i));
			counter++;
			scrollLayout.addView(seeded.get(i));
		}
	}

	private String getTitle(int j, int i) {
		String artist = top.getResponse().get(j).getResults().get(i).getArtist();
		String groupName = top.getResponse().get(j).getResults().get(i).getGroupName();
		String year = top.getResponse().get(j).getResults().get(i).getGroupYear().toString();
		String title = artist + " - " + groupName + " [" + year + "]";
		return title;
	}

	private int getId(int j, int i) {
		return top.getResponse().get(j).getResults().get(i).getGroupId().intValue();
	}

	@Override
	public void onClick(View v) {
		try {
			openTorrent(idMap.get(v.getId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class LoadTopTorrents extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(TopTorrentsActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			top = Top.initTopTorrents(10);
			return top.getStatus();
		}

		@Override
		protected void onPostExecute(Boolean status) {
			if (status == true) {
				populateLayout();
			} else {
				Toast.makeText(TopTorrentsActivity.this, "Couldn't not load top 10", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

	private void openTorrent(int id) {
		Bundle b = new Bundle();
		// intent = new Intent(TopTorrentsActivity.this, what.torrents.torrents.TorrentTabActivity.class);
		b.putInt("torrentGroupId", id);
		intent.putExtras(b);
		startActivity(intent);
	}

}