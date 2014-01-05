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

	/**
	 * Create an image loader, it uses the singleton of the ImageLoader, but allows for
	 * setting some options, such as the context and default image resource
	 * @param context the context to use
	 * @param defaultImageResource the image resource to show when loading an image
	 */
	public MyImageLoader(Context context, int defaultImageResource) {
		this.context = context;
		this.defaultImageResource = defaultImageResource;
		imageLoader = ImageLoader.getInstance();
		initImageLoader();
	}

	/**
	 * Initialize the image loader with our desired configuration
	 */
	private void initImageLoader() {
		cacheDir = StorageUtils.getOwnCacheDirectory(context, "UniversalImageLoader/Cache");

		//Setup default options for images
		options =
			new DisplayImageOptions.Builder()
				.showStubImage(defaultImageResource)
				.showImageForEmptyUri(defaultImageResource)
				.cacheInMemory()
				.cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.build();

		//Setup options for the image loader
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		ImageLoaderConfiguration config =
			new ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800)
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.denyCacheImageMultipleSizesInMemory()
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.defaultDisplayImageOptions(options)
				.build();
		imageLoader.init(config);
	}

	/**
	 * Display an image in the passed ImageView
	 * @param imageUrl url of the image to load and display
	 * @param imageView where to display the loaded image
	 */
	public void displayImage(String imageUrl, ImageView imageView) {
		imageLoader.displayImage(imageUrl, imageView, options);
	}

	/**
	 * Load an image to an image view and specify a resource to be
	 * used as the stub and empty default image along with a loading listener
	 * @param imageUrl url of image to load
	 * @param imageView where to display the loaded image
	 * @param resource default image to display while loading & in case of empty url
	 * @param imageLoadingListener loading listener to send loading progress events too
	 */
	public void displayImage(String imageUrl, ImageView imageView, int resource, ImageLoadingListener imageLoadingListener) {
		DisplayImageOptions opt =
				new DisplayImageOptions.Builder()
					.showStubImage(resource)
					.showImageForEmptyUri(resource)
					.cacheInMemory()
					.cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.build();
		imageLoader.displayImage(imageUrl, imageView, opt, imageLoadingListener);
	}

	/**
	 * Load an image into an image view and specify a default image to display
	 * while loading & in case of empty url
	 * @param imageUrl url of image to load
	 * @param imageView where to display the image
	 * @param resource default image to display while loading & in case of empty url
	 */
	public void displayImage(String imageUrl, ImageView imageView, int resource) {
		DisplayImageOptions opt =
				new DisplayImageOptions.Builder()
					.showStubImage(resource)
					.showImageForEmptyUri(resource)
					.cacheInMemory()
					.cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.build();
		imageLoader.displayImage(imageUrl, imageView, opt);
	}

	/**
	 * Load an image to an imageview and notify the listener on our progress
	 * @param imageUrl url of image to load
	 * @param imageView where to load the image
	 * @param imageLoadingListener loading listener to send loading progress events too
	 */
	public void displayImage(String imageUrl, ImageView imageView, ImageLoadingListener imageLoadingListener) {
		imageLoader.displayImage(imageUrl, imageView, options, imageLoadingListener);
	}
}
