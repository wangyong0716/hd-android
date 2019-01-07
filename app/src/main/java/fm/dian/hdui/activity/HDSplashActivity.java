package fm.dian.hdui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.HDDataService;
import fm.dian.hdservice.HeartbeatService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.net.oldinterface.Login;

public class HDSplashActivity extends HDBaseActivity {
    ImageView iv_logo;
    ImageView iv_logo_static;
    boolean isClosed = false;
//    ConfigBroadcastReceiver  smsReceiver;

    ConfigService configService = ConfigService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_splash);
        initUI();

        loadCaonfig();
    }

    private void loadCaonfig() {
        configService.init(this, new ConfigService.ConfigReadyCallBack() {
            @Override
            public void process(ConfigService serivce) {
                if (serivce.isReady()) {
                    HDUser loginUser = new HDUserCache().getLoginUser();
                    if (null != loginUser) {
                        autoLogin();
                    } else {
                        handLogin();
                    }
                } else {
                    //错误
                    handLogin();
                }
            }
        });
    }

    @Override
    public void initUI() {
        Uri data = getIntent().getData();
        if (data != null) {
            String webaddr = data.getQueryParameter("webaddr");
//			Toast.makeText(this, "第三方调用：webaddr="+webaddr, Toast.LENGTH_SHORT).show();
            HDApp.getInstance().threeAppEnterWebaddr = webaddr;
        }

        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo_static = (ImageView) findViewById(R.id.iv_logo_static);

        HDUser loginUser = new HDUserCache().getLoginUser();
        if (null != loginUser) {
            iv_logo.setVisibility(View.GONE);
            iv_logo_static.setVisibility(View.VISIBLE);

            HDDataService hdDataService = HDDataService.getInstance();
            hdDataService.start();
            AuthService.getInstance().setUserId(loginUser.userId);
//            ConfigService.getInstance().fetchURLConfig();

        } else {
            Animation trans = AnimationUtils.loadAnimation(this, R.anim.splash_icon_move);
            trans.setFillAfter(true);
            trans.setDuration(1000);
            iv_logo.startAnimation(trans);
        }

//        //注册 config下载成功广播
//        smsReceiver = new ConfigBroadcastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ConfigBroadcastReceiver.RECEIVER_KEY);
//        this.registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isClosed = true;
//        unregisterReceiver(smsReceiver);
    }

    private void startHeartbeat() {
        HDUser loginUser = new HDUserCache().getLoginUser();

        AuthService.getInstance().setUserId(loginUser.userId);
        HeartbeatService heartbeatService = HeartbeatService.getInstance();
        heartbeatService.setTimeIntervalSeconds(20);
        heartbeatService.start();
    }

    private void autoLogin() {
        new Login().exchangeNewToken(false, new Login.ExchangeNewTokenCallback() {

            @Override
            public void onExchange(boolean isSuccess, String error) {
                if (!isClosed) {
                    if (isSuccess) {// 自动登完成
                        initUser();
                    } else {//自动登陆失败 ，重新登录
                        Toast.makeText(getApplicationContext(), "请登陆", Toast.LENGTH_SHORT).show();
                        Intent intentMain = new Intent(HDSplashActivity.this, HDNavigationActivity.class);
                        startActivity(intentMain);
                        HDSplashActivity.this.finish();
                    }
                }
            }
        });
    }

    private void handLogin() {
        (new Handler()).postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intentMain = new Intent(HDSplashActivity.this, HDNavigationActivity.class);
                startActivity(intentMain);
                HDSplashActivity.this.finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        }, 2000);
    }


//    //config下载成功的时候才开始登陆，
//    public class ConfigBroadcastReceiver extends BroadcastReceiver {
//     public static final String RECEIVER_KEY="fm.hd.hdui.loadconfig";
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            boolean config=intent.getBooleanExtra("config",false);
//            if(!config){//config 下载失败或错误
//                return ;
//            }
//
//
//        }
//    }

    public void initUser() {
        final HDUser user = new HDUserCache().getLoginUser();
        if (null == user) {
            return;
        }
        CoreService.getInstance().fetchUserInfo(user.userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int error = data.getInt("error_code");
                if (error == CoreService.OK) {
                    User userLocal = (User) data.getSerializable("user");
                    if (null != userLocal) {
                        HDApp.getInstance().setCurrUser(userLocal);

                        Intent intentMain = new Intent(HDSplashActivity.this, HDBaseTabFragmentActivity.class);
                        startActivity(intentMain);
                        HDSplashActivity.this.finish();
                        startHeartbeat();
                    } else {
//                        Toast.makeText(HDSplashActivity.this, "登录失败，请重新登陆！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HDSplashActivity.this, "网络异常，请重新登陆！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}