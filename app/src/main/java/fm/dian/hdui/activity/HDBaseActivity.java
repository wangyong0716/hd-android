package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.dian.hdservice.AuthService;
import fm.dian.hdui.R;
import fm.dian.hdui.util.HDUiLog;

@SuppressLint("InflateParams")
public abstract class HDBaseActivity extends FragmentActivity implements OnClickListener {
    public HDUiLog HDUi_log = new HDUiLog();
    DisplayMetrics metric = new DisplayMetrics();

    public ImageButton ib_action_bar_left;
    private TextView tv_action_bar_title;
    public TextView tv_common_action_bar_right;
    //聊天页面title
    public ImageButton ib_chat_action_bar_right;
    private TextView tv_action_bar_title_sub;
    private View v_action_bar;

    AuthService authService = AuthService.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HDUi_log.i(this.getClass().getSimpleName() + " onCreate: " + android.os.Process.myPid() + " " + this);
        getWindowManager().getDefaultDisplay().getMetrics(metric);

    }

    public abstract void initUI();

    public void initActionBar(final HDBaseActivity mContext) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v_action_bar = inflator.inflate(R.layout.layout_action_bar_common, null);
            LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            actionBar.setCustomView(v_action_bar, layout);

            tv_action_bar_title = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title);
            tv_common_action_bar_right = (TextView) v_action_bar.findViewById(R.id.tv_common_action_bar_right);
            tv_common_action_bar_right.setOnClickListener(mContext);
            ib_action_bar_left = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_left);
            ib_action_bar_left.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((Activity) mContext).finish();
                }
            });
        }
    }

    public void initChatActionBar(final HDBaseActivity mContext) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v_action_bar = inflator.inflate(R.layout.layout_action_bar_chat, null);
            LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            actionBar.setCustomView(v_action_bar, layout);

            tv_action_bar_title = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title);
            tv_action_bar_title_sub = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title_sub);
            ib_chat_action_bar_right = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_right);
            ib_chat_action_bar_right.setOnClickListener(mContext);
            ib_action_bar_left = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_left);
            ib_action_bar_left.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((Activity) mContext).finish();
                }
            });
        }
    }

    public void initChannelActionBar(final HDBaseActivity mContext) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v_action_bar = inflator.inflate(R.layout.layout_action_bar_channel, null);
            LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            actionBar.setCustomView(v_action_bar, layout);

            tv_action_bar_title = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title);
            tv_common_action_bar_right = (TextView) v_action_bar.findViewById(R.id.tv_common_action_bar_right);
            tv_common_action_bar_right.setOnClickListener(mContext);
            ib_action_bar_left = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_left);
            ib_action_bar_left.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((Activity) mContext).finish();
                }
            });
        }
    }

    public void setActionBarTitle(String title) {
        tv_action_bar_title.setText(title);
    }

    public void setActionBarTitleSub(String titleSub) {
        tv_action_bar_title_sub.setText(titleSub);
    }

    public void setChatActionBarBg(int color) {
        v_action_bar.setBackgroundResource(color);
    }

    public void checkBaseHandlerEnable(Context mContext) {
        if (HDBaseTabFragmentActivity.self == null) {
            Intent loginIntent = new Intent(mContext, HDNavigationActivity.class);
            startActivity(loginIntent);
            ((Activity) mContext).finish();
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard() {
        // 收起软键盘
        InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
