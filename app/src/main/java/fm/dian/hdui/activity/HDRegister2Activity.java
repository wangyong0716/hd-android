package fm.dian.hdui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import fm.dian.hddata.business.http.Login;
import fm.dian.hddata.business.http.Login.LoginCallback2;
import fm.dian.hddata.business.http.Login.SignupCallback;
import fm.dian.hddata.business.model.HDDataTypeEnum.HDUserLoginType;
import fm.dian.hddata.business.model.HDLoginUser;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;

/**
 * Created by mac on 14-4-2.
 */
public class HDRegister2Activity extends HDBaseActivity {
    EditText et_code;
    TextView tv_phone_num;
    Button btn_send_code;

    private String phoneNum;
    private String phonePwd;

    private int time = 60;
    private Timer timer = new Timer();
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_reset_pwd2);
        initUI();
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        setActionBarTitle("注册信息（2/3）");
        tv_common_action_bar_right.setText("下一步");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setEnabled(false);
        tv_common_action_bar_right.setOnClickListener(this);

        btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(this);
        et_code = (EditText) findViewById(R.id.et_code);
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() > 0) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
            }
        });
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        phoneNum = getIntent().getStringExtra("phoneNum");
        phonePwd = getIntent().getStringExtra("phonePwd");
        tv_phone_num.setText("+86 " + phoneNum);
        checkBtnCode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                signupConfirm();
                break;
            case R.id.btn_send_code:
                boolean enabled = btn_send_code.isEnabled();
                if (enabled) {
                    checkBtnCode();
                }
                getCode();
                break;

            default:
                break;
        }
    }

    private void getCode() {
        if (phoneNum.length() < 11) {
            new CommonDialog(this, ButtonType.oneButton, null, "请输入正确的手机号码");
            return;
        }

        if (phonePwd.length() < 6) {
            new CommonDialog(this, ButtonType.oneButton, null, "密码长度不能少于6位");
            return;
        }

        HDLoginUser loginUser = new HDLoginUser();
        loginUser.loginType = HDUserLoginType.phone;
        loginUser.phoneNumber = phoneNum;
        loginUser.password = phonePwd;

        new Login().signup(loginUser, new SignupCallback() {
            public void onSignup(boolean isSuccess, String error) {
                String msg = "Signup: " + isSuccess + "  error: " + error;
//				Toast.makeText(HDRegister2Activity.this, msg, Toast.LENGTH_SHORT).show();
                HDUi_log.i(msg);
            }
        });
    }

    public void signupConfirm() {
        if (phoneNum.length() < 11) {
            new CommonDialog(this, ButtonType.oneButton, null, "请输入正确的手机号码");
            return;
        }

        if (phonePwd.length() < 6) {
            new CommonDialog(this, ButtonType.oneButton, null, "密码长度不能少于6位");
            return;
        }


        String confirmationCode = et_code.getText().toString();

        if (confirmationCode.length() < 0) {
            String msg = "请输入正确的Code！";
            Toast.makeText(HDRegister2Activity.this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        HDLoginUser loginUser = new HDLoginUser();
        loginUser.loginType = HDUserLoginType.phone;
        loginUser.phoneNumber = phoneNum;
        loginUser.password = phonePwd;
        loginUser.confirmationCode = confirmationCode;

        new Login().signupConfirm(loginUser, new SignupCallback() {
            public void onSignup(boolean isSuccess, String error) {
                String msg = "signupConfirm: " + isSuccess + "  error: " + error;
                HDUi_log.i(msg);
                if (isSuccess) {
                    login();
                } else {
//					Toast.makeText(HDRegister2Activity.this,"注册失败"+ msg, Toast.LENGTH_SHORT).show();
                    new CommonDialog(HDRegister2Activity.this, ButtonType.TowButton, null, error);
                }
            }
        });
    }


    public void login() {
        HDLoginUser loginUser = new HDLoginUser();
        loginUser.loginType = HDUserLoginType.phone;
        loginUser.phoneNumber = phoneNum;
        loginUser.password = phonePwd;

        new Login().loginByPhoneNumber(loginUser, new LoginCallback2() {
            public void onLogin(boolean isSuccess, int errorCode, HDUser user) {
                String msg = "Signup: " + isSuccess + "  user: " + user;
//				Toast.makeText(HDRegister2Activity.this, msg, Toast.LENGTH_SHORT).show();
                HDUi_log.i(msg);
                if (isSuccess) {
                    Intent register3 = new Intent(HDRegister2Activity.this, HDRegister3Activity.class);
                    startActivity(register3);
                } else {
                    String errorCodeString = Login.getErrorCodeString(errorCode);
                    HDUi_log.i(errorCodeString);
                    Toast.makeText(HDRegister2Activity.this, errorCodeString, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void checkBtnCode() {
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (time <= 0) {
                            btn_send_code.setEnabled(true);
                            btn_send_code.setBackgroundResource(R.drawable.btn_common_red_bg);
                            btn_send_code.setTextColor(Color.rgb(255, 255, 255));
                            btn_send_code.setText("重新获取");
                            task.cancel();
                        } else {
                            btn_send_code.setEnabled(false);
                            btn_send_code.setText("重发验证码（" + time + "）");
                            btn_send_code.setBackgroundResource(R.drawable.btn_common_white_bg);
                            btn_send_code.setTextColor(Color.rgb(125, 125, 125));
                        }
                        time--;
                    }
                });
            }
        };

        time = 60;
        timer.schedule(task, 0, 1000);
    }


}
