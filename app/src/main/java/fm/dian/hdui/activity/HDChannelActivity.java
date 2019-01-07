package fm.dian.hdui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hddata_android.core.CorePublish;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDChannelActivityAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

/**
 * Created by song on 2015/4/2.
 */
public class HDChannelActivity extends HDBaseActivity {
    public static final int RESULT_CODE_CREATE_LIVE_SUCCESS = 3;
    public static final int REQUEST_CODE_ROOM_PWD = 4;

    List<Object> dataList = new ArrayList<Object>();

    HDChannelActivityAdapter mAdapter;
    PullRefreshLayout layout;
    ListView mListView;
    LinearLayout ll_bottom;

    boolean isFollow = false;
    long roomId;
    long userId = 0;
    Room roomLocal;
    boolean hasHistory = false;
    Observer coreObserver;

    private CoreService coreService = CoreService.getInstance();
    private LiveService liveService = LiveService.getInstance();
    private HistoryService historyService = HistoryService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        jumpChatAct();
        setContentView(R.layout.activity_channel);
        initUI();

        loadData();
    }

    private void jumpChatAct() {
        boolean jump = getIntent().getBooleanExtra("jumpChatAct", false);
        if (authService.getLiveId() != null && jump) {
            final Intent intentChat = new Intent(this, HDChatActivity.class);
            intentChat.putExtra("isJoined", false);
            intentChat.putExtra("liveId", authService.getLiveId());
            intentChat.putExtra("isSameRoomId", true);
            startActivity(intentChat);
        }

    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                   ContextMenu.ContextMenuInfo menuInfo) {
        // set context menu title
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        int pos = info.position;
        Log.i("FUCK", "" + pos);
        LiveListElement ele = (LiveListElement)mAdapter.getItem(pos);
        Log.i("FUCK", ele.toString());
        menu.setHeaderTitle(ele.getLive().getName());
        // add context menu item
        boolean isAdmin = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserOwner);
        boolean isUser = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserUser);
        if (isAdmin || isOwner) {
            menu.add(0, 1, Menu.NONE, "删除");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Log.i("FUCK", "We should remove this");
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                final int pos = info.position;
                LiveListElement ele = (LiveListElement)mAdapter.getItem(pos);
                Log.i("FUCK", ele.toString());
                long liveId = ele.getLive().getId();
                liveService.closeLive(liveId, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (LiveService.OK == e) {
                            Toast.makeText(HDChannelActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            dataList.remove(pos);
                            mAdapter.resetData(dataList);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(HDChannelActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }*/

    @Override
    public void initUI() {
        initActionBar(this);
        HDUser user = new HDUserCache().getLoginUser();
        userId = user.userId;
        roomId = getIntent().getLongExtra("roomId", 0);
        tv_common_action_bar_right.setVisibility(View.VISIBLE);
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_chat_room_menu_bg_selector);
        tv_common_action_bar_right.setOnClickListener(this);
        findViewById(R.id.ll_bottom).setOnClickListener(this);
//        setActionBarTitle("频道");
        mListView = (ListView) findViewById(R.id.mListView);
        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRoom();
                loadLives();
                cancelRefrash();
                setupRole();
            }
        });

        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mAdapter = new HDChannelActivityAdapter(HDChannelActivity.this, dataList);

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Long press" + i + " " + l, Toast.LENGTH_LONG);
                Log.e("setOnItemLongClick", "Long press" + i + " ");
                registerForContextMenu(view);
                //openContextMenu(view);
                return true;
            }
        });*/

        coreObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Bundle bundle = (Bundle) data;
                int pubType = bundle.getInt("publish_type");
                switch (pubType) {
                    case CorePublish.ROOM_PUBLISH://房间变化(房间注销)
                        Room room = (Room) bundle.getSerializable("room");
                        setupRoomProperty(room);
                        break;
                    default:
                        break;
                }
            }
        };
        coreService.addObserver(coreObserver);
    }

    private void loadData() {
        historyService.fetchHistoryCount(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HistoryService.OK == e) {
                    int count = data.getInt("published_count");
                    if (count > 0) {
                        mAdapter.setHistoryVisiable(true);
                    } else {
                        mAdapter.setHistoryVisiable(false);
                    }
                }
            }
        });

        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Room tempRoom = (Room) data.getSerializable("room");
                    if (roomLocal == null && tempRoom != null) {//避免CoreService重复回调
                        dataList.add(0, tempRoom);
                        roomLocal = tempRoom;

                        loadLives();//获取lives
                    }
                    if (roomLocal != null) {
                        inflateView(roomLocal);
                    }
                }
            }
        });
        setupRole();

    }

    boolean dialogShowing=false;//避免coreService调用两次
    private void setupRoomProperty(Room room) {
        Boolean isCanceled = room.getIsCanceled();
        boolean owner = actionAuthService.isRole(userId, room.getRoomId(), 0l, AuthActionRequest.UserAuthType.UserOwner);
        if (isCanceled && !dialogShowing && !owner) {//房间注销了
            dialogShowing=true;
            new CommonDialog(HDBaseTabFragmentActivity.self,
                    CommonDialog.ButtonType.alert1Type,
                    new CommonDialog.DialogClickListener() {
                        public void doPositiveClick() {
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                            sendBroadcast(seeding);

                            HDApp.getInstance().currRoomDel = true;
                            HDChannelActivity.this.finish();
                        }

                        public void doNegativeClick() {
                        }
                    }, "该频道已被频道主注销,你将自动退出该频道");
        }
    }

    //判断用户角色，给予相应权限
    private void setupRole() {
        boolean isAdmin = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserOwner);
        boolean isUser = actionAuthService.isRole(userId, roomId, 0l, AuthActionRequest.UserAuthType.UserUser);
        if (isUser || isAdmin || isOwner) {
            ll_bottom.setVisibility(View.GONE);
        } else {
            ll_bottom.setVisibility(View.VISIBLE);
        }

        if (isAdmin || isOwner) {
            mAdapter.setAddRoomVisiable(true);
        } else {
            mAdapter.setAddRoomVisiable(false);
        }
    }

    private void loadRoom() {
        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Room tempRoom = (Room) data.getSerializable("room");
                    if (roomLocal == null && tempRoom != null) {//避免CoreService重复回调
                        dataList.add(0, tempRoom);
                        roomLocal = tempRoom;
                    }
                    if (roomLocal != null) {
                        inflateView(roomLocal);
                    }
                }
            }
        });
    }

    private void loadLives() {
        if (roomLocal == null) {//房间信息加载失败就不再加载lives
            return;
        }
        dataList.clear();
        dataList.add(0, roomLocal);
        liveService.fetchLiveList(roomId, 0, 20, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    ArrayList<LiveListElement> liveListElements = (ArrayList<LiveListElement>) data.getSerializable("live_list");
                    if (liveListElements != null && liveListElements.size() > 0) {
                        dataList.addAll(1, liveListElements);
                    }
                    dataList.add("节目");//节目条目
                    mAdapter.resetData(dataList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        historyService.fetchHistoryCount(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HistoryService.OK == e) {
                    int count = data.getInt("published_count");
                    if (count > 0) {
                        mAdapter.setHistoryVisiable(true);
                    } else {
                        mAdapter.setHistoryVisiable(false);
                    }
                }
            }
        });
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (HDApp.getInstance().currRoomDel || HDApp.getInstance().currUserPulledblack) {
//            HDChannelActivity.this.finish();
//            HDApp.getInstance().currRoomDel = false;//房间注销
//            HDApp.getInstance().currUserPulledblack = false;//被拉黑
//        } else {
//            loadLives();
//            setupRole();
//        }
//    }

    private void inflateView(Room room) {
        setActionBarTitle(room.getName());
        if (room != null && room.getIsCanceled()) {
            roomDeleted();
        }

    }

    private void addFollow(long roomId, long userId) {
        coreService.addFollow(roomId, userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Toast.makeText(HDChannelActivity.this, "加入频道成功", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.ll_bottom).setVisibility(View.GONE);
                } else {
                    Toast.makeText(HDChannelActivity.this, "加入频道失败", Toast.LENGTH_SHORT).show();
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

    private void roomDeleted() {
        new CommonDialog(HDChannelActivity.this,
                CommonDialog.ButtonType.oneButton,
                new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                        Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                        seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                        sendBroadcast(seeding);

                        HDChannelActivity.this.finish();
                    }

                    public void doNegativeClick() {
                    }
                }, "该频道已被频道主注销,你将自动退出该频道");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                Intent channelDetailActIntent = new Intent(this, HDChannelDetailActivity.class);
                channelDetailActIntent.putExtra(HDChannelDetailActivity.ROOM_ID, roomId);
                channelDetailActIntent.putExtra(HDChannelDetailActivity.IS_FOLLOW, false);
                channelDetailActIntent.putExtra(HDChannelDetailActivity.IS_FROM_CHAT_ACT, false);
                HDChannelActivity.this.startActivityForResult(channelDetailActIntent, 1);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                break;
            case R.id.ll_bottom:
                addFollow(roomId, userId);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_CREATE_LIVE_SUCCESS) {
            loadData();
        }
        if (resultCode == HDChannelDetailActivity.RESULT_CODE_DEL_ROOM) {
            HDChannelActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        coreService.deleteObserver(coreObserver);
    }
}
