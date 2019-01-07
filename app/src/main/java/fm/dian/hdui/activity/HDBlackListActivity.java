package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.RoomUserIgnore;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDBlackListActivityAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

/**
 * ******************
 * fileName  :HDHistoryActivity.java
 * author    : song
 * createTime:2015-3-30 下午4:09:03
 * fileDes   :
 */
@SuppressLint("InflateParams")
public class HDBlackListActivity extends HDBaseActivity {
    private PullRefreshLayout layout;
    private ListView lv_member;
    private HDBlackListActivityAdapter mAdapter;
    ArrayList<RoomUserIgnore> dataList = new ArrayList<>();
    CoreService coreService = CoreService.getInstance();

    public long roomId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_blacklist_activity);
        initUI();

        loadData();
    }

    public void loadData() {
        coreService.fetchIgnoreByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    cancelRefrash();
                    dataList = (ArrayList<RoomUserIgnore>) data.getSerializable("room_user_ignore_list");
                    if (dataList != null) {
                        setActionBarTitle("黑名单(" + dataList.size() + ")");
                        mAdapter.resetData(dataList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setVisibility(View.GONE);
        setActionBarTitle("黑名单");
        roomId = getIntent().getLongExtra("roomId", 0);
        mAdapter = new HDBlackListActivityAdapter(this, dataList);
        lv_member = (ListView) findViewById(R.id.lv_member);
        lv_member.setAdapter(mAdapter);
        lv_member.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                RoomUserIgnore m = dataList.get(position);
                Intent intent = new Intent(HDBlackListActivity.this, HDUserActivity.class);
                intent.putExtra(HDUserActivity.USER_ID, m.getUserId());
                intent.putExtra(HDUserActivity.ROOM_ID, roomId);
                HDBlackListActivity.this.startActivity(intent);
            }
        });

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
