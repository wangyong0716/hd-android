package fm.dian.hdui.view.indexlistview;

import android.util.SparseArray;
import android.view.View;

/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2014年1月16日 上午10:42:03
 *          测试
 */

@SuppressWarnings("unchecked")
public class StickyViewHolder {

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }

        return (T) childView;
    }
}
