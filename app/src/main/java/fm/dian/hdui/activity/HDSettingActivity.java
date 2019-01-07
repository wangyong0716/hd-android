package fm.dian.hdui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import fm.dian.hddata.HDData;
import fm.dian.hddata.business.service.core.user.HDUserCache;
import fm.dian.hddata.util.HDLog;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.util.ActivityCheck;
import fm.dian.hdui.util.HDUiLog;
import fm.dian.hdui.util.SysTool;

public class HDSettingActivity extends HDBase2Activity {

    private HDLog log = new HDLog(HDSettingActivity.class);
    private TextView version;
    private Button video;

    private int hideCount = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_setting);

        version = (TextView) findViewById(R.id.version);
        version.setText(SysTool.getAppVersion(this));
//        version.setText("内测版");
    }

    public void back(View v) {
        finish();
    }

    public void hide(View v) {
        hideCount++;
        if (hideCount == 5) {
            hideCount = 0;
            final Dialog builder = new Dialog(this, R.style.HDDialog);
            builder.setContentView(R.layout.activity_setting_hide_dialog);
            builder.setCanceledOnTouchOutside(true);
            builder.show();

            if (log.isDebugging()) {
                builder.findViewById(R.id.gender11).setVisibility(View.VISIBLE);
            } else {
                builder.findViewById(R.id.gender11).setVisibility(View.INVISIBLE);
            }

            if (log.isDebugNetwork()) {
                builder.findViewById(R.id.gender01).setVisibility(View.VISIBLE);
            } else {
                builder.findViewById(R.id.gender01).setVisibility(View.INVISIBLE);
            }

            if (new HDData().isDebugData()) {
                builder.findViewById(R.id.gender21).setVisibility(View.VISIBLE);
            } else {
                builder.findViewById(R.id.gender21).setVisibility(View.INVISIBLE);
            }

            if (new HDUiLog().isDebugging()) {
                builder.findViewById(R.id.gender31).setVisibility(View.VISIBLE);
            } else {
                builder.findViewById(R.id.gender31).setVisibility(View.INVISIBLE);
            }

            builder.findViewById(R.id.gender1).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    log.debug(!log.isDebugging());
                    if (log.isDebugging()) {
                        builder.findViewById(R.id.gender11).setVisibility(View.VISIBLE);
                    } else {
                        builder.findViewById(R.id.gender11).setVisibility(View.INVISIBLE);
                    }
                }
            });

            builder.findViewById(R.id.gender0).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    log.debugNetwork(!log.isDebugNetwork());
                    if (log.isDebugNetwork()) {
                        builder.findViewById(R.id.gender01).setVisibility(View.VISIBLE);
                    } else {
                        builder.findViewById(R.id.gender01).setVisibility(View.INVISIBLE);
                    }
                }
            });

            builder.findViewById(R.id.gender3).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    HDUiLog log = new HDUiLog();
                    log.debug(!log.isDebugging());
                    if (log.isDebugging()) {
                        builder.findViewById(R.id.gender31).setVisibility(View.VISIBLE);
                    } else {
                        builder.findViewById(R.id.gender31).setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }


    public void feedback(View v) {
        Intent intent = new Intent(getApplicationContext(), HDFeedbackActivity.class);
        startActivity(intent);
    }

    public void contactUs(View v) {
        Intent intent = new Intent(getApplicationContext(), HDContactUsActivity.class);
        startActivity(intent);
    }

    public void explain(View v) {
        Intent intent = new Intent(getApplicationContext(), HDExplainActivity.class);
        startActivity(intent);
    }

    public void video(View v) {
//        Intent intent = new Intent(getApplicationContext(), DemoActivity.class);
//        startActivity(intent);
    }

    public void logout(View v) {
        HDBaseTabFragmentActivity.self.appLogout();
        finish();

        HDUserCache userCache = new HDUserCache();
        userCache.removeLoginUser();
    }

}
