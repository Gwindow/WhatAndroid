package what.gui;

import android.content.Context;

public class ReportSender {
	ErrorReporter errReporter = new ErrorReporter();

	public ReportSender(Context context) {
		errReporter.Init(context);
		errReporter.CheckErrorAndSendMail(context);
	}

	public void AddCustomData(String Key, String Value) {
		errReporter.AddCustomData(Key, Value);
	}
}
