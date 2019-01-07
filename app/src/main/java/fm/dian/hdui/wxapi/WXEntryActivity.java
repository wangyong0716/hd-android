package fm.dian.hdui.wxapi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fm.dian.hddata.business.model.HDDataTypeEnum.HDUserLoginType;
import fm.dian.hddata.business.model.HDLoginUser;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.HDDataService;
import fm.dian.hdservice.HeartbeatService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDNavigationActivity;
import fm.dian.hdui.activity.HDResetPwd1Activity;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.net.HDAsycTask;
import fm.dian.hdui.net.HDAsycTask.HDAsyncHandler;
import fm.dian.hdui.net.NetUtil;
import fm.dian.hdui.net.NetworkUtils;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.net.oldinterface.Login;
import fm.dian.hdui.sdk.qq.QQBaseUIListener;
import fm.dian.hdui.util.PreferenceHelper;
import fm.dian.service.core.HDTableUser;

/**
 * Created by mac on 14-4-2.
 */
public class WXEntryActivity extends HDBaseActivity implements
        QQBaseUIListener.AuthCompleteListener, IWXAPIEventHandler {
    private Tencent mTencent;
    private QQBaseUIListener uiListener;
    private IWXAPI wxApi;

    LinearLayout ll_login_wx;
    LinearLayout ll_login_qq;
    Button btn_phone_login;
    EditText et_phone;
    EditText et_pwd;

    Drawable drawLeft = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_login);
        initUI();
        regToWX();
    }

    private void regToWX() {
        wxApi = WXAPIFactory.createWXAPI(WXEntryActivity.this, HongdianConstants.WX_ID, true);
        wxApi.handleIntent(getIntent(), this);

    }

    @Override
    public void onStart() {
        //注册广播接收器
        wxApi.registerApp(HongdianConstants.WX_ID);
        super.onStart();
    }

    @Override
    protected void onPause() {
        //取消广播接收器
        wxApi.unregisterApp();
        super.onPause();
    }

    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setVisibility(View.GONE);

        btn_phone_login = (Button) this.findViewById(R.id.btn_phone_login);
        btn_phone_login.setEnabled(false);
        ll_login_wx = (LinearLayout) this.findViewById(R.id.ll_login_wx);
        ll_login_qq = (LinearLayout) this.findViewById(R.id.ll_login_qq);

        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_phone.setCompoundDrawablePadding(20);
        et_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    drawLeft = getResources().getDrawable(R.drawable.et_left_drawable_phone);
                } else {
                    drawLeft = getResources().getDrawable(R.drawable.et_left_drawable_phone_tan);
                }
                drawLeft.setBounds(0, 0, drawLeft.getMinimumWidth(), drawLeft.getMinimumHeight());
                et_phone.setCompoundDrawables(drawLeft, null, null, null);
            }
        });
        et_pwd = (EditText) this.findViewById(R.id.et_pwd);
        et_pwd.setCompoundDrawablePadding(20);
        et_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    drawLeft = getResources().getDrawable(R.drawable.et_left_drawable_pwd_p);
                } else {
                    drawLeft = getResources().getDrawable(R.drawable.et_left_drawable_pwd);
                }
                drawLeft.setBounds(0, 0, drawLeft.getMinimumWidth(), drawLeft.getMinimumHeight());
                et_pwd.setCompoundDrawables(drawLeft, null, null, null);
            }
        });
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() > 0) {
                    btn_phone_login.setEnabled(true);
                    btn_phone_login.setTextColor(getResources().getColor(R.color.color_white));
                    btn_phone_login.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector);
                } else {
                    btn_phone_login.setEnabled(false);
                    btn_phone_login.setTextColor(getResources().getColor(R.color.act_login_btn_text_color_unable));
                    btn_phone_login.setBackgroundResource(R.drawable.btn_common_white_red_bg_selector);
                }
            }
        });


        this.findViewById(R.id.tv_forget_pwd).setOnClickListener(this);

        btn_phone_login.setOnClickListener(this);
        ll_login_wx.setOnClickListener(this);
        ll_login_qq.setOnClickListener(this);
    }

    /**
     * qq登录的auth验证完成的回调
     */
    public void authComplete() {
        HDUi_log.i("qq authComplete" + mTencent.getAccessToken() + " "
                + mTencent.getOpenId());
        PreferenceHelper.writeQQAccessToken(WXEntryActivity.this, mTencent);
        System.out.println("qq token==" + mTencent.toString());

        new HDAsycTask(this, new HDAsyncHandler() {

            @Override
            public String doInBackground(Void... params) {
                try {
                    Map<String, String> mParams = new HashMap<String, String>();
                    mParams.put("oauth_consumer_key", HongdianConstants.QQ_APPID);
                    mParams.put("access_token", mTencent.getAccessToken());
                    mParams.put("openid", mTencent.getOpenId());

                    String subJson = NetUtil.getRequest(
                            HongdianConstants.QQ_USER_INFO_URL, mParams,
                            WXEntryActivity.this);
                    if (null != subJson) {
                        try {
                            JSONObject jsonObject = new JSONObject(subJson);

                            HDLoginUser u = new HDLoginUser();
                            u.loginType = HDUserLoginType.qq;
                            u.nickname = jsonObject.getString("nickname");
                            u.account_id = mTencent.getOpenId();
                            u.gender = jsonObject.getString("gender").equals(
                                    "男") ? HDTableUser.HDUser.GenderType.MALE
                                    : HDTableUser.HDUser.GenderType.FEMALE;
                            u.avatar = jsonObject.getString("figureurl_qq_2");

                            new Login().loginByAccountId(u, new Login.LoginCallback() {
                                public void onLogin(boolean isSuccess,
                                                    HDUser user) {
                                    if (isSuccess) {
                                        appLogin();
                                    }
//									Toast.makeText(getApplicationContext(),isSuccess ? "Login 成功: "+ user.userId : "Login 失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String onPostExecute(String result) {
                return null;
            }
        }).execute();

    }

    private void qqLogin() {
        if (null == uiListener) {
            uiListener = new QQBaseUIListener(WXEntryActivity.this,
                    WXEntryActivity.this);
        }
        if (null == mTencent) {
            mTencent = Tencent.createInstance(HongdianConstants.QQ_APPID,
                    this.getApplicationContext());
        }
        mTencent.login(WXEntryActivity.this, "all", uiListener);

    }

    private void wxLogin() {
        if (wxApi.isWXAppInstalled()) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "hongdian";
            wxApi.sendReq(req);
        } else {
            Toast.makeText(WXEntryActivity.this, "not install weixin",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("onReq", "onReq");
//		Toast.makeText(this, "openid = " + req.openId, Toast.LENGTH_SHORT)
//				.show();

        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("onResp", "onResp");
        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            String code = ((SendAuth.Resp) resp).code;
            authWeixin(code);
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phone_login:
                phoneLogin();
                break;
            case R.id.ll_login_wx:
                wxLogin();
                break;
            case R.id.ll_login_qq:
                qqLogin();
                break;
            case R.id.tv_forget_pwd:
                Intent resetPwdIntent = new Intent(this, HDResetPwd1Activity.class);
                WXEntryActivity.this.startActivity(resetPwdIntent);
                break;
            default:
                break;
        }
    }

    private void phoneLogin() {
        if (0 == NetworkUtils.getNetworkState(this)) {
            Toast.makeText(WXEntryActivity.this, "请设置网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        String phoneString = et_phone.getText().toString();

        if (phoneString.length() < 0) {
            String msg = "请输入正确的Phone！";
            Toast.makeText(WXEntryActivity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        String pwdString = et_pwd.getText().toString();

        if (pwdString.length() < 0) {
            String msg = "请输入正确的PWD！";
            Toast.makeText(WXEntryActivity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        HDLoginUser loginUser = new HDLoginUser();
        loginUser.loginType = HDUserLoginType.phone;
        loginUser.phoneNumber = phoneString;
        loginUser.password = pwdString;

        new Login().loginByPhoneNumber(loginUser, new Login.LoginCallback2() {

            public void onLogin(boolean isSuccess, int errorCode, HDUser user) {
                if (isSuccess) {
                    appLogin();
                } else {
                    String errorCodeString = Login.getErrorCodeString(errorCode);
                    Toast.makeText(WXEntryActivity.this, "登录失败:" + errorCodeString, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void appLogin() {
        HDDataService hdDataService = HDDataService.getInstance();
        hdDataService.start();

        hideKeyBoard();

        initUser();
    }

    private void authWeixin(final String code) {
        HDUi_log.i("weixin code=" + code);
//		Toast.makeText(this, "code = " + code, Toast.LENGTH_SHORT).show();

        if (null != code && !"".equals(code)) {
            new HDAsycTask(this, new HDAsyncHandler() {

                @Override
                public String doInBackground(Void... params) {
                    try {
                        Map<String, String> mParams = new HashMap<String, String>();
                        mParams.put("appid", HongdianConstants.WX_ID);
                        mParams.put("secret", HongdianConstants.WX_SECRET);
                        mParams.put("code", code);
                        mParams.put("grant_type", "authorization_code");
                        String subResp = NetUtil.getRequest(
                                HongdianConstants.WX_ACCESS_URL, mParams,
                                WXEntryActivity.this);

                        JSONObject subJsonObj = new JSONObject(subResp);

                        String openid = subJsonObj.getString("openid");
                        String accessToken = subJsonObj
                                .getString("access_token");
                        Map<String, String> subParams = new HashMap<String, String>();

                        subParams.put("access_token", accessToken);
                        subParams.put("openid", openid);

                        String subJson = NetUtil.getRequest(
                                HongdianConstants.WX_USERINFO_URL, subParams,
                                WXEntryActivity.this);

                        JSONObject resultJsonObj = new JSONObject(subJson);
                        HDLoginUser u = new HDLoginUser();
                        u.loginType = HDUserLoginType.weixin;
                        u.nickname = resultJsonObj.getString("nickname");
                        u.account_id = resultJsonObj.getString("unionid");
                        u.gender = resultJsonObj.getString("sex").equals("1") ? HDTableUser.HDUser.GenderType.MALE
                                : HDTableUser.HDUser.GenderType.FEMALE;
                        u.avatar = resultJsonObj.getString("headimgurl");

                        new Login().loginByAccountId(u, new Login.LoginCallback() {
                            public void onLogin(boolean isSuccess, HDUser user) {
                                if (isSuccess) {
                                    appLogin();
                                }
                            }
                        });
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public String onPostExecute(String result) {

                    return null;
                }
            }).execute();
        }
    }

    public void initUser() {
        Log.i("AAA","initUser");
        final HDUser user = new HDUserCache().getLoginUser();
        if (null == user) {
            return;
        }
        Log.i("AAA","initUser->userId="+user.userId);
        CoreService.getInstance().fetchUserInfo(user.userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                Log.i("AAA","initUser->data="+data);
                int error = data.getInt("error_code");
                if (error == CoreService.OK) {
                    Log.i("AAA","ok");
                    User userLocal = (User) data.getSerializable("user");
                    if (null != userLocal) {
                        Log.i("AAA","userLocal="+userLocal);
                        HDApp.getInstance().setCurrUser(userLocal);

                        Intent intentMain = new Intent(WXEntryActivity.this, HDBaseTabFragmentActivity.class);
                        intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentMain);
                        WXEntryActivity.this.finish();

                        if (HDNavigationActivity.self != null) {
                            HDNavigationActivity.self.finish();
                        }

                        AuthService.getInstance().setUserId(user.userId);
                        HeartbeatService heartbeatService = HeartbeatService.getInstance();
                        heartbeatService.setTimeIntervalSeconds(20);
                        heartbeatService.start();
                    } else {
//                        Toast.makeText(HDSplashActivity.this, "登录失败，请重新登陆！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WXEntryActivity.this, "网络异常，请重新登陆！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
