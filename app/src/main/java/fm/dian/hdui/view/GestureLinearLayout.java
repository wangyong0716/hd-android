package fm.dian.hdui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * *****************************************
 *
 * @author song
 * @fileName    : GuestureRelativeLayout.java
 * @createTime    : 2015-1-9下午11:06:51
 * @fileDes        : *****************************************
 */
@SuppressWarnings("deprecation")
@SuppressLint("ClickableViewAccessibility")
public class GestureLinearLayout extends LinearLayout implements OnTouchListener, OnGestureListener {
    @SuppressWarnings("unused")
    private Context context;
    @SuppressWarnings("unused")
    private RelativeLayout rl_get_mic;

    private boolean isLongPress = false;
    private int wantContinueSliding = 0;//上滑 1  下滑2 没有滑动0
    private boolean nextStop = false;//按钮弹起时停止录音

    private GestureDetector gd;
    final int FLING_MIN_DISTANCE = 150;

    public void setNextStop(boolean flag) {
        this.nextStop = flag;
    }

    private HDChatActivityRecoderListener mRecoderListener;

    public GestureLinearLayout(Context context) {
        super(context);
    }

    public GestureLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GestureLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void initData(RelativeLayout rl_get_mic, HDChatActivityRecoderListener mRecoderListener) {
        this.mRecoderListener = mRecoderListener;
        this.rl_get_mic = rl_get_mic;
        rl_get_mic.setOnTouchListener(this);
        rl_get_mic.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                isLongPress = true;
                return false;
            }
        });
        gd = new GestureDetector(this);
        gd.setIsLongpressEnabled(false);
    }
//	public void disableMicButton(){
//		rl_get_mic.setOnTouchListener(null);
//		rl_get_mic.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(getContext(), "需要管理员权限", Toast.LENGTH_SHORT).show();
//			}
//		});
//	}

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if ((e1.getY() - e2.getY()) > FLING_MIN_DISTANCE) {
            mRecoderListener.releaseMic();//松开手指，锁住上麦
            wantContinueSliding = 2;
        } else {
            mRecoderListener.lockMic();//向上滑动，锁住上麦
            wantContinueSliding = 1;
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (nextStop) {
                nextStop = false;
            } else {
                nextStop = true;
                mRecoderListener.start();
            }
        }
        if (MotionEvent.ACTION_UP == event.getAction()) {
            System.out.println("滑动状态=" + wantContinueSliding);
            //长按行为
            if (isLongPress) {
                nextStop = false;
            }
            //滑动行为
            if (wantContinueSliding != 0) {
                if (wantContinueSliding == 1) {
                    nextStop = false;
                } else if (wantContinueSliding == 2) {
                    nextStop = true;
                }
            }
            //默认点击行为
            if (!nextStop) {//点击按钮时执行
                mRecoderListener.over();

                nextStop = false;
            }

            wantContinueSliding = 0;
            isLongPress = false;
        }
        return gd.onTouchEvent(event);
    }


    public interface HDChatActivityRecoderListener {
        public void start();

        public void over();

        public void lockMic();

        public void releaseMic();
    }

    //	---------------软键盘是否显示--------------------------------
    private Handler uiHandler = new Handler();
    private static final int SOFTKEYPAD_MIN_HEIGHT = 50;

    public static final int keyboardshow = 1;
    public static final int keyboardGone = 2;

    private onKybdsChangeListener mListener;

    public void setOnkbdStateListener(onKybdsChangeListener listener) {
        mListener = listener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh - h > SOFTKEYPAD_MIN_HEIGHT) {// 显示 键盘
            mListener.onKeyBoardStateChange(keyboardshow);
        } else {//隐藏 键盘
            mListener.onKeyBoardStateChange(keyboardGone);
        }
    }

    public interface onKybdsChangeListener {
        public void onKeyBoardStateChange(int state);
    }
}
