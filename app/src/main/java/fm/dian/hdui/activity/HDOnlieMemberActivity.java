package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.MemberService;
import fm.dian.hdservice.OnlineService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.OnlineUserList;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDOnlineMemberActivityAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.model.HDUIOnlineMemberModel;
import fm.dian.hdui.view.indexlistview.IndexableListView;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

@SuppressLint("HandlerLeak")
public class HDOnlieMemberActivity extends HDBaseActivity {
    public static final int CODE_RESULT_ONLINE_ACT = 5;
    public static final String ROOMNUM = "room_num";
    public static final String ROOMID = "roomId";
    public static final String KEY = "type";

    public String type = "user";//type: user用户列表   room房间列表
    public long roomId = 0;

    private static final int msg_load_online_member_success = 3;
    private PullRefreshLayout layout;
    private IndexableListView lv_member;
    private HDOnlineMemberActivityAdapter mAdapter;
    private List<HDUIOnlineMemberModel> dataList;

    MemberService memberService = MemberService.getInstance();
    OnlineService onlineService = OnlineService.getInstance();
    CoreService coreService = CoreService.getInstance();
    private int offlineNum = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_online_member);
        checkBaseHandlerEnable(this);
        type = getIntent().getStringExtra(KEY);
        roomId = getIntent().getLongExtra(ROOMID, 0);

        initUI();
        if (type != null && "user".equals(type)) {
            getOnlineData();
        } else {
            getRoomFllowUserData();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            int count = mAdapter.getCount();
            HDBaseTabFragmentActivity.self.chatActivityOnlineNumber = count + offlineNum;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void updateUserList(OnlineUserList onlineUserList) {
        List<Long> ownerUserIds = onlineUserList.getOwnerUserIdList();
        List<Long> adminUserIds = onlineUserList.getAdminUserIdList();
        List<Long> normalUserIds = onlineUserList.getNormalUserIdList();
        long anonyUserIds = onlineUserList.getAnonymousUserNumber();
        dataList.clear();
        if (ownerUserIds != null) {
            for (long id : ownerUserIds) {
                User user = new User();
                user.setUserId(id);
                HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
                m.setUiType("B");
                m.setOwner(true);
                dataList.add(m);
            }
        }
        if (adminUserIds != null) {
            for (long id : adminUserIds) {
                User user = new User();
                user.setUserId(id);
                HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
                m.setUiType("B");
                dataList.add(m);
            }
        }
        if (normalUserIds != null) {
            for (long id : normalUserIds) {
                User user = new User();
                user.setUserId(id);
                HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
                m.setUiType("C");
                dataList.add(m);
            }
        }
        //最后条目 还有3人未登录
        if (dataList.size() > 0 && anonyUserIds > 0) {
            offlineNum = (int) anonyUserIds;
            HDUIOnlineMemberModel lastModel = new HDUIOnlineMemberModel();
//			HDUIOnlineMemberModel realEdnItem = dataList.get(0);
            lastModel.setUiType("X-last");
            lastModel.setUnLoginNum(anonyUserIds);
            dataList.add(lastModel);
        }

        if (dataList != null) {
            setActionBarTitle("在线人数（" + dataList.size() + "）");
            Collections.sort(dataList);
            mAdapter.resetData(dataList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateRoomList(Long ownerId, Set<Long> adminSet, Set<Long> followSet) {
        dataList.clear();
        if (ownerId != null) {
            User user = new User();
            user.setUserId(ownerId);
            HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
            m.setUiType("B");
            m.setOwner(true);
            dataList.add(m);
        }
        if (adminSet != null) {
            for (long id : adminSet) {
                User user = new User();
                user.setUserId(id);
                HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
                m.setUiType("B");
                dataList.add(m);
            }
        }
        if (followSet != null) {
            for (long id : followSet) {
                User user = new User();
                user.setUserId(id);
                HDUIOnlineMemberModel m = new HDUIOnlineMemberModel(user);
                m.setUiType("C");
                dataList.add(m);
            }
        }
        if (dataList != null) {
            setActionBarTitle("成员人数（" + dataList.size() + "）");
            Collections.sort(dataList);
            mAdapter.resetData(dataList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setVisibility(View.GONE);
        ib_action_bar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int count = mAdapter.getCount();
                HDBaseTabFragmentActivity.self.chatActivityOnlineNumber = count + offlineNum;

                HDOnlieMemberActivity.this.finish();
            }
        });

        lv_member = (IndexableListView) findViewById(R.id.lv_member);
//		View bottom_view = LayoutInflater.from(this).inflate(R.layout.layout_online_activity_bottom_tip, null);
//		lv_member.addFooterView(bottom_view);

        lv_member.setFastScrollEnabled(false);

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (type != null && "user".equals(type)) {
                    getOnlineData();
                } else {
                    getRoomFllowUserData();
                }
            }
        });

        dataList = new ArrayList<HDUIOnlineMemberModel>();
        mAdapter = new HDOnlineMemberActivityAdapter(this, dataList);
        lv_member.setAdapter(mAdapter);
        lv_member.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HDUIOnlineMemberModel m = dataList.get(position);
                Intent intent = new Intent(HDOnlieMemberActivity.this, HDUserActivity.class);
                intent.putExtra(HDUserActivity.USER_ID, m.userId);
                intent.putExtra(HDUserActivity.ROOM_ID, roomId);
                HDOnlieMemberActivity.this.startActivity(intent);
            }
        });

    }

    private void cancelRefrash() {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 300);
    }

    public void getOnlineData() {
        int startIndex = 0;
        int endIndex = 1000000;

        onlineService.fetchOnlineUserList(startIndex, endIndex, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (OnlineService.OK == e) {
                    OnlineUserList onlineUserList = (OnlineUserList) data.getSerializable("online_user_list");
                    if (onlineUserList != null) {
                        updateUserList(onlineUserList);
                        cancelRefrash();
                    }
                } else {
                    cancelRefrash();
                    Toast.makeText(HDOnlieMemberActivity.this, "在线列表失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getRoomFllowUserData() {
        memberService.getMemberInOrder(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MemberService.OK == e) {
                    Long ownerId = data.getLong("owner");
                    Set<Long> adminSet = (Set<Long>) data.getSerializable("admin_set");
                    Set<Long> followSet = (Set<Long>) data.getSerializable("follow_set");

                    updateRoomList(ownerId, adminSet, followSet);
                    cancelRefrash();
                } else {
                    cancelRefrash();
                    Toast.makeText(HDOnlieMemberActivity.this, "成员列表失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }


}