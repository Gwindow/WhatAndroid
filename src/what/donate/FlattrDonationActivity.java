package what.donate;

import what.gui.MyActivity;
import what.gui.R;
import what.gui.ReportSender;
import what.services.NotificationService;
import android.content.Intent;
import android.os.Bundle;

public class FlattrDonationActivity extends MyActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReportSender sender = new ReportSender(this);
		setContentView(R.layout.flattr);

		Intent i = new Intent(this, NotificationService.class);
		startService(i);
	}
}
