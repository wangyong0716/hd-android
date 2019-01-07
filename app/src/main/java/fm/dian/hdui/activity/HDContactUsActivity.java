package fm.dian.hdui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;

public class HDContactUsActivity extends HDBaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_contact_us);
        initUI();
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        setActionBarTitle("联系我们");
        tv_common_action_bar_right.setVisibility(View.GONE);
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
