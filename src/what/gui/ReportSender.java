package what.gui;

import android.content.Context;

public class ReportSender {
	private ErrorReporter errReporter;

	public ReportSender(Context context) {
		// TODO renable
		errReporter = new ErrorReporter();
		errReporter.init(context);
		errReporter.CheckErrorAndSendMail(context);
	}
}
