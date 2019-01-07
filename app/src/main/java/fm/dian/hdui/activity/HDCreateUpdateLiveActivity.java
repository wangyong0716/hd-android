package fm.dian.hdui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fm.dian.hddata.HDData;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.view.CommonDialog;

/**
 * Created by song on 2015/4/2.
 */
public class HDCreateUpdateLiveActivity extends HDBaseActivity {
    public static final int result_code_update_success = 10;
    private LiveService liveService = LiveService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();

    long roomId;
    long liveId;
    TextView tv_item2_value, tv_item3_value, tv_item4_value, tv_item5_value;
    EditText et_item1_value;
    RelativeLayout rl_item5, rl_item6, rl_item20;

    Live live = new Live();
    boolean isUpdate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_create_live);
        initUI();
    }

    private int getChineseCount(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count + str.length();
    }

    @Override
    public void initUI() {
        roomId = getIntent().getLongExtra("roomId", 0);
        liveId = getIntent().getLongExtra("liveId", 0);
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        HDUser user = new HDUserCache().getLoginUser();
        live = new Live();
        live.setRoom_id(roomId);
        live.setUser_id(user.userId);

        initActionBar(this);
        if (isUpdate) {
            setActionBarTitle("房间设置");
            tv_common_action_bar_right.setText("完成");
        } else {
            setActionBarTitle("发起直播");
            tv_common_action_bar_right.setText("创建");
        }
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);

        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setOnClickListener(this);
        rl_item5 = (RelativeLayout) findViewById(R.id.rl_item5);
        et_item1_value = (EditText) findViewById(R.id.et_item1_value);
        tv_item2_value = (TextView) findViewById(R.id.tv_item2_value);
        tv_item2_value.setOnClickListener(this);
        tv_item3_value = (TextView) findViewById(R.id.tv_item3_value);
        tv_item3_value.setOnClickListener(this);
        tv_item4_value = (TextView) findViewById(R.id.tv_item4_value);
        tv_item4_value.setOnClickListener(this);
        tv_item5_value = (TextView) findViewById(R.id.tv_item5_value);
        tv_item5_value.setOnClickListener(this);
        rl_item6 = (RelativeLayout) findViewById(R.id.rl_item6);
        rl_item6.setOnClickListener(this);
        rl_item20 = (RelativeLayout) findViewById(R.id.rl_item20);
        rl_item20.setOnClickListener(this);

        if (isUpdate) {
            loadData();
        }

        InputFilter []filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(20) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (getChineseCount(source.toString()) + getChineseCount(dest.toString()) > 20) {
                    return "";
                }
                return null;
            }
        };

        et_item1_value.setFilters(filters);


        et_item1_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {

                /*String s = cs.toString();
                while(s.getBytes().length > 20){
                    s = s.substring(0, s.length() - 1);
                }
                //et_item1_value.setText((CharSequence)s);

                et_item1_value.setSelection(s.toCharArray().length);*/
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        long[] liveIds = new long[1];
        liveIds[0] = liveId;
        liveService.fetchLiveInfo(liveIds, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    ArrayList<Live> lives = (ArrayList<Live>) data.getSerializable("lives");
                    if (lives != null && lives.size() > 0) {
                        live = lives.get(0);
                        inflateLive();
                    }
                }
            }
        });

        long loginUserId = new HDData().getLoginUser().userId;
        boolean isOwner = actionAuthService.isRole(loginUserId, roomId, 0l, AuthActionRequest.UserAuthType.UserOwner);
        boolean isAdmin = actionAuthService.isRole(loginUserId, roomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        if (isOwner || isAdmin) {
            if (isUpdate) {
                rl_item20.setVisibility(View.VISIBLE);
            }
        } else {
            rl_item20.setVisibility(View.GONE);
        }

    }

    private void inflateLive() {
        et_item1_value.setText(live.getName());
        if (live.getFree_live()) {//自由上麦
            tv_item2_value.setBackgroundResource(R.drawable.room_pass_btn_2);
        } else {
            tv_item2_value.setBackgroundResource(R.drawable.room_pass_btn_1);
        }
        if (live.getSilence()) {//全体禁言
            tv_item3_value.setBackgroundResource(R.drawable.room_pass_btn_2);
        } else {
            tv_item3_value.setBackgroundResource(R.drawable.room_pass_btn_1);
        }
        if (live.getLocked()) {//房间密码
            tv_item4_value.setBackgroundResource(R.drawable.room_pass_btn_2);
            rl_item5.setVisibility(View.VISIBLE);
            tv_item5_value.setText(live.getPasswd());
        } else {
            tv_item4_value.setBackgroundResource(R.drawable.room_pass_btn_1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                if (isUpdate) {
                    updateLive();
                } else {
                    createLive();
                }
                break;
            case R.id.tv_item2_value://自由上麦
                Boolean freeLive = live.getFree_live();
                if (freeLive == null || !freeLive) {
                    tv_item2_value.setBackgroundResource(R.drawable.room_pass_btn_2);
                    live.setFree_live(true);

                } else {
                    tv_item2_value.setBackgroundResource(R.drawable.room_pass_btn_1);
                    live.setFree_live(false);
                }
                break;
            case R.id.tv_item3_value://全体禁言
                Boolean silence = live.getSilence();
                if (silence == null || !silence) {
                    tv_item3_value.setBackgroundResource(R.drawable.room_pass_btn_2);
                    live.setSilence(true);
                } else {
                    tv_item3_value.setBackgroundResource(R.drawable.room_pass_btn_1);
                    live.setSilence(false);
                }
                break;
            case R.id.tv_item4_value://直播密码
                Boolean locked = live.getLocked();
                if (locked == null || !locked) {
                    Intent intent = new Intent(getApplicationContext(), HDRoomPasswdActivity.class);
                    intent.putExtra("fromSetting", true);
                    startActivityForResult(intent, HDRoomPasswdActivity.REQUEST_CODE_ROOM_PWD);
                } else {
                    tv_item4_value.setBackgroundResource(R.drawable.room_pass_btn_1);
                    live.setLocked(false);
                    rl_item5.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_item5_value://当前密码
                Intent intent = new Intent(getApplicationContext(), HDRoomPasswdActivity.class);
                intent.putExtra("fromSetting", true);
                startActivityForResult(intent, HDRoomPasswdActivity.REQUEST_CODE_ROOM_PWD);
                break;
            case R.id.rl_item6://关键词过滤
                Intent keywordIntent = new Intent(getApplicationContext(), HDKeywordActivity.class);
                keywordIntent.putExtra("liveId", liveId);
                startActivityForResult(keywordIntent, HDRoomPasswdActivity.REQUEST_CODE_ROOM_PWD);
                break;
            case R.id.rl_item20://删除房间
                new CommonDialog(this, CommonDialog.ButtonType.TowButton, new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                        deleteLive();
                    }

                    public void doNegativeClick() {
                    }
                }, "确定删除房间？");
                break;
            default:
                break;
        }
    }

    private void createLive() {
        String liveName = et_item1_value.getText().toString().trim();
        if (liveName == null || liveName.length() == 0) {
            Toast.makeText(HDCreateUpdateLiveActivity.this, "请填写标题", Toast.LENGTH_SHORT).show();
            return;
        }
        live.setName(liveName);
        liveService.startLive(live, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    HDCreateUpdateLiveActivity.this.finish();
                } else {
                    Toast.makeText(HDCreateUpdateLiveActivity.this, "发起直播失败 " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLive() {
        String liveName = et_item1_value.getText().toString().trim();
        if (liveName == null || liveName.length() == 0) {
            Toast.makeText(HDCreateUpdateLiveActivity.this, "请填写标题", Toast.LENGTH_SHORT).show();
            return;
        }
        live.setName(liveName);
        liveService.updateLive(live, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    Intent intent = new Intent();
                    intent.putExtra("liveName", live.getName());
                    intent.putExtra("isSilence", live.getSilence());
                    setResult(result_code_update_success, intent);
                    SysTool.hideKeyBoard(HDCreateUpdateLiveActivity.this);

                    HDCreateUpdateLiveActivity.this.finish();
                } else {
                    Toast.makeText(HDCreateUpdateLiveActivity.this, "更新失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteLive() {
        liveService.closeLive(liveId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    Intent intent = new Intent();
                    intent.putExtra("isClosed", true);
                    setResult(result_code_update_success, intent);
                    SysTool.hideKeyBoard(HDCreateUpdateLiveActivity.this);
                    HDCreateUpdateLiveActivity.this.finish();

                } else {
                    Toast.makeText(HDCreateUpdateLiveActivity.this, "关闭直播间" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HDRoomPasswdActivity.REQUEST_CODE_ROOM_PWD:
                if (resultCode == RESULT_OK) {
                    String pwd = data.getStringExtra(HDRoomPasswdActivity.ROOM_PWD);
                    if (pwd != null && pwd.length() == 4) {
                        rl_item5.setVisibility(View.VISIBLE);
                        tv_item4_value.setBackgroundResource(R.drawable.room_pass_btn_2);
                        live.setLocked(true);
                        live.setPasswd(pwd);
                        tv_item5_value.setText(pwd);
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
