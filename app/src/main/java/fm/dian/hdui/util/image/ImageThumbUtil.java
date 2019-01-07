package fm.dian.hdui.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import fm.dian.hdui.R;

/**
 * ******************
 * fileName :ImageThumbUtil.java author : song createTime:2015-3-10 下午3:00:11
 * fileDes :
 */
public class ImageThumbUtil {
    public static Bitmap getViewBitmap(View view, Context ctx) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = null;
        try {
            if (null != view.getDrawingCache()) {
                bitmap = Bitmap.createScaledBitmap(view.getDrawingCache(), view.getWidth(),
                        view.getHeight(), false);
            } else {
                bitmap = ((BitmapDrawable) (ctx.getResources()
                        .getDrawable(R.drawable.app_icon)))
                        .getBitmap();
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();
        }
        return bitmap;

    }

}
