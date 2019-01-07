package fm.dian.hdui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hddata.HDData;
import fm.dian.hddata.activity.HDLiveActivity;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata.business.service.core.user.HDUserHandler;
import fm.dian.hddata.business.service.core.user.HDUserModelMessage;
import fm.dian.hddata.channel.HDDataChannelClient;
import fm.dian.hddata.channel.HDDataChannelClient.HDDataCallbackListener;
import fm.dian.hddata.channel.HDDataChannelClient.HDDataChannelClientListener;
import fm.dian.hddata.channel.message.HDDataMessage;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.HDDataService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.util.image.ImageScaleUtil;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.wximage.choose.ImageChooseActivity;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;
import fm.dian.service.rpc.HDResponse.Response.ResponseStatus;

/**
 * Created by mac on 14-4-2.
 */
public class HDRegister3Activity extends HDBaseActivity {
    private HDDataChannelClient dataChannel;
    private HDUserHandler userHandler;

    //	private String phoneNum ;
//	private String phonePwd ;
    private String userIconPath = null;

    EditText et_user_name;
    EditText et_user_des;
    ImageView iv_user_icon;

    TextView tv_man;
    TextView tv_woman;

    private boolean isMan = true;
    private boolean isSetIcon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_register3);
        initUI();
    }


    public void initUI() {
        super.initActionBar(this);
        setActionBarTitle("注册信息（3/3）");
        dataChannel = new HDDataChannelClient(new SimpleHDDataChannelListener());
        userHandler = new HDUserHandler();
        dataChannel.bind();
//		phoneNum = getIntent().getStringExtra("phoneNum");
//		phonePwd = getIntent().getStringExtra("phonePwd");

        tv_common_action_bar_right.setText("下一步");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setEnabled(false);
        tv_common_action_bar_right.setOnClickListener(this);

        this.findViewById(R.id.rl_man).setOnClickListener(this);
        this.findViewById(R.id.rl_woman).setOnClickListener(this);
        tv_man = (TextView) this.findViewById(R.id.tv_man);
        tv_woman = (TextView) this.findViewById(R.id.tv_woman);
        iv_user_icon = (ImageView) this.findViewById(R.id.iv_user_icon);
        iv_user_icon.setOnClickListener(this);
        et_user_name = (EditText) this.findViewById(R.id.et_user_name);
        et_user_des = (EditText) this.findViewById(R.id.et_user_des);

        et_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s.toString().trim() && s.toString().length() > 0) {
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
                updateUser();
                break;
            case R.id.iv_user_icon:
                Intent chooseImage = new Intent(this, ImageChooseActivity.class);
                startActivityForResult(chooseImage, 1);
                break;
            case R.id.rl_man:
                isMan = true;
                tv_man.setBackgroundResource(R.drawable.rb_sex_checked);
                tv_woman.setBackgroundResource(R.drawable.rb_sex_uncheck);
                break;
            case R.id.rl_woman:
                isMan = false;
                tv_man.setBackgroundResource(R.drawable.rb_sex_uncheck);
                tv_woman.setBackgroundResource(R.drawable.rb_sex_checked);

                break;
            default:
                break;
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != HDBaseTabFragmentActivity.chooseBitmapPathLocal) {
            userIconPath = HDBaseTabFragmentActivity.chooseBitmapPathRemote;

            Bitmap scaleBitmap = ImageScaleUtil.scaleBitmap(HDBaseTabFragmentActivity.chooseBitmapPathLocal);
            Drawable drawable = new BitmapDrawable(scaleBitmap);
            iv_user_icon.setImageDrawable(drawable);

            HDBaseTabFragmentActivity.chooseBitmapPathRemote = null;
            HDBaseTabFragmentActivity.chooseBitmapPathLocal = null;
            isSetIcon = true;
        }
    }

    private void updateUser() {
        HDUser user = new HDData().getLoginUser();
        if (user == null) {
            return;
        }
        String uName = et_user_name.getText().toString().trim();
        String uDes = et_user_des.getText().toString().trim();
        user.nickname = uName;
        user.signature = uDes;

        if (uName == null || uName.length() == 0) {
            new CommonDialog(HDRegister3Activity.this, ButtonType.TowButton, null, "请填写用户名");
            return;
        }
        if (uDes == null || uDes.length() == 0) {
            new CommonDialog(HDRegister3Activity.this, ButtonType.TowButton, null, "请填写个性签名");
            return;
        }
        user.gender = isMan ? GenderType.MALE : GenderType.FEMALE;
        if (userIconPath != null) {
            user.avatar = userIconPath;
        }

        if (!isSetIcon) {
            new CommonDialog(HDRegister3Activity.this, ButtonType.TowButton, null, "请设置头像");
            return;
        }

        HDDataService hdDataService = HDDataService.getInstance();
        hdDataService.start();
        AuthService.getInstance().setUserId(user.userId);

        CoreService coreService = CoreService.getInstance();
        User newUser=new User();
        newUser.setUserId(user.userId);
        newUser.setNickname(user.nickname);
        newUser.setAvatar(user.avatar);
        newUser.setSignature(user.signature);

        coreService.updateUserInfo(newUser,new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if(CoreService.OK == e){
                    Intent intentMain = new Intent(HDRegister3Activity.this, HDBaseTabFragmentActivity.class);
                    startActivity(intentMain);
                    HDRegister3Activity.this.finish();
                }else{
                    Toast.makeText(HDRegister3Activity.this,"注册 更新用户信息"+e,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataChannel != null) {
            dataChannel.unBind();
        }
        if (userHandler != null) {
            userHandler = null;
        }
    }

    private final class SimpleHDDataChannelListener implements HDDataChannelClientListener {
        public void onUnBind(boolean isUnBind) {
            HDUi_log.i(HDLiveActivity.class.getSimpleName() + " onUnBind: " + isUnBind);
        }
    }
}
