package what.gui;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * @author Gwindow
 * @since Aug 2, 2012 9:31:43 PM
 */
public class MyImageLoader {
	private final ImageLoader imageLoader;
	private final Context context;
	private File cacheDir;
	private DisplayImageOptions options;
	private int defaultImageResource;

	public MyImageLoader(Context context) {
		this(context, R.drawable.noartwork);
	}

	public MyImageLoader(Context context, int defaultImageResource) {
		this.context = context;
		this.defaultImageResource = defaultImageResource;
		imageLoader = ImageLoader.getInstance();
		initImageLoader();
	}

	private void initImageLoader() {
		cacheDir = StorageUtils.getOwnCacheDirectory(context, "UniversalImageLoader/Cache");

		//Setup default options for images
		options =
			new DisplayImageOptions.Builder()
				.showStubImage(defaultImageResource)
				.showImageForEmptyUri(defaultImageResource)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.build();

		//Setup options for the image loader
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		ImageLoaderConfiguration config =
			new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800)
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.denyCacheImageMultipleSizesInMemory()
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs()
				.defaultDisplayImageOptions(options)
				.build();
		imageLoader.init(config);
	}

	public void displayImage(String imageUrl, ImageView imageView) {
		imageLoader.displayImage(imageUrl, imageView, options);
	}

	public void displayImage(String imageUrl, ImageView imageView, int resource, ImageLoadingListener imageLoadingListener) {
		options =
				new DisplayImageOptions.Builder()
					.showStubImage(resource)
					.showImageForEmptyUri(resource)
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.build();
		imageLoader.displayImage(imageUrl, imageView, options, imageLoadingListener);
	}

	public void displayImage(String imageUrl, ImageView imageView, int resource) {
		options =
				new DisplayImageOptions.Builder()
					.showStubImage(resource)
					.showImageForEmptyUri(resource)
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.build();
		imageLoader.displayImage(imageUrl, imageView, options);
	}

	public void displayImage(String imageUrl, ImageView imageView, ImageLoadingListener imageLoadingListener) {
		imageLoader.displayImage(imageUrl, imageView, options, imageLoadingListener);
	}
}
