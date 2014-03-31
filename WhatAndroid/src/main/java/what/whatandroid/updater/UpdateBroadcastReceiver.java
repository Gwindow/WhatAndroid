package what.whatandroid.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that will launch the update service when it receives a broadcast
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent){
		Intent update = new Intent(context, UpdateService.class);
		context.startService(update);
	}
}
