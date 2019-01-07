package fm.dian.hdui.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

import fm.dian.hdui.R;

public class Config {
    public static int[] deviceWidthHeight;

    public static ImageLoaderConfiguration getImageLoaderConfig(Context ctx, File cacheDir) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_load_fail_user) // resource or drawable
                .showImageForEmptyUri(R.drawable.default_image_load_fail_user) // resource or
                        // drawable
                .showImageOnFail(R.drawable.default_image_load_fail_user) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                // .memoryCacheExtraOptions(480, 800) // default = device screen
                // dimensions
                .discCacheExtraOptions(640, 640, CompressFormat.JPEG, 75, null)
                .threadPoolSize(3)
                        // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                        // default
                .discCache(new UnlimitedDiscCache(cacheDir))
                        // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                        // default
                .imageDownloader(
                        new BaseImageDownloader(ctx)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(options).build();
        return config;
    }

    public static DisplayImageOptions getRoomConfig() {
//		HDApp.getInstance().imageLoader.displayImage("", null, options)

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_load_fail_room) // resource or drawable
                .showImageForEmptyUri(R.drawable.default_image_load_fail_room) // resource or
                        // drawable
                .showImageOnFail(R.drawable.default_image_load_fail_room) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        return options;
    }

    public static DisplayImageOptions getUserConfig() {
//		HDApp.getInstance().imageLoader.displayImage("", null, options)

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_load_fail_user) // resource or drawable
                .showImageForEmptyUri(R.drawable.default_image_load_fail_user) // resource or
                        // drawable
                .showImageOnFail(R.drawable.default_image_load_fail_user) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        return options;
    }

    public static DisplayImageOptions getNothingConfig() {
//		HDApp.getInstance().imageLoader.displayImage("", null, options)

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.transparency_pic) // resource or drawable
                .showImageForEmptyUri(R.drawable.transparency_pic) // resource or
                        // drawable
                .showImageOnFail(R.drawable.transparency_pic) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true) // default
                .cacheOnDisc(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        return options;
    }


}
