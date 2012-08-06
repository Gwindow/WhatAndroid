package what.gui;

import android.content.Context;

public class ReportSender {
	private ErrorReporter errReporter;

	public ReportSender(Context context) {
		errReporter = new ErrorReporter();
		errReporter.init(context);
		errReporter.checkErrorAndSendMail(context);
	}
}
