package fm.dian.hdui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fm.dian.hdui.R;

public class DotsIndicator extends LinearLayout {
    public static int pageMaxCount = 5;
    Context mContext;
    int mCount;
    int mFocuseIndex;

    private ImageView iv_left_arrow;
    private ImageView iv_right_arrow;

    public DotsIndicator(Context context) {
        this(context, null);
    }

    public DotsIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.DotsIndicator);
        mCount = ta.getInt(R.styleable.DotsIndicator_dotsCount, 0);
        mFocuseIndex = ta.getInt(R.styleable.DotsIndicator_focuseIndex, 0);
        ta.recycle();

        setupView(context);
    }

    public int getDotsCount() {
        return mCount;
    }

    public int getFocuseIndex() {
        return mFocuseIndex;
    }

    public void setupDots(int sum) {
        setupDots(sum, 0);
    }

    public void setupDots(int sum, int focuseIndex) {
//        if (sum <= 0) {//一个点不显示
//            mCount = sum;
//            return;
//        }
        mCount = sum;
        removeAllViews();
//        if (sum <= 1) return;

        for (int index = 0; index < sum; index++) {
            ImageView item = new ImageView(mContext);
            item.setBackgroundResource(R.drawable.hd_dot_b);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);


            addView(item, lp);
        }
        mCount = sum;
        setFocuseByIndex(focuseIndex);
    }

    public void addArrow(ImageView left, ImageView right) {
        this.iv_left_arrow = left;
        this.iv_right_arrow = right;
    }
//	private void setupArrow(){
//		if(mCount>5){
//			iv_right_arrow.setVisibility(View.VISIBLE);
//		}
//		
//		if(mCount-5>0){
//			iv_left_arrow.setVisibility(View.VISIBLE);
//		}
//	}


    private void setupView(Context context) {
        mContext = context;
        initView(mCount, mFocuseIndex);
    }

    private void initView(int count, int indexFocuse) {
        mCount = count;
        mFocuseIndex = indexFocuse;
        setGravity(Gravity.CENTER);
        setupDots(count);
    }

    public void setFocuseByIndex(int focuse) {
        if (focuse >= 0 && focuse < mCount && mCount >= 2) {
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setBackgroundResource(R.drawable.hd_dot_b);
            }
            mFocuseIndex = focuse;
            ((ImageView) (getChildAt(mFocuseIndex)))
                    .setBackgroundResource(R.drawable.hd_dot_w);
        }
    }
}
