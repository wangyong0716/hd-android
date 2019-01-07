package fm.dian.hdui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;

/**
 * Created by song on 2015/4/2.
 */
public class HDBlackboardTxt extends HDBaseActivity {
    public static final int RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS = 3;

    EditText et_blackboard_txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_blackboard_txt);
        initUI();
    }

    @Override
    public void initUI() {
        initActionBar(this);
        tv_common_action_bar_right.setVisibility(View.VISIBLE);
        tv_common_action_bar_right.setTextColor(Color.RED);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setBackgroundResource(0);
        tv_common_action_bar_right.setText("发送");
        tv_common_action_bar_right.setOnClickListener(this);

        setActionBarTitle("文字");
        et_blackboard_txt = (EditText) findViewById(R.id.et_blackboard_txt);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                String txt = et_blackboard_txt.getText().toString().trim();
                if (txt == "") {
                    return;
                }
                Intent intentResult = new Intent();
                intentResult.putExtra("txt", txt);
                setResult(RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS, intentResult);
                hideKeyBoard();
                finish();
                break;
            default:
                break;
        }
    }

    public void hideKeyBoard() {
        // 收起软键盘
        InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
