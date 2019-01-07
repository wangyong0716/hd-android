package fm.dian.hdui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hddata.HDData;
import fm.dian.hddata.business.http.Feedback.FeedbackCallback;
import fm.dian.hddata.util.HDLog;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;

public class HDFeedbackActivity extends Activity {

    private HDLog log = new HDLog(HDFeedbackActivity.class);

    private TextView feedback;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_feedback);

        feedback = (TextView) findViewById(R.id.feedback);
    }

    public void back(View v) {
        finish();
    }

    public void save(View v) {
        String content = feedback.getText().toString();
        if (content.length() == 0) {
            Toast.makeText(getApplicationContext(), "请输入你的意见反馈！", Toast.LENGTH_SHORT).show();
            return;
        }

        FeedbackCallback callback = new FeedbackCallback() {
            public void onFeedback(boolean isSuccess) {
                String msg = isSuccess ? "意见反馈 成功！" : "意见反馈 失败";
                log.i(msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                if (isSuccess) {
                    finish();
                }
            }
        };

        new HDData().feedback(content, callback);
    }
}
