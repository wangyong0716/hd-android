package fm.dian.hdui.view.face;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdui.R;

@SuppressWarnings({"deprecation"})
public class FaceRelativeLayout extends RelativeLayout implements
        OnItemClickListener, OnClickListener {
    private int inputMode = 1;// 1 文本  2语音
    private ListView mListView;
    private Context context;

    /**
     * 表情页的监听事件
     */
    private OnCorpusSelectedListener mListener;

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vp_face;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layout_point;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private List<List<ChatEmoji>> emojis;

    /**
     * 表情区域
     */
    private View view;

    /**
     * 输入框
     */
    private EditText et_sendmessage;

    /**
     * 表情数据填充器
     */
    private List<FaceAdapter> faceAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;
    private boolean isSilence=false;

    public FaceRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 表情选择监听
     *
     * @author naibo-liao
     * @时间： 2013-1-15下午04:32:54
     */
    public interface OnCorpusSelectedListener {

        void onCorpusSelected(ChatEmoji emoji);

        void onCorpusDeleted();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        emojis = FaceConversionUtil.getInstace().emojiLists;
        onCreate();
    }

    private void onCreate() {
        Init_View();
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    public void goneMicOrAddIcon(boolean mic, boolean add) {
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.LEFT_OF, R.id.btn_face);

        if (mic) {//隐藏
            findViewById(R.id.ib_change_type).setVisibility(View.GONE);
            findViewById(R.id.tv_send).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_blackboard).setVisibility(View.GONE);
            if (inputMode == 2) {
                voice2txt();
            }
            lp.setMargins(10, 0, 0, 0);
        } else {//显示
            findViewById(R.id.ib_change_type).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_send).setVisibility(View.GONE);
            findViewById(R.id.iv_blackboard).setVisibility(View.VISIBLE);
            txt2voice();

            lp.setMargins(0, 0, 0, 0);
        }
        if (add) {//隐藏 小黑板入口
            findViewById(R.id.tv_send).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_blackboard).setVisibility(View.GONE);
        } else {//显示
            findViewById(R.id.tv_send).setVisibility(View.GONE);
            findViewById(R.id.iv_blackboard).setVisibility(View.VISIBLE);
        }
        et_sendmessage.setLayoutParams(lp);
    }
    public void disableBlackboardBtn(boolean silence) {
        this.isSilence=silence;
        if(silence){//禁言状态不可选择表情,不可发小黑板
            findViewById(R.id.btn_face).setOnClickListener(null);
        }else{
            findViewById(R.id.btn_face).setOnClickListener(this);
        }
    }

    public boolean isFaceViewShow() {
        return (view.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_change_type://切换文本、语音
                if (inputMode == 1) {//文本变语音
                    txt2voice();

                } else if (inputMode == 2) {//语音变文本
                    showKeyboard4Edittext();
                    voice2txt();
                }
                findViewById(R.id.ll_blackbord_bottom).setVisibility(View.GONE);
                break;
            case R.id.btn_face:
                //显示/ 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    hideKeyBoard();
                }
                findViewById(R.id.ll_blackbord_bottom).setVisibility(View.GONE);
                break;
            case R.id.et_sendmessage:
                // 隐藏表情选择框
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                if (null != mListView && mListView.getCount() > 0) {
                    mListView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mListView.setSelection(mListView.getCount() - 1);
                        }
                    }, 500);
                }
                break;

        }
    }

    private void txt2voice() {
        if (inputMode == 1) {
            ((ImageView)findViewById(R.id.ib_change_type)).setImageDrawable(getResources().getDrawable(R.drawable.btn_chat_room_type_txt_bg));
            hideFaceView();
            hideKeyBoard();

            findViewById(R.id.rl_et).setVisibility(View.GONE);
            findViewById(R.id.rl_get_mic).setVisibility(View.VISIBLE);
            if (findViewById(R.id.iv_blackboard).getVisibility() != View.VISIBLE) {
                findViewById(R.id.tv_send).setVisibility(View.GONE);
            }


            inputMode = 2;
        }
    }

    Drawable typeBg = null;

    private void voice2txt() {

        if (inputMode == 2) {
            String tag = (String) findViewById(R.id.ib_change_type).getTag();
            if ("speaking".equals(tag)) {
                ((ImageView)findViewById(R.id.ib_change_type)).setImageDrawable(getResources().getDrawable(R.drawable.btn_chat_room_type_voice_bg));
            } else {
                ((ImageView)findViewById(R.id.ib_change_type)).setImageDrawable(getResources().getDrawable(R.drawable.btn_chat_room_type_voice_bg));
            }

            findViewById(R.id.rl_et).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_get_mic).setVisibility(View.GONE);
            if (findViewById(R.id.iv_blackboard).getVisibility() != View.VISIBLE) {
                findViewById(R.id.tv_send).setVisibility(View.VISIBLE);
            }
            if (null != mListView && mListView.getCount() > 0) {
                mListView.setSelection(mListView.getCount() - 1);
            }
            inputMode = 1;
        }
    }

    private void showKeyboard4Edittext(){
        if(!isSilence){
            et_sendmessage.setFocusable(true);
            et_sendmessage.setFocusableInTouchMode(true);
            et_sendmessage.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏表情选择框
     */
    public boolean hideFaceView() {
        // 隐藏表情选择框
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 隐藏键盘
     */
    public boolean hideKeyBoard() {
        // 收起软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean result = imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0);
        return result;
    }


    /**
     * 初始化控件
     */
    private void Init_View() {
        vp_face = (ViewPager) findViewById(R.id.vp_contains);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        layout_point = (LinearLayout) findViewById(R.id.iv_image);
        et_sendmessage.setOnClickListener(this);
        findViewById(R.id.ib_change_type).setOnClickListener(this);
        findViewById(R.id.btn_face).setOnClickListener(this);
        view = findViewById(R.id.ll_facechoose);

    }

    /**
     * 初始化显示表情的viewpager
     */
    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
//		View nullView2 = new View(context);
////		 设置透明背景
//		nullView2.setBackgroundColor(Color.TRANSPARENT);
//		pageViews.add(nullView2); 
    }

    /**
     * 初始化游标 滚动小点点
     */
    private void Init_Point() {

        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_point.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            pointViews.add(imageView);

        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vp_face.setAdapter(new ViewPagerAdapter(pageViews));

        vp_face.setCurrentItem(1);
        current = 0;
        vp_face.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
        if (emoji.getId() == R.drawable.face_del_icon) {
            int selection = et_sendmessage.getSelectionStart();
            String text = et_sendmessage.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    et_sendmessage.getText().delete(start, end);
                    return;
                }
                et_sendmessage.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            if (mListener != null)
                mListener.onCorpusSelected(emoji);
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .addFace(getContext(), emoji.getId(), emoji.getCharacter());
            et_sendmessage.append(spannableString);
        }
    }

    public void setListView(ListView mListView) {
        this.mListView = mListView;
    }
}
