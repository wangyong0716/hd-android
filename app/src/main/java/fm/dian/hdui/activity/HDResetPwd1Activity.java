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
import fm.dian.hddata.business.http.Login.ResetPasswordCallback;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;

public class HDResetPwd1Activity extends HDBaseActivity {
    EditText et_reset1_phone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_reset_pwd1);
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

        et_reset1_phone_num = (EditText) findViewById(R.id.et_reset1_phone_num);
        et_reset1_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() == 11) {
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
                reset1();
                break;

            default:
                break;
        }
    }

    public void reset1() {
        final String phoneString = et_reset1_phone_num.getText().toString();

        if (phoneString.length() < 0) {
            new CommonDialog(HDResetPwd1Activity.this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content7));
            return;
        }
        new Login().resetPassword1(phoneString, new ResetPasswordCallback() {
            public void onReset(boolean isSuccess, String error) {
                String msg = "resetPassword1: " + isSuccess + "  error: " + error;
//				Toast.makeText(HDResetPwd1Activity.this, msg, Toast.LENGTH_SHORT).show();
                HDUi_log.i(msg);
                if (isSuccess) {
                    Intent resetPwdIntent = new Intent(HDResetPwd1Activity.this, HDResetPwd2Activity.class);
                    resetPwdIntent.putExtra("phoneNum", phoneString);
                    HDResetPwd1Activity.this.startActivity(resetPwdIntent);
                }
            }
        });
    }
}
