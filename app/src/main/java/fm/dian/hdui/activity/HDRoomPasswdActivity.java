package fm.dian.hdui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fm.dian.hddata.util.HDLog;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;

public class HDRoomPasswdActivity extends HDBase2Activity {

    public static final Logger loger = Logger.getLogger(HDRoomPasswdActivity.class);

    public static final int REQUEST_CODE_ROOM_PWD = 2;
    public static final int RESULT_CODE_ROOM_PWD = 5;

    public static final String ROOM_PWD = "ROOM_PWD";

    private HDLog log = new HDLog(HDRoomPasswdActivity.class);

    private EditText passwdView;
    private View v1;
    private View v2;
    private View v3;
    private View v4;
    private TextView p1;
    private TextView p2;
    private TextView p3;
    private TextView p4;

    AuthService authService = AuthService.getInstance();
    LiveService liveService = LiveService.getInstance();
    ActionAuthService actionAuthService = ActionAuthService.getInstance();
    MediaService mediaService = MediaService.getInstance();
    Live currLive;
    boolean fromSetting = false;
    long liveId = 0;
    boolean isAdmin = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        liveId = getIntent().getLongExtra("liveId", 0);
        boolean isLocked = getIntent().getBooleanExtra("isLocked", false);
        fromSetting = getIntent().getBooleanExtra("fromSetting", false);
        String currPwd = HDApp.getInstance().getCurrPwd();

        long currRoomId = HDApp.getInstance().getCurrRoomId();
        long currUerId = new HDUserCache().getLoginUser().userId;
        boolean isAdmin = actionAuthService.isRole(currUerId, currRoomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(currUerId, currRoomId, 0l, AuthActionRequest.UserAuthType.UserOwner);

        if (!isLocked && !fromSetting) {
            enterChatAct("");
        } else if ((isOwner || isAdmin) && !fromSetting) {//有锁没输入过密码，但是我是管理员或房间主
            adminJoinLive();
        } else if (isLocked && !fromSetting && !"".equals(currPwd)) {//有锁，但是已经输入过密码
            enterChatAct(currPwd);
        }

        setContentView(R.layout.activity_room_passwd);

        passwdView = (EditText) findViewById(R.id.passwdView);

        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);
        v3 = findViewById(R.id.v3);
        v4 = findViewById(R.id.v4);
        p1 = (TextView) findViewById(R.id.p1);
        p2 = (TextView) findViewById(R.id.p2);
        p3 = (TextView) findViewById(R.id.p3);
        p4 = (TextView) findViewById(R.id.p4);

        passwd("");

        passwdView.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int c) {
                log.i("passwdView: " + s);
                passwd(s);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        loadLiveData();
    }

    private void adminJoinLive() {
        long[] lives = new long[1];
        lives[0] = liveId;
        liveService.fetchLiveInfo(lives, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    ArrayList<Live> lives = (ArrayList<Live>) data.getSerializable("lives");
                    if (lives != null && lives.size() == 1) {
                        enterChatAct(lives.get(0).getPasswd());
                    }
                }
            }
        });
        finish();
    }

    private int resultCode = RESULT_CANCELED;
    private String passwd = "";

    public void back(View v) {
        Intent data = new Intent();
        data.putExtra(ROOM_PWD, passwd);
        setResult(resultCode, data);
        finish();
    }

    private void enterChatAct(String pwd) {
        loger.debug("enter chat liveId={}", liveId);
        HDApp.getInstance().setCurrPwd(pwd);
        if (authService.getLiveId() != null && authService.getLiveId() == liveId) {
            final Intent intentChat = new Intent(HDRoomPasswdActivity.this, HDChatActivity.class);
            intentChat.putExtra("isJoined", false);
            intentChat.putExtra("liveId", liveId);
            intentChat.putExtra("isSameRoomId", true);
            startActivity(intentChat);
        } else if (authService.getLiveId() == null) {
            authService.joinLive(liveId, pwd, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (AuthService.OK == e && authService.getLiveId() != null) {
                        final Intent intentChat = new Intent(HDRoomPasswdActivity.this, HDChatActivity.class);
                        intentChat.putExtra("isJoined", false);
                        intentChat.putExtra("liveId", liveId);
                        intentChat.putExtra("isSameRoomId", false);
                        startActivity(intentChat);
                    } else {
                        HDApp.getInstance().setCurrPwd("");
                        Toast.makeText(HDRoomPasswdActivity.this, "密码错误" + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            loger.debug("enterChatAct");
            authService.leaveLive(authService.getLiveId(), new CallBack() {
                @Override
                public void process(Bundle data) {
                    //TODO leave room
                }
            });

            authService.joinLive(liveId, pwd, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (AuthService.OK == e && authService.getLiveId() != null) {
                        final Intent intentChat = new Intent(HDRoomPasswdActivity.this, HDChatActivity.class);
                        intentChat.putExtra("isJoined", false);
                        intentChat.putExtra("liveId", liveId);
                        intentChat.putExtra("isSameRoomId", false);
                        startActivity(intentChat);
                    } else {
                        HDApp.getInstance().setCurrPwd("");
                        Toast.makeText(HDRoomPasswdActivity.this, "密码错误" + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        finish();
    }

    private void loadLiveData() {
        long[] lives = new long[1];
        lives[0] = liveId;
        liveService.fetchLiveInfo(lives, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    ArrayList<Live> lives = (ArrayList<Live>) data.getSerializable("lives");
                    currLive = lives.get(0);
                }
            }
        });
    }

    private void passwd(CharSequence pwd) {
        if (pwd == null) {
            pwd = "";
        }
        if (pwd.length() >= 4) {
            v4.setVisibility(View.INVISIBLE);
            p4.setVisibility(View.VISIBLE);
            p4.setText(pwd.subSequence(3, 4));
            passwd = pwd.subSequence(0, 4).toString();
            resultCode = RESULT_OK;
            if (fromSetting) {
                back(null);
            } else {
                enterChatAct(passwd);
            }
            return;
        } else {
            v4.setVisibility(View.VISIBLE);
            p4.setVisibility(View.INVISIBLE);
        }

        if (pwd.length() >= 3) {
            v3.setVisibility(View.INVISIBLE);
            p3.setVisibility(View.VISIBLE);
            p3.setText(pwd.subSequence(2, 3));
        } else {
            v3.setVisibility(View.VISIBLE);
            p3.setVisibility(View.INVISIBLE);
        }

        if (pwd.length() >= 2) {
            v2.setVisibility(View.INVISIBLE);
            p2.setVisibility(View.VISIBLE);
            p2.setText(pwd.subSequence(1, 2));
        } else {
            v2.setVisibility(View.VISIBLE);
            p2.setVisibility(View.INVISIBLE);
        }

        if (pwd.length() >= 1) {
            v1.setVisibility(View.INVISIBLE);
            p1.setVisibility(View.VISIBLE);
            p1.setText(pwd.subSequence(0, 1));
        } else {
            v1.setVisibility(View.VISIBLE);
            p1.setVisibility(View.INVISIBLE);
        }

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            back(null);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
