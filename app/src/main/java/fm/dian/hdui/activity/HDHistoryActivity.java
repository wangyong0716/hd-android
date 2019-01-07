package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HistoryListElement;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDHistoryAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.net.oldinterface.HDUserCache;

/**
 * ******************
 * fileName  :HDHistoryActivity.java
 * author    : song
 * createTime:2015-3-30 下午4:09:03
 * fileDes   :
 */
@SuppressLint("InflateParams")
public class HDHistoryActivity extends HDBaseActivity {
    TextView tv_unpub;
    TextView tv_pub;
    int type = 1;
    ListView mListView;
    private HistoryService historyService = HistoryService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();

    private long roomId;

    HDHistoryAdapter unPubAdapter;
    HDHistoryAdapter pubAdapter;

    Boolean isNormalUser = false;

    int pubLoadIdx = 0;
    int unpubLoadIdx = 0;

    private boolean mLoading = false;

    private List mPubList = new ArrayList();
    private List mUnpubList = new ArrayList();

    private boolean mPubEnd = false;
    private boolean mUnpubEnd = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_history);
        initUI();
    }

    public void loadData() {
        // 到底啦，不要拉啦
        if ((type == 1 && mUnpubEnd)
                || (type == 2 && mPubEnd))
            return;

        historyService.fetchHistoryList(roomId,
                (type == 1) ? 1 : 0,
                type == 1 ? unpubLoadIdx : pubLoadIdx,
                20,
                new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HistoryService.OK == e) {
                    if (type == 2){
                        Set<HistoryListElement> publishSet = (Set<HistoryListElement>) data.getSerializable("published");
                        if (publishSet.size() != 0){
                            mPubList.addAll(publishSet);
                            pubAdapter.resetData(mPubList);
                            pubAdapter.notifyDataSetChanged();

                            mListView.setSelection(pubLoadIdx);
                            pubLoadIdx += 20;
                        }else{
                            mPubEnd = true;
                            Toast.makeText(HDHistoryActivity.this, getResources().getString(R.string.net_get_no_more_data), Toast.LENGTH_SHORT).show();
                        }
                    }else if (type == 1) {
                        Set<HistoryListElement> unpublishSet = (Set<HistoryListElement>) data.getSerializable("unpublished");
                        if (unpublishSet.size() != 0) {
                            mUnpubList.addAll(unpublishSet);
                            unPubAdapter.resetData(mUnpubList);
                            unPubAdapter.notifyDataSetChanged();

                            mListView.setSelection(unpubLoadIdx);
                            unpubLoadIdx += 20;
                        }else{
                            mUnpubEnd = true;
                            Toast.makeText(HDHistoryActivity.this, getResources().getString(R.string.net_get_no_more_data), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                mLoading = false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unpub:
                type = 1;
                tv_unpub.setBackgroundResource(R.drawable.btn_history_title_tab_checked_selector);
                tv_unpub.setTextColor(getResources().getColor(R.color.chat_room_recording_bar));
                tv_pub.setBackgroundResource(R.color.color_white);
                tv_pub.setTextColor(getResources().getColor(R.color.color_black));

                mListView.setAdapter(unPubAdapter);
                break;
            case R.id.tv_pub:
                type = 2;
                tv_pub.setBackgroundResource(R.drawable.btn_history_title_tab_checked_selector);
                tv_unpub.setTextColor(getResources().getColor(R.color.color_black));
                tv_unpub.setBackgroundResource(R.color.color_white);
                tv_pub.setTextColor(getResources().getColor(R.color.chat_room_recording_bar));

                mListView.setAdapter(pubAdapter);
                break;

            default:
                break;
        }
    }

    @Override
    public void initUI() {
        roomId = getIntent().getLongExtra("roomId", 0);

        long currUerId = new HDUserCache().getLoginUser().userId;
        boolean isAdmin = actionAuthService.isRole(currUerId, roomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(currUerId, roomId, 0l, AuthActionRequest.UserAuthType.UserOwner);
        if (isAdmin || isOwner) {
            findViewById(R.id.ll_type).setVisibility(View.VISIBLE);
            isNormalUser = false;
        } else {
            isNormalUser = true;
            type = 2;
        }

        initActionBar(this);
        setActionBarTitle("节目");
        tv_common_action_bar_right.setVisibility(View.GONE);
        tv_unpub = (TextView) findViewById(R.id.tv_unpub);
        tv_pub = (TextView) findViewById(R.id.tv_pub);
        tv_unpub.setOnClickListener(this);
        tv_pub.setOnClickListener(this);

        unPubAdapter = new HDHistoryAdapter(this, false);
        pubAdapter = new HDHistoryAdapter(this, true);
        pubAdapter.setUserType(isNormalUser);

        mListView = (ListView) findViewById(R.id.mListView);
        if (isNormalUser){
            mListView.setAdapter(pubAdapter);
        }else{
            mListView.setAdapter(unPubAdapter);
        }
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HistoryListElement record = null;
                if (isNormalUser) {
                    record = (HistoryListElement) pubAdapter.getItem(position);
                } else {
                    if (type == 1) {
                        record = (HistoryListElement) unPubAdapter.getItem(position);
                    } else {
                        record = (HistoryListElement) pubAdapter.getItem(position);
                    }
                }
                Intent intent = new Intent(HDHistoryActivity.this, HDWebActivity.class);
                intent.putExtra("url", record.getShareUrl());
                intent.putExtra("title", record.getName());
                intent.putExtra("currRoomId", record.getRoomId());
                intent.putExtra("liveId", record.getHistoryId());
                HDHistoryActivity.this.startActivity(intent);
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch (view.getId()) {
                    case R.id.mListView:
                        if (firstVisibleItem + visibleItemCount == totalItemCount) {
                            // List reaches the bottom now
                            if (!mLoading) {
                                synchronized (this) {
                                    mLoading = true;
                                    loadData();
                                }
                            }
                        }
                }
            }
        });

    }


}
