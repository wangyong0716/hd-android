package fm.dian.hdui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;

public class HDExplainActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_explain);

    }

    public void back(View v) {
        finish();
    }
}
