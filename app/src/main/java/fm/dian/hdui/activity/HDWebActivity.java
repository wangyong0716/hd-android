package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgentJSInterface;

import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

@SuppressLint({"SetJavaScriptEnabled", "HandlerLeak"})
public class HDWebActivity extends HDBaseActivity {
    WebView webView;
    ProgressBar loadingProgress;
    PullRefreshLayout layout;

    private String url = "";
    private String title = "";
    long currRoomId;
    long liveId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_web_page);
        initUI();

        load();
    }

    private void load(){
        if (null != url) {
            useWebview(url);
        }
        if (title != url) {
            setActionBarTitle(title);
        }
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        ib_action_bar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                webView.loadUrl("javascript:historyPlayer.pause();");
                HDWebActivity.this.finish();
            }
        });

        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        currRoomId = getIntent().getLongExtra("currRoomId", 0);
        liveId = getIntent().getLongExtra("liveId", 0);

        if (currRoomId != 0 && liveId != 0) {
            Drawable drawable= getResources().getDrawable(R.drawable.live_share);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_common_action_bar_right.setCompoundDrawables(drawable, null, null, null);
            tv_common_action_bar_right.setBackgroundResource(0);
            tv_common_action_bar_right.setOnClickListener(this);
            tv_common_action_bar_right.setVisibility(View.VISIBLE);
        }
        else {
            tv_common_action_bar_right.setVisibility(View.GONE);
        }
        webView = (WebView) findViewById(R.id.detail_webview);
        new MobclickAgentJSInterface(this, webView);//友盟统计
        loadingProgress = (ProgressBar) findViewById(R.id.loadingProgress);
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(url);
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    private void useWebview(String toUrl) {
        webView.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);
        //使网页自使用手机屏幕
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Activity和Webview根据加载程度决定进度条的进度大小
                //当加载到100%的时候 进度条自动消失
                setProgress(progress * 100);
                if (progress == 100) {
                    loadingProgress.setAnimation(shrink());
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = loadingProgress;
                    mHandler.sendMessageDelayed(msg, 995);
//            	  loadingProgress.setVisibility(View.GONE);
                } else if (progress == 0) {
                    loadingProgress.setAnimation(unfold());
                    loadingProgress.setVisibility(View.VISIBLE);
                }
                loadingProgress.setProgress(progress);
                super.onProgressChanged(view, progress);
            }
        });


        webView.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                webView.loadUrl(url);
                return true;
            }
        });

        webView.addJavascriptInterface(this, "map");

        if(!toUrl.contains("http://")){
            toUrl = "http://" + toUrl;
        }

        webView.loadUrl(toUrl);
    }

    //透明度隐藏
    private Animation shrink() {
        Animation myAnimation_Alpha = new AlphaAnimation(1.0f, 0.0f);
        myAnimation_Alpha.setDuration(1000);
        myAnimation_Alpha.setInterpolator(new AccelerateInterpolator());
        return myAnimation_Alpha;
    }

    //通过X轴做展开
    private Animation unfold() {
        Animation myAnimation_Scale = new ScaleAnimation(0.0f, 1.0f, 1.0f,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        myAnimation_Scale.setDuration(2000);
        myAnimation_Scale.setInterpolator(new AccelerateInterpolator());
        return myAnimation_Scale;
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ProgressBar loadingProgress = (ProgressBar) msg.obj;
                    loadingProgress.setVisibility(View.GONE);
                    layout.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent shareIntent = new Intent(HDWebActivity.this, HDShareActivity.class);
        shareIntent.putExtra(HDShareActivity.ROOM_ID, currRoomId);
        shareIntent.putExtra(HDShareActivity.LIVE_ID, liveId);
        shareIntent.putExtra("shareUrl", url);
        HDWebActivity.this.startActivity(shareIntent);
        overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webView.loadUrl("javascript:historyPlayer.pause();");
        }
        return super.onKeyDown(keyCode, event);
    }
}
