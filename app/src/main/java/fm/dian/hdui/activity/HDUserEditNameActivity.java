package fm.dian.hdui.activity;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hddata.util.HDLog;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.ActivityCheck;

public class HDUserEditNameActivity extends HDBase2Activity {

    private HDLog log = new HDLog(HDUserEditNameActivity.class);

    private TextView userName;
    private CoreService coreService = CoreService.getInstance();
    private User userLocal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_user_edit_name);

        userLocal = HDApp.getInstance().getCurrUser();

        if (userLocal == null) {
            log.e("LoginUser is null.");
            Toast.makeText(getApplicationContext(), "没有登录用户", Toast.LENGTH_SHORT).show();
            finish();
        }

        userName = (TextView) findViewById(R.id.userName);

        setUser(userLocal);

    }

    private void setUser(final User user) {
        try {
            if (user == null) {
                log.e("user is null.");
                return;
            }

            userName.setText(user.getNickname());

            userName.requestFocus();

            Selection.setSelection((Spannable) userName.getText(), userName.getText().length());
        } catch (Throwable e) {
            log.e("setUser [ERROR]: " + e.getMessage(), e);
        }
    }

    public void back(View v) {
        finish();
    }

    public void save(View v) {

        String nickname = userName.getText().toString().trim();

        nickname = nickname.replaceAll("\n", " ");

        userLocal.setNickname(nickname.trim());

        coreService.updateUserInfo(userLocal, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    User user = (User) data.getSerializable("user");
                    HDApp.getInstance().setCurrUser(user);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
