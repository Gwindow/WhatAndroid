package what.whatandroid.errors;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;

import java.io.*;
import java.util.Date;
import java.util.Random;

/**
 * Handles checking for any errors that may have crashed the app previously
 * and listening for uncaught errors and writing out stack traces that we
 * can then pick up and report the next time the app starts
 */
public class ErrorLogger implements Thread.UncaughtExceptionHandler {
	/**
	 * The previous error handler so that we can throw the exception up once we've
	 * written our crash report
	 */
	private Thread.UncaughtExceptionHandler prevHandler;
	private Context context;

	/**
	 * Create an error reporter to check for errors. The context will be used to launch a send to
	 * intent if an error report is found
	 *
	 * @param context context to send error report in
	 */
	public ErrorLogger(Context context){
		this.context = context;
		prevHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex){
		saveReport(buildReport(thread, ex));
		prevHandler.uncaughtException(thread, ex);
	}

	/**
	 * Save the error report to disk using some generated filename and the .report extension
	 *
	 * @param report error report to save
	 */
	private void saveReport(String report){
		try {
			Random rand = new Random();
			String fName = rand.nextInt() + ".report";
			FileOutputStream file = null;
			try {
				file = context.openFileOutput(fName, Context.MODE_PRIVATE);
				file.write(report.getBytes());
			}
			catch (IOException e){
				e.printStackTrace();
			}
			finally {
				if (file != null){
					file.close();
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Get the error report for this crash
	 */
	private String buildReport(Thread thread, Throwable ex){
		StringBuilder report = new StringBuilder();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			report.append("Error Report on: ")
				.append(new Date().toString())
				.append("\n")
				.append(info.packageName)
				.append(" version name: ")
				.append(info.versionName)
				.append(", version code: ")
				.append(info.versionCode)
				.append("\nAndroid Version: ")
				.append(Build.VERSION.RELEASE)
				.append("\nDevice: ")
				.append(Build.DEVICE)
				.append("\nBrand: ")
				.append(Build.BRAND)
				.append("\nModel: ")
				.append(Build.MODEL);

			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			report.append("\n\nStack Trace:\n")
				.append(writer.toString())
				.append("\n\nPlease describe where this occurred and what")
				.append("may have provoked the crash to help debug:\n");

			for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()){
				cause.printStackTrace(printWriter);
				report.append(writer);
			}
			printWriter.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return report.toString();
	}
}
