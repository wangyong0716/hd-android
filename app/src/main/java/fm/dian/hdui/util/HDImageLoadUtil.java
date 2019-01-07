package fm.dian.hdui.util;

import android.annotation.SuppressLint;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class HDImageLoadUtil {

    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> map = new HashMap<Integer, String>();

    public void setImageLoad(View v, String url) {
        if (v == null || url == null) {
            return;
        }
        if (!isLoadingComplete(v, url)) {
            cancelImageLoad(v);
            map.put(v.hashCode(), url);
        }
    }

    public void cancelImageLoad(View v) {
        if (v == null) {
            return;
        }
        if (map.containsKey(v.hashCode())) {
            map.remove(v.hashCode());
        }
    }

    public boolean isLoadingComplete(View v, String url) {
        if (v == null || url == null) {
            return false;
        }
        if (map.containsKey(v.hashCode()) && url.equals(map.get(v.hashCode()))) {
            return true;
        }
        return false;
    }

}
