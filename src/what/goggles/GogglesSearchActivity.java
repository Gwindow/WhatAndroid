package what.goggles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import what.gui.MyActivity;
import what.gui.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.Toast;
import api.goggles.GoogleGogglesSearch;

public class GogglesSearchActivity extends MyActivity {
	private static final int TAKE_PHOTO_CODE = 1;
	private ProgressDialog dialog;
	private Bitmap bitmap;
	private String extStorageDirectory;
	private File file;
	private String result;
	private GoogleGogglesSearch ggs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.goggles, true);
	}

	@Override
	public void init() {
		extStorageDirectory = getExternalCacheDir().toString();
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	public void takePhoto(View v) {
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)));
		startActivityForResult(intent, TAKE_PHOTO_CODE);
	}

	private File getTempFile(Context context) {
		return new File(extStorageDirectory, "image.jpg");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == TAKE_PHOTO_CODE) {
				File file = getTempFile(this);
				try {
					bitmap = Media.getBitmap(getContentResolver(), Uri.fromFile(file));
					int width = 640;
					int height = 480;
					bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
					OutputStream fOut = null;
					file = new File(extStorageDirectory, "image_resized.jpg");
					fOut = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fOut);
					fOut.flush();
					fOut.close();
					new LoadSearchResults().execute();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class LoadSearchResults extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			lockScreenRotation();
			dialog = new ProgressDialog(GogglesSearchActivity.this);
			dialog.setIndeterminate(true);
			dialog.setMessage("Searching...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean status;
			try {
				ggs = new GoogleGogglesSearch(file);
				result = ggs.getResult();
				status = true;
			} catch (Exception e) {
				status = false;
			}
			return status;
		}

		@Override
		protected void onPostExecute(Boolean status) {
			dialog.dismiss();
			if (status == true) {
				Toast.makeText(GogglesSearchActivity.this, result, Toast.LENGTH_SHORT).show();
				unlockScreenRotation();
			}
			if (status == false) {
				Toast.makeText(GogglesSearchActivity.this, "Search Failed", Toast.LENGTH_SHORT).show();
				unlockScreenRotation();
			}
		}
	}

}