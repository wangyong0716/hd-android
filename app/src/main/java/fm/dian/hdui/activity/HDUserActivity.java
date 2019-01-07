package fm.dian.hdui.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import fm.dian.hddata.HDData;
import fm.dian.hddata.util.HDLog;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.broadcast.HDUserChangedBroadCastReceiver;
import fm.dian.hdui.util.ActivityCheck;
import fm.dian.hdui.util.HDImageLoadUtil;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;

public class HDUserActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";
    public static final String USER_ID = "USER_ID";

    private HDLog log = new HDLog(HDUserActivity.class);

    private long roomId = 0;
    private long userId = 0;

    private HDImageLoadUtil imageLoadUtil;

    private TextView userName;
    private TextView userGender;
    private ImageView userAvatar;
    private TextView signature;

    private View addAdmin;
    private View cancelAdmin;
    private View addIgnore;
    private View cancelIgnore;

//    private boolean isFollow = true;

    CoreService coreService = CoreService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_user);

        roomId = getIntent().getLongExtra(ROOM_ID, 0);
        userId = getIntent().getLongExtra(USER_ID, 0);
        imageLoadUtil = new HDImageLoadUtil();

        userName = (TextView) findViewById(R.id.userName);
        userGender = (TextView) findViewById(R.id.userGender);
        userAvatar = (ImageView) findViewById(R.id.userAvatar);
        signature = (TextView) findViewById(R.id.signature);

        addAdmin = findViewById(R.id.addAdmin);
        cancelAdmin = findViewById(R.id.cancelAdmin);
        addIgnore = findViewById(R.id.addIgnore);
        cancelIgnore = findViewById(R.id.cancelIgnore);

        loadUser();
    }

    private void loadUser() {
        coreService.fetchUserInfo(userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    User user = (User) data.getSerializable("user");

                    setUser(user);
                }
            }
        });
    }

    private void setUser(final User user) {
        addAdmin.setVisibility(View.GONE);
        cancelAdmin.setVisibility(View.GONE);
        addIgnore.setVisibility(View.GONE);
        cancelIgnore.setVisibility(View.GONE);

        if (user == null) {
            log.e("user is null.");
            userName.setText("");
            signature.setText("");
            userGender.setText("");
            return;
        }

        String s = user.getSignature();
        if (s == null || s.equals("NONE")) {
            s = "";
        }

        userName.setText(user.getNickname());
        signature.setText(s);

        if (user.getGender() == GenderType.MALE) {
            userGender.setText("男");
        } else if (user.getGender() == GenderType.FEMALE) {
            userGender.setText("女");
        } else {
            userGender.setText("");
        }

        if (!imageLoadUtil.isLoadingComplete(userAvatar, user.getAvatar())) {
            HDApp.getInstance().imageLoader.displayImage(user.getAvatar(), userAvatar, Config.getUserConfig(), new SimpleImageLoadingListener() {
                public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
                    imageLoadUtil.setImageLoad(userAvatar, user.getAvatar());
                }
            });
        }

        long loginUserId = new HDData().getLoginUser().userId;

        if (loginUserId == userId) {
            return;
        }

        boolean addAdminPermission = actionAuthService.hasPermission(loginUserId, userId, roomId, AuthActionRequest.ActionType.ActionAddAdmin);
        boolean cancelAdminPermission = actionAuthService.hasPermission(loginUserId, userId, roomId, AuthActionRequest.ActionType.ActionRemoveAdmin);
        boolean addIgnorePermission = actionAuthService.hasPermission(loginUserId, userId, roomId, AuthActionRequest.ActionType.ActionAddIgnore);
        boolean cancelIgnorePermission = actionAuthService.hasPermission(loginUserId, userId, roomId, AuthActionRequest.ActionType.ActionRemoveIgnore);


        if (addAdminPermission) {
            addAdmin.setVisibility(View.VISIBLE);
        } else {
            addAdmin.setVisibility(View.GONE);
        }
        if (cancelAdminPermission) {
            cancelAdmin.setVisibility(View.VISIBLE);
        } else {
            cancelAdmin.setVisibility(View.GONE);
        }
        if (addIgnorePermission) {
            addIgnore.setVisibility(View.VISIBLE);
        } else {
            addIgnore.setVisibility(View.GONE);
        }
        if (cancelIgnorePermission) {
            cancelIgnore.setVisibility(View.VISIBLE);
        } else {
            cancelIgnore.setVisibility(View.GONE);
        }

    }

    public void back(View v) {
        finish();
    }


    public void addAdmin(View v) {
        addAdmin.setEnabled(false);
        coreService.addAdmin(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    loadUser();
                }else if(400==e){
                    addAdminFail();
                } else {
                    Toast.makeText(HDUserActivity.this, "添加管理员失败" + e, Toast.LENGTH_SHORT).show();
                }
                addAdmin.setEnabled(true);
            }
        });
    }
    private void addAdminFail(){
        final Dialog builder = new Dialog(this, R.style.HDDialog);
        builder.setContentView(R.layout.activity_user_tip_dialog);
        builder.setCanceledOnTouchOutside(true);

        TextView title = (TextView) builder.findViewById(R.id.title);
        title.setText("设置管理员失败");
        TextView content = (TextView) builder.findViewById(R.id.content);
        content.setText("该用户可能还未订阅频道");

        builder.findViewById(R.id.OK).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public void cancelAdmin(View v) {
        cancelAdmin.setEnabled(false);
        coreService.cancelAdmin(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    loadUser();
                } else {
                    Toast.makeText(HDUserActivity.this, "取消管理员失败" + e, Toast.LENGTH_SHORT).show();
                }
                cancelAdmin.setEnabled(true);
            }
        });
    }

    public void addIgnore(View v) {
        addIgnore.setEnabled(false);
        coreService.addIgnore(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    loadUser();
                } else {
                    Toast.makeText(HDUserActivity.this, "拉黑失败" + e, Toast.LENGTH_SHORT).show();
                }
                addIgnore.setEnabled(true);
            }
        });
    }

    public void cancelIgnore(View v) {
        cancelIgnore.setEnabled(false);
        coreService.cancelIgnore(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    loadUser();
                } else {
                    Toast.makeText(HDUserActivity.this, "取消拉黑失败" + e, Toast.LENGTH_SHORT).show();
                }
                cancelIgnore.setEnabled(true);
            }
        });
    }

}
