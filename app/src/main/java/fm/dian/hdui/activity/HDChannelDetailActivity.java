package fm.dian.hdui.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.RoomUserFollow;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

/**
 * Created by song on 2015/4/2.
 */
public class HDChannelDetailActivity extends HDBaseActivity {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String IS_FOLLOW = "IS_FOLLOW";
    public static final String IS_FROM_CHAT_ACT = "IS_FROM_CHAT_ACT";

    public static final int REQUEST_CODE_ROOM_EDIT = 1;
    public static final int REQUEST_CODE_ROOM_EDIT_WEBADDR = 4;
    public static final int RESULT_CODE_DEL_ROOM = 2;

    PullRefreshLayout layout;
    long roomId;
    private long userId = 0;
    private boolean isFollow = true;
    private String roomNamePrefix;

    ImageView iv_user_icon;
    TextView tv_user_name, tv_user_des, tv_item1_value, tv_item2_value, tv_item3_value, tv_item10_value, tv_shareRoomWebaddr, tv_mark;
    RelativeLayout rl_item4, rl_item5;
    private PopupWindow mPopupWindow;

    private CoreService coreService = CoreService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();
    private HistoryService historyService = HistoryService.getInstance();
    Room room;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_room2);
        initUI();
        loadData();
    }


    @Override
    public void initUI() {
        roomId = getIntent().getLongExtra(ROOM_ID, 0);
        HDUser user = new HDUserCache().getLoginUser();
        userId = user.userId;
        isFollow = getIntent().getBooleanExtra(IS_FOLLOW, true);
        roomNamePrefix = ConfigService.getInstance().getUtilsRoomNamePrefix();

        initActionBar(this);
        tv_common_action_bar_right.setVisibility(View.VISIBLE);
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_chat_room_menu_bg_selector);
        tv_common_action_bar_right.setOnClickListener(this);
        setActionBarTitle("频道信息");
        findViewById(R.id.rl_item2).setOnClickListener(this);
        findViewById(R.id.rl_item3).setOnClickListener(this);
        findViewById(R.id.rl_item10).setOnClickListener(this);
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cancelRefrash();
            }
        });
        iv_user_icon = (ImageView) findViewById(R.id.iv_user_icon);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_des = (TextView) findViewById(R.id.tv_user_des);
        tv_item1_value = (TextView) findViewById(R.id.tv_item1_value);
        tv_item2_value = (TextView) findViewById(R.id.tv_item2_value);
        tv_item3_value = (TextView) findViewById(R.id.tv_item3_value);
        tv_item10_value = (TextView) findViewById(R.id.tv_item10_value);
        tv_shareRoomWebaddr = (TextView) findViewById(R.id.tv_shareRoomWebaddr);
        tv_mark = (TextView) findViewById(R.id.tv_mark);
        tv_mark.setOnClickListener(this);

        rl_item4 = (RelativeLayout) findViewById(R.id.rl_item4);
        rl_item4.setOnClickListener(this);
        rl_item5 = (RelativeLayout) findViewById(R.id.rl_item5);
        rl_item5.setOnClickListener(this);
    }

    private void loadData() {
        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    room = (Room) data.getSerializable("room");

                    if (room != null) {
                        inflateView(room);
                    }

                }
            }
        });

        boolean isAdmin = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserOwner);
        boolean isUser = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserUser);

        //pop菜单 根据角色不同
        if (isAdmin) {//管理员
            createPopupWindow(1, tv_common_action_bar_right);
        } else if (isOwner) {//频道主
            createPopupWindow(2, tv_common_action_bar_right);
        } else if (isUser) {//普通用户
            createPopupWindow(0, tv_common_action_bar_right);
        } else {
            tv_mark.setVisibility(View.VISIBLE);
            createPopupWindow(9, tv_common_action_bar_right);
        }
        //页面显示根据角色不同
        if (isAdmin || isOwner) {
            rl_item4.setVisibility(View.VISIBLE);
            rl_item5.setVisibility(View.VISIBLE);
        }

        coreService.fetchFollowByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    ArrayList<RoomUserFollow> roomUserFollowList = (ArrayList<RoomUserFollow>) data.getSerializable("room_user_follow_list");
                    if (roomUserFollowList != null && roomUserFollowList.size()>0) {
                        tv_item2_value.setText(roomUserFollowList.size() + "人");
                    }
                }
            }
        });

        historyService.fetchHistoryCount(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HistoryService.OK == e) {
                    int count = data.getInt("published_count");
                    if(count>0){
                        tv_item3_value.setText(count + "个");
                    }
                }
            }
        });
    }

    private void createPopupWindow(int level, View parent) {
        View menu_content = LayoutInflater.from(this).inflate(R.layout.popmenu_chanel, null);
        menu_content.findViewById(R.id.ll_item1).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_item2).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_item3).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_item4).setOnClickListener(this);
        if (level == 1 || level == 2) {
            menu_content.findViewById(R.id.ll_item2).setVisibility(View.VISIBLE);
        }
        if (level == 2) {
            menu_content.findViewById(R.id.ll_item3).setVisibility(View.VISIBLE);
            menu_content.findViewById(R.id.ll_item4).setVisibility(View.GONE);
        }

        if (level == 9) {
            menu_content.findViewById(R.id.ll_item4).setVisibility(View.GONE);
        }

        mPopupWindow = new PopupWindow(menu_content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）

//        mPopupWindow.showAsDropDown(parent, 10, 0);//popupwindow 显示在相对父容器x轴0像素，y轴0像素的位置下方

        mPopupWindow.setFocusable(true);// 使其聚集
        mPopupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        mPopupWindow.update();//刷新状态
    }

    private void inflateView(Room room) {
        HDApp.getInstance().imageLoader.displayImage(room.getAvatar(), iv_user_icon);
        tv_user_name.setText(room.getName());
        tv_user_des.setText("频道号: " + room.getWebaddr());

        tv_item1_value.setText(room.getDescription());
        tv_shareRoomWebaddr.setText("频道地址：" + roomNamePrefix + room.getWebaddr());
        tv_item10_value.setText(roomNamePrefix + room.getWebaddr());
    }

    private void cancelRefrash() {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 300);
    }

    private void cancelFollow(long roomId, long userId) {
        coreService.cancelFollow(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    createPopupWindow(9, tv_common_action_bar_right);
                    tv_mark.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(HDChannelDetailActivity.this, "取消订阅失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFollow(long roomId, long userId) {
        coreService.addFollow(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    tv_mark.setVisibility(View.GONE);
                    createPopupWindow(0, tv_common_action_bar_right);
                } else {
                    Toast.makeText(HDChannelDetailActivity.this, "加入频道失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteRoom() {
        new CommonDialog(this, CommonDialog.ButtonType.alert2Type, new CommonDialog.DialogClickListener() {

            @Override
            public void doPositiveClick() {//注销频道
                room.setIsCanceled(true);
                coreService.updateRoom(room, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (CoreService.OK == e) {
                            Toast.makeText(HDChannelDetailActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                            sendBroadcast(seeding);

                            setResult(RESULT_CODE_DEL_ROOM);
                            HDChannelDetailActivity.this.finish();
                        }
                    }
                });
            }

            @Override
            public void doNegativeClick() {
            }
        }, "注销后将无法恢复，确认要注销该频道吗");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                if (mPopupWindow != null) {
                    mPopupWindow.showAsDropDown(v, 10, 0);//popupwindow 显示在相对父容器x轴0像素，y轴0像素的位置下方
                } else {
                    Toast.makeText(this, "错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_item2:
                Intent onlineIntent = new Intent(getApplicationContext(), HDOnlieMemberActivity.class);
                onlineIntent.putExtra(HDOnlieMemberActivity.ROOMID, roomId);
                onlineIntent.putExtra(HDOnlieMemberActivity.KEY, "room");
                startActivity(onlineIntent);
                break;
            case R.id.rl_item3:
                Intent historyIntent = new Intent(getApplicationContext(), HDHistoryActivity.class);
                historyIntent.putExtra("roomId", roomId);
                startActivity(historyIntent);
                break;
            case R.id.rl_item4:
                Intent blacklistIntent = new Intent(this, HDBlackListActivity.class);
                blacklistIntent.putExtra("roomId", roomId);
                startActivity(blacklistIntent);
                break;
            case R.id.rl_item5:
                Intent liveSettingIntent = new Intent(this, HDLiveSettingActivity.class);
                liveSettingIntent.putExtra("roomId", roomId);
                startActivity(liveSettingIntent);
                break;
            case R.id.rl_item10:
                Intent shareIntent = new Intent(this, HDShareActivity.class);
                shareIntent.putExtra(HDShareActivity.ROOM_ID, roomId);
                startActivity(shareIntent);
                break;
            case R.id.ll_item1://右上角pop菜单 分享
                Intent shareIntent2 = new Intent(this, HDShareActivity.class);
                shareIntent2.putExtra(HDShareActivity.ROOM_ID, roomId);
                startActivity(shareIntent2);
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
                mPopupWindow.dismiss();
                break;
            case R.id.ll_item2://右上角pop菜单 编辑
                Intent intent = new Intent(getApplicationContext(), HDRoomEditActivity.class);
                intent.putExtra(HDRoomEditActivity.ROOM_ID, roomId);
                startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT);
                mPopupWindow.dismiss();
                break;
            case R.id.ll_item3://右上角pop菜单 注销
                deleteRoom();
                mPopupWindow.dismiss();
                break;
            case R.id.ll_item4://右上角pop菜单 取消订阅
                cancelFollow(roomId, userId);
                mPopupWindow.dismiss();
                break;
            case R.id.tv_mark://加入频道
                addFollow(roomId, userId);
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ROOM_EDIT:
            case REQUEST_CODE_ROOM_EDIT_WEBADDR:
                if (resultCode == RESULT_OK) {
                    loadData();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
