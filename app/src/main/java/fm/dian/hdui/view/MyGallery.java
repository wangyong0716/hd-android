package fm.dian.hdui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {
    ListView mListView;
    private OnFlipingListener onFlipingListener;

    public MyGallery(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        // TODO Auto-generated constructor stub
    }
    public void addOnFlipingListener(OnFlipingListener l){
        this.onFlipingListener =l;
    }

    public void setListView(ListView mListView) {
        this.mListView = mListView;
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        // return super.onFling(e1, e2, 0, velocityY);//方法一：只去除翻页惯性
        // return false;//方法二：只去除翻页惯性 注：没有被注释掉的代码实现了开始说的2种效果。

        int kEvent;
        if (isScrollingLeft(e1, e2)) {
            // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
            // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        this.onFlipingListener.onFliping();
        //减慢滑动速度
//		velocityX = velocityX>0 ? 400 : -400;

//		return super.onFling(e1, e2, velocityX, velocityY);
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (null != mListView) {
//				this.mListView.setCanPullRefresh(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (null != mListView) {

//				this.mListView.setCanPullRefresh(true);
                }
                break;
            default:
                if (null != mListView) {

//				this.mListView.setCanPullRefresh(false);
                }
                break;
        }

        super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return true;
    }

    public interface OnFlipingListener{
        public void onFliping();
    }
}
