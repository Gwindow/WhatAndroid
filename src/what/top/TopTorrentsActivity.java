package what.top;

import java.util.ArrayList;

import what.gui.MyActivity;
import what.gui.R;
import what.gui.ReportSender;
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
	private ArrayList<TextView> sectionTitle = new ArrayList<TextView>();
	private ArrayList<TextView> pastDay = new ArrayList<TextView>();
	private ArrayList<TextView> pastWeek = new ArrayList<TextView>();
	private ArrayList<TextView> allTime = new ArrayList<TextView>();
	private ArrayList<TextView> snatched = new ArrayList<TextView>();
	private ArrayList<TextView> transferred = new ArrayList<TextView>();
	private ArrayList<TextView> seeded = new ArrayList<TextView>();
	private Top top;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toptorrents);
		@SuppressWarnings("unused")
		ReportSender sender = new ReportSender(this);

		scrollLayout = (LinearLayout) this.findViewById(R.id.scrollLayout);

		populateLayout();
	}

	public void populateLayout() {
		new LoadTopTorrents().execute();
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
					pastDay.get(i)
							.setText(top.getResponse().get(0).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					pastDay.get(i).setOnClickListener(TopTorrentsActivity.this);
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
					pastWeek.get(i).setText(
							top.getResponse().get(1).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					pastWeek.get(i).setOnClickListener(TopTorrentsActivity.this);
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
					allTime.get(i)
							.setText(top.getResponse().get(2).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					allTime.get(i).setOnClickListener(TopTorrentsActivity.this);
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
					snatched.get(i).setText(
							top.getResponse().get(3).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					snatched.get(i).setOnClickListener(TopTorrentsActivity.this);
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
					transferred.get(i).setText(
							top.getResponse().get(4).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					transferred.get(i).setOnClickListener(TopTorrentsActivity.this);
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
					seeded.get(i).setText(top.getResponse().get(5).getResults().get(i).getArtist() + " - " + "Album name [2010]");
					seeded.get(i).setOnClickListener(TopTorrentsActivity.this);
					scrollLayout.addView(seeded.get(i));
				}

			} else {
				Toast.makeText(TopTorrentsActivity.this, "Couldn't not load top 10", Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			unlockScreenRotation();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}