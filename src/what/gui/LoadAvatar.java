package what.gui;

import what.cache.ImageCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class LoadAvatar extends AsyncTask<Void, Void, Boolean> implements Cancelable {
	private final ImageView avatar;
	private final int userId;
	private final String avatarUrl;
	private Bitmap bitmap;
	private Context mCtx;

	public LoadAvatar(Context ctx, ImageView avatar, int userId, String avatarUrl) {
		this.mCtx = ctx;
		this.avatarUrl = avatarUrl;
		this.avatar = avatar;
		this.userId = userId;
	}

	@Override
	public void cancel() {
		Log.d("cancel", "cancelled avatar");
		super.cancel(true);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean status = false;

		if (!ImageCache.hasImage(userId, avatarUrl)) {
			if (avatarUrl.length() > 0) {
				try {
					bitmap = ImageLoader.loadBitmap(avatarUrl);
					bitmap = Bitmap.createScaledBitmap(bitmap, 110, 110 / (bitmap.getWidth() / bitmap.getHeight()), true);
					ImageCache.saveImage(userId, avatarUrl, bitmap);
					status = true;
					Log.d("cache", "saved : " + String.valueOf(userId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				bitmap = ImageCache.getImage(userId);
				status = true;
				Log.d("cache", "loaded : " + String.valueOf(userId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return status;
	}

	@Override
	protected void onPostExecute(Boolean status) {
		if (status) {
			avatar.setImageBitmap(bitmap);
		} else {
			Bitmap no_avatar = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.dne);
			avatar.setImageBitmap(Bitmap.createScaledBitmap(no_avatar, 110,
					110 / (no_avatar.getWidth() / (no_avatar.getHeight())), true));
		}
	}
}