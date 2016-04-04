package what.whatandroid.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import what.whatandroid.R;

/**
 * A dialog fragment for navigating to and selecting a folder
 */
public class FolderPickerDialog extends DialogFragment
		implements AdapterView.OnItemClickListener, View.OnClickListener {
	private static String DIRECTORY = "what.whatandroid.folderpickerdialog.DIRECTORY",
			FALLBACK_DIR = "what.whatandroid.folderpickerdialog.FALLBACK_DIR:";
	/**
	 * Adapter displaying the list of directories in the current directory
	 */
	private ArrayAdapter<String> adapter;
	/**
	 * The current directory we're in
	 */
	private File currentDir;
	/**
	 * Header view showing the current directory's title
	 */
	private TextView currentDirTitle;
	/**
	 * Listener to inform of the user's folder selection
	 */
	private FolderPickerCallback pickerCallback;

	/**
	 * Callbacks to be implemented by the activity opening the folder picker dialog to
	 * get back the full path of the selected folder. If the user cancels no function will be called
	 */
	public interface FolderPickerCallback {
		/**
		 * Called when the user has selected the desired folder
		 *
		 * @param folder the absolute path to the selected folder
		 */
		public void pickFolder(String folder);
	}

	/**
	 * Create the folder picker dialog and specify the initial directory to display
	 *
	 * @param dir initial directory to display
	 * @param fallbackDir directory to fallback to if dir can't be opened
	 */
	public static FolderPickerDialog newInstance(String dir, String fallbackDir){
		FolderPickerDialog d = new FolderPickerDialog();
		Bundle args = new Bundle();
		args.putString(DIRECTORY, dir);
		args.putString(FALLBACK_DIR, fallbackDir);
		d.setArguments(args);
		return d;
	}

	public FolderPickerDialog(){
		//Required empty ctor
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			pickerCallback = (FolderPickerCallback)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implement FolderPickerCallback");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_folder_picker, null);
		((ImageButton)view.findViewById(R.id.new_folder)).setImageResource(R.drawable.ic_add_24dp);
		currentDirTitle = (TextView)view.findViewById(R.id.current_folder);
		ListView list = (ListView)view.findViewById(R.id.list);
		view.findViewById(R.id.new_folder).setOnClickListener(this);

		if (savedInstanceState != null){
			currentDir = new File(savedInstanceState.getString(DIRECTORY));
		}
		else {
			currentDir = new File(getArguments().getString(DIRECTORY));
		}
		//Try to open the directory and load the list of subdirectories
		List<String> dirs = readDirectory(currentDir);
		//If we fail to open the directory read the fallback directory
		if (dirs == null){
			Toast.makeText(getActivity(), "Could not enter directory " + currentDir.getName(), Toast.LENGTH_SHORT).show();
			currentDir = new File(getArguments().getString(FALLBACK_DIR));
			dirs = readDirectory(currentDir);
		}
		currentDirTitle.setText(currentDir.getName());
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_folder, R.id.folder_name, dirs);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		builder.setView(view)
				.setTitle("Select Folder")
				.setPositiveButton("Select", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){
						pickerCallback.pickFolder(currentDir.getAbsolutePath());
					}
				})
				.setNegativeButton("Cancel", null);
		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString(DIRECTORY, currentDir.getAbsolutePath());
	}

	/**
	 * When a directory is clicked in the list try to open it
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		enterDirectory(adapter.getItem(position));
	}

	/**
	 * Read and return a list of all subdirectories in the passed directory, returns null
	 * if reading failed. This list will also contain '..' as the first entry if the
	 * directory has a parent directory
	 * @param directory directory to read
	 * @return list of subdirectories or null if we couldn't read the directory
	 */
	public List<String> readDirectory(File directory){
		String sd[] = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename){
				return new File(dir, filename).isDirectory();
			}
		});
		if (sd == null){
			return null;
		}
		ArrayList<String> subdirs = new ArrayList<String>(Arrays.asList(sd));
		if (directory.getParent() != null){
			subdirs.add(0, "..");
		}
		return subdirs;
	}

	/**
	 * Enter a new directory from the current one, or go up a directory
	 *
	 * @param dir '..' will specify to go up to the parent if possible, any other string
	 *            indicates a subdirectory to enter
	 */
	public void enterDirectory(String dir){
		File prev = currentDir;
		if (dir.equalsIgnoreCase("..")){
			currentDir = currentDir.getParentFile();
		}
		else {
			currentDir = new File(currentDir, dir);
		}
		List<String> dirs = readDirectory(currentDir);
		//If we can't enter the directory don't update the list and inform the user (we probably don't have adequate permissions)
		if (dirs == null){
			Toast.makeText(getActivity(), "Could not enter directory " + dir, Toast.LENGTH_SHORT).show();
			currentDir = prev;
			return;
		}
		if (!currentDir.getName().isEmpty()){
			currentDirTitle.setText(currentDir.getName());
		}
		else {
			currentDirTitle.setText("/");
		}
		adapter.clear();
		adapter.addAll(dirs);
		adapter.notifyDataSetChanged();
	}

	public void createDirectory(String name){
		File newDir = new File(currentDir, name);
		//If we can't create the directory it's likely a permissions issue so let the user know
		if (!newDir.mkdir()){
			Toast.makeText(getActivity(), "Could not create directory " + name, Toast.LENGTH_LONG).show();
		}
		else {
			enterDirectory(name);
		}
	}

	/**
	 * When the new directory button is clicked we put up a text dialog to get the desired
	 * directory name from the user
	 */
	@Override
	public void onClick(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final EditText input = new EditText(getActivity());
		input.setSingleLine(true);
		builder.setTitle("Enter New Directory Name:")
				.setView(input)
				.setPositiveButton("Create", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){
						if (input.getText().length() > 0){
							createDirectory(input.getText().toString());
						}
						else {
							Toast.makeText(getActivity(), "No directory name entered", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("Cancel", null);
		builder.show();
	}
}
