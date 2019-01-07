package fm.dian.hdui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import fm.dian.hddata.business.http.Login;
import fm.dian.hddata.business.http.Login.SignupCallback;
import fm.dian.hddata.business.model.HDDataTypeEnum.HDUserLoginType;
import fm.dian.hddata.business.model.HDLoginUser;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.view.CommonDialog.DialogClickListener;
import fm.dian.hdui.wxapi.WXEntryActivity;

/**
 * Created by mac on 14-4-2.
 */
public class HDRegister1Activity extends HDBaseActivity {
    EditText et_phone;
    EditText et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_register);
        initUI();
    }


    public void initUI() {
        super.initActionBar(this);
        setActionBarTitle("注册信息（1/3）");
        tv_common_action_bar_right.setText("下一步");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setEnabled(false);
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_pwd = (EditText) this.findViewById(R.id.et_pwd);

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                String strPw = et_pwd.getText().toString().trim();
                if ((str != null && str.trim().length() == 11) && (strPw != null && strPw.length() > 0)) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
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
                String str = s.toString();
                String strP = et_phone.getText().toString().trim();
                if (str != null && str.trim().length() > 0 && (strP != null && strP.length() == 11)) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
            }
        });
//		btn_get_code.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//		case R.id.btn_get_code:
//			registerPhone();
//			break;
            case R.id.tv_common_action_bar_right:
//			signupConfirm();
                registerPhone();
                break;
            default:
                break;
        }
    }

    private void registerPhone() {
        final String phoneString = et_phone.getText().toString();

        if (phoneString.length() != 11) {
            new CommonDialog(this, ButtonType.oneButton, null, "请输入正确的手机号码");
            return;
        }

        final String pwdString = et_pwd.getText().toString();

        if (pwdString.length() < 6) {
            new CommonDialog(this, ButtonType.oneButton, null, "密码长度不能少于6位");
            return;
        }

        new CommonDialog(this, ButtonType.TowButton, new DialogClickListener() {

            @Override
            public void doPositiveClick() {
                HDLoginUser loginUser = new HDLoginUser();
                loginUser.loginType = HDUserLoginType.phone;
                loginUser.phoneNumber = phoneString;
                loginUser.password = pwdString;

                new Login().signup(loginUser, new SignupCallback() {
                    public void onSignup(boolean isSuccess, String error) {
                        String msg = "Signup: " + isSuccess + "  error: " + error;
                        HDUi_log.i(msg);
                        if (isSuccess) {
                            Intent intent = new Intent(HDRegister1Activity.this, HDRegister2Activity.class);
                            intent.putExtra("phoneNum", phoneString);
                            intent.putExtra("phonePwd", pwdString);
                            startActivity(intent);
                        } else {
                            new CommonDialog(HDRegister1Activity.this, ButtonType.TowButton, new DialogClickListener() {

                                @Override
                                public void doPositiveClick() {
                                    Intent intentLogin = new Intent(HDRegister1Activity.this, WXEntryActivity.class);
                                    startActivity(intentLogin);
                                    HDRegister1Activity.this.finish();
                                }

                                @Override
                                public void doNegativeClick() {
                                }
                            }, "手机号码不可用或已被注册", "取消", "登录");
                        }
                    }
                });
            }

            @Override
            public void doNegativeClick() {
                et_phone.requestFocus();
            }
        }, "确认手机号码\n(+86)" + phoneString, "修改", "确认");


    }


//	public void signupConfirm() {
//		String phoneString = et_phone.getText().toString();
//
//		if (phoneString.length() < 0) {
//			String msg = "请输入正确的Phone！";
//			Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		String pwdString = et_pwd.getText().toString();
//
//		if (pwdString.length() < 0) {
//			String msg = "请输入正确的PWD！";
//			Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		
//		String confirmationCode = et_code.getText().toString();
//		
//		if (confirmationCode.length() < 0) {
//			String msg = "请输入正确的Code！";
//			Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//			return ;
//		}
//		
//		HDLoginUser loginUser = new HDLoginUser();
//		loginUser.loginType = HDUserLoginType.PhoneNumber;
//		loginUser.phoneNumber = phoneString;
//		loginUser.password = pwdString;
//		loginUser.confirmationCode = confirmationCode;
//		
//		new Login().signupConfirm(loginUser, new SignupCallback() {
//			public void onSignup(boolean isSuccess, String error) {
//				String msg = "signupConfirm: "+isSuccess+"  error: "+error;
//				Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//				if (isSuccess) {
//					login();
//				}else{
//					Toast.makeText(HDRegister1Activity.this,"注册失败"+ msg, Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//	}

//	public void login() {
//		String phoneString = et_phone.getText().toString();
//		
//		if (phoneString.length() < 0) {
//			String msg = "请输入正确的Phone！";
//			Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//			return ;
//		}
//		
//		String pwdString = et_pwd.getText().toString();
//		
//		if (pwdString.length() < 0) {
//			String msg = "请输入正确的PWD！";
//			Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//			return ;
//		}
//		
//		HDLoginUser loginUser = new HDLoginUser();
//		loginUser.loginType = HDUserLoginType.PhoneNumber;
//		loginUser.phoneNumber = phoneString;
//		loginUser.password = pwdString;
//		
//		new Login().loginByPhoneNumber(loginUser, new LoginCallback() {
//			public void onLogin(boolean isSuccess, HDUser user) {
//				String msg = "Signup: "+isSuccess+"  user: "+user;
//				Toast.makeText(HDRegister1Activity.this, msg, Toast.LENGTH_SHORT).show();
//				if(isSuccess){
//					Intent intentMain = new Intent(HDRegister1Activity.this,HDBaseTabFragmentActivity.class);
//					startActivity(intentMain);
//					HDRegister1Activity.this.finish();
//				}
//			}
//		});
//	}

}
