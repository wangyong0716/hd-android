package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDLiveSettingAdapter;
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
public class HDLiveSettingActivity extends HDBaseActivity {
    private PullRefreshLayout layout;
    private ListView lv_member;
    private TextView tv_des;
    private HDLiveSettingAdapter mAdapter;
    ArrayList<LiveListElement> dataList = new ArrayList<>();
    private LiveService liveService = LiveService.getInstance();
    private ConfigService configService = ConfigService.getInstance();

    public long roomId = 0;
    private int maxRoomCount= configService.getLiveNumber();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_livesetting_activity);
        initUI();

        loadLives();
    }

    private void loadLives() {
        liveService.fetchLiveList(roomId, 0, 200, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    dataList = (ArrayList<LiveListElement>) data.getSerializable("live_list");
                    if (dataList != null) {
                        tv_des.setText("已经创建" + dataList.size() + "个房间，最多可创建"+maxRoomCount+"个房间");
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
        setActionBarTitle("房间管理");
        roomId = getIntent().getLongExtra("roomId", 0);
        mAdapter = new HDLiveSettingAdapter(this, dataList);
        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_member = (ListView) findViewById(R.id.lv_member);
        LayoutInflater inflater= LayoutInflater.from(this);
        View inflate = inflater.inflate(R.layout.item_add_live, null);
        lv_member.addFooterView(inflate);

        lv_member.setAdapter(mAdapter);
        lv_member.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(position == mAdapter.getCount()){
                    if(dataList.size()<maxRoomCount){
                        final Intent intentCreateLive = new Intent(HDLiveSettingActivity.this, HDCreateUpdateLiveActivity.class);
                        intentCreateLive.putExtra("roomId", roomId);
                        startActivityForResult(intentCreateLive, 1);
                    }else{
                        Toast.makeText(HDLiveSettingActivity.this,"最多可创建"+maxRoomCount+"个房间",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    LiveListElement m = dataList.get(position);
                    Intent liveSettingIntent = new Intent(HDLiveSettingActivity.this, HDCreateUpdateLiveActivity.class);
                    liveSettingIntent.putExtra("roomId", roomId);
                    liveSettingIntent.putExtra("liveId", m.getLive().getId());
                    liveSettingIntent.putExtra("isUpdate", true);
                    HDLiveSettingActivity.this.startActivity(liveSettingIntent);
                }
            }
        });

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLives();
                cancelRefrash();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLives();
    }
}
