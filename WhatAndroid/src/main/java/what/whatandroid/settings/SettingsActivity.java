package what.whatandroid.settings;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity for changing user settings
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
	}
}
