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

import java.util.Timer;
import java.util.TimerTask;

import fm.dian.hddata.business.http.Login;
import fm.dian.hddata.business.http.Login.ResetPasswordCallback;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;

public class HDResetPwd2Activity extends HDBaseActivity {
    EditText et_code;
    TextView tv_phone_num;
    Button btn_send_code;

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
        tv_common_action_bar_right.setText("下一步");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 10, 0);
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
                if (null != s.toString().trim() && s.toString().length() == 6) {
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setEnabled(false);
                }
            }
        });
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        String phoneNum = getIntent().getStringExtra("phoneNum");
        tv_phone_num.setText("+86 " + phoneNum);
        checkBtnCode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                boolean enabled = btn_send_code.isEnabled();
                if (enabled) {
                    checkBtnCode();
                }
                reset2();
                break;
            case R.id.btn_send_code:
                checkBtnCode();
                break;

            default:
                break;
        }
    }

    public void reset2() {
        final String phoneNum = getIntent().getStringExtra("phoneNum");
        if (phoneNum == null || phoneNum.length() < 0) {
            new CommonDialog(this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content6));
            return;
        }

        String confirmationCode = et_code.getText().toString();

        if (confirmationCode.length() < 0) {
            new CommonDialog(this, ButtonType.oneButton, null, getString(R.string.act_reset_new_pwd3_dialog_content8));
            return;
        }

        new Login().resetPassword2(phoneNum, confirmationCode, new ResetPasswordCallback() {
            public void onReset(boolean isSuccess, String error) {
                String msg = "resetPassword2: " + isSuccess + "  error: " + error;
//				Toast.makeText(HDResetPwd2Activity.this,msg, Toast.LENGTH_SHORT).show();
                HDUi_log.i(msg);
                if (isSuccess) {
                    Intent resetPwdIntent = new Intent(HDResetPwd2Activity.this, HDResetPwd3Activity.class);
                    resetPwdIntent.putExtra("phoneNum", phoneNum);
                    HDResetPwd2Activity.this.startActivity(resetPwdIntent);
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
