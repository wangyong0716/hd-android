package fm.dian.hdui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import fm.dian.hddata.business.http.Login;
import fm.dian.hddata.business.http.Login.ResetPasswordCallback;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.view.CommonDialog.DialogClickListener;
import fm.dian.hdui.wxapi.WXEntryActivity;

public class HDResetPwd3Activity extends HDBaseActivity {
    EditText et_new_pwd;
    EditText et_new_pwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_reset_pwd3);
        initUI();
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setText("下一步");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 10, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setEnabled(false);
        tv_common_action_bar_right.setOnClickListener(this);

        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_new_pwd2 = (EditText) findViewById(R.id.et_new_pwd2);
        et_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() > 20) {
                    Toast.makeText(HDResetPwd3Activity.this, "密码长度6-20", Toast.LENGTH_SHORT).show();
                    et_new_pwd.setText(s.toString().subSequence(0, 20));
                    return;
                }

                String et2String = et_new_pwd2.getText().toString();
                if (null != s.toString().trim() && s.toString().length() >= 6 && null != et2String.trim() && et2String.length() >= 6) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
            }
        });
        et_new_pwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() > 20) {
                    Toast.makeText(HDResetPwd3Activity.this, "密码长度6-20", Toast.LENGTH_SHORT).show();
                    et_new_pwd2.setText(s.toString().subSequence(0, 20));
                    return;
                }

                String etString = et_new_pwd.getText().toString();
                if (null != s.toString().trim() && s.toString().length() >= 6 && null != etString.trim() && etString.length() >= 6) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                reset3();
                break;

            default:
                break;
        }
    }


    public void reset3() {
        final String paramPhoneNum = getIntent().getStringExtra("phoneNum");

        if (paramPhoneNum == null || paramPhoneNum.length() < 0) {
            new CommonDialog(this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content6));
            return;
        }

        final String pwdString = et_new_pwd.getText().toString();
        String pwdString2 = et_new_pwd2.getText().toString();
        if (null != pwdString && pwdString.length() < 6) {
            new CommonDialog(this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content1));
            return;
        }
        if (!pwdString.equals(pwdString2)) {
            new CommonDialog(this, ButtonType.oneButton, null, "两次密码不一致");
            return;
        }

        if (pwdString.trim() != null && pwdString.equals(pwdString2)) {
            new Login().resetPassword3(paramPhoneNum, pwdString, new ResetPasswordCallback() {
                public void onReset(boolean isSuccess, String error) {
                    String msg = "resetPassword3: " + isSuccess + "  error: " + error;
//					Toast.makeText(HDResetPwd3Activity.this,msg, Toast.LENGTH_SHORT).show();
                    HDUi_log.i(msg);
                    if (isSuccess) {
                        phoneLogin(paramPhoneNum, pwdString);
                    }
                }
            });
        } else {
            new CommonDialog(this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content2), null);
            return;
        }
    }


    private void phoneLogin(String phoneString, String pwdString) {
//		HDLoginUser loginUser = new HDLoginUser();
//		loginUser.loginType = HDUserLoginType.PhoneNumber;
//		loginUser.phoneNumber = phoneString;
//		loginUser.password = pwdString;
//
//		new Login().loginByPhoneNumber(loginUser, new LoginCallback2() {
//			public void onLogin(boolean isSuccess,int errorCode ,HDUser user) {
//				String msg = "Signup: " + isSuccess + "  user: " + user;
////				Toast.makeText(HDResetPwd3Activity.this, msg, Toast.LENGTH_SHORT).show();
//				HDUi_log.i(msg);
//				if(isSuccess){
//					Intent intentMain = new Intent(HDResetPwd3Activity.this,HDBaseTabFragmentActivity.class);
//					startActivity(intentMain);
//					HDResetPwd3Activity.this.finish();
//				}else{
//					Toast.makeText(HDResetPwd3Activity.this,"登录失败"+ msg, Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
        //重置密码以后跳转到登录页面，让用户主动登录
        new CommonDialog(this, ButtonType.TowButton, new DialogClickListener() {

            @Override
            public void doPositiveClick() {
                Intent intentMain = new Intent(HDResetPwd3Activity.this, WXEntryActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentMain);
                finish();
            }

            @Override
            public void doNegativeClick() {
            }
        }, "密码重置成功", "取消", "登录");
    }
}
