/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.dian.hdui.view.pulllistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.TimerTask;

import fm.dian.hdui.R;

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
@SuppressWarnings({"unused", "deprecation"})
public class RefreshableListView extends ListView {
    // 更新时间
//	private TextView head_lastUpdatedTextView = null;
    // 添加控制器（是否可以刷新）
    private boolean isCanPullRefresh = true;

    // 控制是否启用下拉刷新控制器
    public void setCanPullRefresh(boolean isCanPullRefresh) {
        this.isCanPullRefresh = isCanPullRefresh;
    }

    private int flag_change_icon = 0;

    // ----------------------------------
    private View mHeaderContainer = null;
    private View mHeaderView = null;
    //	private ImageView mArrow = null;
    private ProgressBar mProgress = null;
    private TextView mText = null;
    private float mY = 0;
    private float mHistoricalY = 0;
    private int mHistoricalTop = 0;
    private int mInitialHeight = 0;
    private boolean mFlag = false;
    private boolean mArrowUp = false;
    private boolean mIsRefreshing = false;
    private int mHeaderHeight = 0;
    private OnRefreshListener mListener = null;

    private static final int REFRESH = 0;
    private static final int NORMAL = 1;
    private static final int HEADER_HEIGHT_DP = 42;

    Handler handler = new Handler();

    public RefreshableListView(Context context) {
        super(context);
        initialize();
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        mListener = l;
    }

    public void completeRefreshing() {
//		mProgress.setVisibility(View.INVISIBLE);
//		mArrow.setVisibility(View.VISIBLE);

        mText.setText(R.string.release_refresh_success);
        mText.setTextColor(Color.WHITE);
//		setArrow(R.drawable.refresh_success_icon);
//		ScaleAnimation scaleAnim=new ScaleAnimation(0f, 1.3f,0.5f, 1.3f,Animation.RELATIVE_TO_SELF, 0.5f,
//				Animation.RELATIVE_TO_SELF,0.5f);
//		scaleAnim.setRepeatCount(0); 
//		scaleAnim.setDuration(200);
//		mArrow.startAnimation(scaleAnim);
//		handler.postDelayed(task, 1500);
        mHandler.sendMessage(mHandler.obtainMessage(NORMAL, mHeaderHeight, 0));

        mIsRefreshing = false;
        invalidateViews();
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (flag_change_icon == 0) {
                mHandler.sendMessage(mHandler.obtainMessage(NORMAL, mHeaderHeight, 0));
            }
            flag_change_icon++;
//			setArrow(R.drawable.refreshable_listview_arrow);
//			rotateArrow();
            mText.setTextColor(Color.GRAY);

        }
    };

//	private void setArrow(int resId){
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),resId);
//		mArrow.setImageBitmap(bitmap);
//	}

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanPullRefresh) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeMessages(REFRESH);
                    mHandler.removeMessages(NORMAL);
                    mY = mHistoricalY = ev.getY();
                    if (mHeaderContainer.getLayoutParams() != null)
                        mInitialHeight = mHeaderContainer.getLayoutParams().height;
                    break;
                case MotionEvent.ACTION_MOVE:
                    try {
                        mHistoricalTop = getChildAt(0).getTop();
                    } catch (IndexOutOfBoundsException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mIsRefreshing) {
                        if (mArrowUp) {
                            startRefreshing();
                            mHandler.sendMessage(mHandler.obtainMessage(REFRESH,
                                    (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                        } else {
                            if (getChildAt(0).getTop() == 0) {
                                mHandler.sendMessage(mHandler
                                        .obtainMessage(NORMAL,
                                                (int) (ev.getY() - mY) / 2
                                                        + mInitialHeight, 0));
                            }
                        }
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(REFRESH,
                                (int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
                    }
                    mFlag = false;
                    break;
            }
        }
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            // TODO: handle exception
//				e.printStackTrace();
            return false;
        }
//		return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanPullRefresh) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE
                    && getFirstVisiblePosition() == 0) {
                float direction = ev.getY() - mHistoricalY;
                int height = (int) (ev.getY() - mY) / 2 + mInitialHeight;
                if (height < 0)
                    height = 0;

                // Scrolling downward
                if (direction > 0) {
                    // Refresh bar is extended if top pixel of the first item is
                    // visible
                    if (getChildAt(0).getTop() == 0) {
                        if (mHistoricalTop < 0) {
                            mY = ev.getY();
                            mHistoricalTop = 0;
                        }
                        // Extends refresh bar
                        setHeaderHeight(height);

                        // Stop list scroll to prevent the list from
                        // overscrolling
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        mFlag = false;
                    }
                } else if (direction < 0) {
                    // Scrolling upward
                    try {// ·ÀÖ¹ÏÂ±êÔ½½ç
                        // Refresh bar is shortened if top pixel of the
                        // first
                        // item is visible
                        if (getChildAt(0).getTop() == 0) {
                            setHeaderHeight(height);

                            // If scroll reaches top of the list, list scroll is
                            // enabled
                            if (getChildAt(1) != null
                                    && getChildAt(1).getTop() <= 1 && !mFlag) {
                                ev.setAction(MotionEvent.ACTION_DOWN);
                                mFlag = true;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }

                mHistoricalY = ev.getY();
            }
        }
        try {

            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    private void initialize() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderContainer = inflater.inflate(R.layout.layout_refreshable_list_header,
                null);
        mHeaderView = mHeaderContainer
                .findViewById(R.id.refreshable_list_header);
//		mArrow = (ImageView) mHeaderContainer
//				.findViewById(R.id.refreshable_list_arrow);
        mProgress = (ProgressBar) mHeaderContainer
                .findViewById(R.id.refreshable_list_progress);
        mText = (TextView) mHeaderContainer
                .findViewById(R.id.refreshable_list_text);
        addHeaderView(mHeaderContainer);
        // 更新时间
//		head_lastUpdatedTextView = (TextView) mHeaderContainer
//				.findViewById(R.id.head_lastUpdatedTextView);

//		head_lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());


        mHeaderHeight = (int) (HEADER_HEIGHT_DP * getContext().getResources()
                .getDisplayMetrics().density);
        setHeaderHeight(0);
    }

    private void setHeaderHeight(int height) {
        if (isCanPullRefresh) {
            if (height <= 1)
                mHeaderView.setVisibility(View.GONE);
            else
                mHeaderView.setVisibility(View.VISIBLE);

            // Extends refresh bar
            LayoutParams lp = (LayoutParams) mHeaderContainer.getLayoutParams();
            if (lp == null)
                lp = new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT);
            lp.height = height;
            mHeaderContainer.setLayoutParams(lp);

            // Refresh bar shows up from bottom to top
            LinearLayout.LayoutParams headerLp = (LinearLayout.LayoutParams) mHeaderView
                    .getLayoutParams();
            if (headerLp == null)
                headerLp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT);
            headerLp.topMargin = -mHeaderHeight + height;
            mHeaderView.setLayoutParams(headerLp);

            if (!mIsRefreshing) {
                // If scroll reaches the trigger line, start refreshing
                if (height > mHeaderHeight && !mArrowUp) {
//				mArrow.startAnimation(AnimationUtils.loadAnimation(
//						getContext(), R.anim.refreshable_list_rotate));
                    mText.setText(R.string.release_refresh);
//				rotateArrow();
                    mArrowUp = true;
                } else if (height < mHeaderHeight && mArrowUp) {
//				mArrow.startAnimation(AnimationUtils.loadAnimation(
//						getContext(), R.anim.refreshable_list_rotate));
                    mText.setText(R.string.pull_refresh);
//				rotateArrow();
                    mArrowUp = false;
                }
            }
        }
    }

//	private void rotateArrow() {
//		Drawable drawable = mArrow.getDrawable();
//		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//				drawable.getIntrinsicHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmap);
//		canvas.save();
//		canvas.rotate(180.0f, canvas.getWidth() / 2.0f,
//				canvas.getHeight() / 2.0f);
//		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//				drawable.getIntrinsicHeight());
//		drawable.draw(canvas);
//		canvas.restore();
//		mArrow.setImageBitmap(bitmap);
//	}

    private void startRefreshing() {
        handler.removeCallbacks(task);
        flag_change_icon = 0;
//		mArrow.setVisibility(View.INVISIBLE);
//		mProgress.setVisibility(View.VISIBLE);
        mText.setText(R.string.release_refresh_doing);
        mIsRefreshing = true;

        if (mListener != null)
            mListener.onRefresh();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int limit = 0;
            switch (msg.what) {
                case REFRESH:
                    limit = mHeaderHeight;
                    break;
                case NORMAL:
                    limit = 0;
                    break;
            }

            // Elastic scrolling
            if (msg.arg1 >= limit) {
                setHeaderHeight(msg.arg1);
                int displacement = (msg.arg1 - limit) / 5;
                if (displacement == 0)
                    mHandler.sendMessage(mHandler.obtainMessage(msg.what,
                            msg.arg1 - 1, 0));
                else
                    mHandler.sendMessage(mHandler.obtainMessage(msg.what,
                            msg.arg1 - displacement, 0));
            }
        }

    };

    // 底部加载更多按钮
    public interface OnRefreshListener {
        public void onRefresh();
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        // TODO Auto-generated method stub
        try {
            super.setAdapter(adapter);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("RefreshableListView set adapter Exception " + e.getMessage());
        }
    }

}
