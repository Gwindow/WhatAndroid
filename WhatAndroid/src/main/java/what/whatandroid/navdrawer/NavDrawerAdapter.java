package what.whatandroid.navdrawer;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * An adapter for displaying the menu options for navigation but
 * supporting a few more advanced find/replace functions than
 * the basic ArrayAdapter
 */
public class NavDrawerAdapter extends ArrayAdapter<String> {
	private List<String> objects;

	public NavDrawerAdapter(Context context, int resource, int textViewResourceId, List<String> objects){
		super(context, resource, textViewResourceId, objects);
		this.objects = objects;
	}

	/**
	 * Find the first entry in the list containing the sequence and
	 * change its text to the replacement
	 */
	public void fuzzyUpdate(String sequence, String replacement){
		for (int i = 0; i < objects.size(); ++i){
			if (objects.get(i).contains(sequence)){
				objects.set(i, replacement);
				break;
			}
		}
	}
}
