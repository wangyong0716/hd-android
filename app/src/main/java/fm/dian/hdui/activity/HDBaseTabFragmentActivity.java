package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import fm.dian.hddata.util.RootUtil;
import fm.dian.hddata_android.core.CorePublish;
import fm.dian.hddata_android.live.LivePublish;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDBashTabFragmentAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.broadcast.HDUserChangedBroadCastReceiver;
import fm.dian.hdui.fragment.HDTab1Fragment;
import fm.dian.hdui.fragment.HDTab2Fragment;
import fm.dian.hdui.fragment.HDTab3Fragment;
import fm.dian.hdui.util.HDUiLog;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.view.CommonDialog.DialogClickListener;
import fm.dian.hdui.view.FixedViewPager;
import fm.dian.hdui.view.stickyHeader.engin.SimpleSectionedListAdapter;

@SuppressWarnings({"deprecation"})
@SuppressLint("InflateParams")
public class HDBaseTabFragmentActivity extends HDBaseActivity implements OnClickListener {
    private static final Logger logger = Logger.getLogger(HDBaseTabFragmentActivity.class);
    public static final int msg_what_tab1_seeding_show = 60;
    public static final int msg_what_tab1_seeding_cancel = 61;
    public static final int msg_what_leave_live = 62;
    public static final int msg_what_entered_chat_act = 63;
    public static final int msg_what_drop_headset = 64;
    public HDUiLog log = new HDUiLog();
    public static int REQUEST_CODE_ADD_ROOM = 10;
    public static int RESULT_CODE_ADD_ROOM = 11;
    public static final int TAB_INDEX = 0;
    public static final int TAB_HONGDIAN = 1;
    public static final int TAB_ME = 2;

    public static HDBaseTabFragmentActivity self;
    private FixedViewPager viewPager;
    public HDBashTabFragmentAdapter tabFragmentAdapter;
    private RadioButton main_tab_index, main_tab_hongdian, main_tab_me;
    private PopupWindow mPopupWindow;
    private HDUserChangedBroadCastReceiver userChangedBroadCastReceiver;

    public int chatActivityOnlineNumber = -1;
    public static String chooseBitmapPathRemote = null;
    public static String chooseBitmapPathLocal = null;
    public static boolean isShowSeedingBar = false;

    public ArrayList<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
    private List<SimpleSectionedListAdapter> selectionAdapterList = new ArrayList<SimpleSectionedListAdapter>();
    CoreService coreService = CoreService.getInstance();
    AuthService authService = AuthService.getInstance();
    MediaService mediaService = MediaService.getInstance();
    LiveService liveService = LiveService.getInstance();
    Observer coreObserver;
    Observer liveObserver;

    SeedingBarBroadcastReceiver seedingbarReceiver;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @SuppressWarnings("rawtypes")
    private HashMap<Class, BaseViewPagerListener> mapPageListeners = new HashMap<Class, BaseViewPagerListener>();


    public void addSectionAdapter(SimpleSectionedListAdapter adapter) {
        selectionAdapterList.add(adapter);
    }

    public void removeSectionAdapter(SimpleSectionedListAdapter adapter) {
        selectionAdapterList.remove(adapter);
    }

    public int getSelectionSize() {
        return sections.size();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_base_tab);
        initUI();
        addListener();

        addObserver();
        checkUpdate();
        checkCpuNeon();
        registerHeadsetPlugReceiver();
    }

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private void addObserver() {

        liveObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    Bundle bundle = (Bundle) data;
                    int type = bundle.getInt("publish_type");
                    if (LivePublish.LIVE_LIVE_PUBLISH == type) {
//                        Live live = (Live) bundle.getSerializable("live");
                        Live live = liveService.getLive();
                        if (live != null && live.getClosed()) {
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_leave_live);
                            sendBroadcast(seeding);
                        }
                    }
                }
            }
        };
        liveService.addObserver(liveObserver);

        coreObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Bundle bundle = (Bundle) data;
                int pubType = bundle.getInt("publish_type");
                switch (pubType) {
                    case CorePublish.ROOM_PUBLISH://房间变化(房间注销)
                        Room room = (Room) bundle.getSerializable("room");
                        if (room.getIsCanceled()) {
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                            sendBroadcast(seeding);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        coreService.addObserver(coreObserver);
    }

    public void initUI() {
        super.initActionBar(this);
        ib_action_bar_left.setVisibility(View.GONE);

        viewPager = (FixedViewPager) findViewById(R.id.viewpager);
        main_tab_index = (RadioButton) findViewById(R.id.main_tab_index);
        main_tab_hongdian = (RadioButton) findViewById(R.id.main_tab_hongdian);
        main_tab_me = (RadioButton) findViewById(R.id.main_tab_me);
        main_tab_index.setOnClickListener(this);
        main_tab_hongdian.setOnClickListener(this);
        main_tab_me.setOnClickListener(this);

        tabFragmentAdapter = new HDBashTabFragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);

        IntentFilter broadCastIntent = new IntentFilter(HDUserChangedBroadCastReceiver.INTENT_FILTER_ACTION);
        userChangedBroadCastReceiver = new HDUserChangedBroadCastReceiver();
        registerReceiver(userChangedBroadCastReceiver, broadCastIntent);


        //第三方app调用红点app
        if (HDApp.getInstance().threeAppEnterWebaddr != null && HDApp.getInstance().threeAppEnterWebaddr.length() > 0) {
            viewPager.postDelayed(new Runnable() {

                @Override
                public void run() {
                    threeAppEnter(HDApp.getInstance().threeAppEnterWebaddr);
                    HDApp.getInstance().threeAppEnterWebaddr = null;
                }
            }, 500);
        }
    }

    BaseViewPagerListener baseViewPagerListener;

    private void addListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int id) {
                switch (id) {
                    case TAB_INDEX:
                        main_tab_index.setChecked(true);
                        tv_common_action_bar_right.setVisibility(View.VISIBLE);
                        break;
                    case TAB_HONGDIAN:
                        main_tab_hongdian.setChecked(true);
                        tv_common_action_bar_right.setVisibility(View.GONE);
                        break;
                    case TAB_ME:
                        main_tab_me.setChecked(true);
                        tv_common_action_bar_right.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        seedingbarReceiver = new SeedingBarBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SeedingBarBroadcastReceiver.RECEIVER_KEY);
        this.registerReceiver(seedingbarReceiver, filter);
    }

    @SuppressWarnings("rawtypes")
    public void addPageListener(Class c, BaseViewPagerListener l) {
        this.mapPageListeners.put(c, l);
    }

    public interface BaseViewPagerListener {
        public void onCurrPage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_index:
                viewPager.setCurrentItem(TAB_INDEX);
                baseViewPagerListener = mapPageListeners.get(HDTab1Fragment.class);
                if (baseViewPagerListener != null) {
                    baseViewPagerListener.onCurrPage();
                }
                break;
            case R.id.main_tab_hongdian:
                viewPager.setCurrentItem(TAB_HONGDIAN);
                baseViewPagerListener = mapPageListeners.get(HDTab2Fragment.class);
                if (baseViewPagerListener != null) {
                    baseViewPagerListener.onCurrPage();
                }
                break;
            case R.id.main_tab_me:
                viewPager.setCurrentItem(TAB_ME);

                baseViewPagerListener = mapPageListeners.get(HDTab3Fragment.class);
                if (baseViewPagerListener != null) {
                    baseViewPagerListener.onCurrPage();
                }
                break;
            case R.id.ll_add_room:
                Intent intentAddRoom = new Intent(this, HDAddRoomActivity.class);
                startActivityForResult(intentAddRoom, REQUEST_CODE_ADD_ROOM);
                mPopupWindow.dismiss();
                break;
            case R.id.ll_create_room:
                Intent intentCreateRoom = new Intent(this, HDRoomCreateActivity.class);
                startActivity(intentCreateRoom);
                mPopupWindow.dismiss();
                break;
            case R.id.tv_common_action_bar_right:
                showAsDropDown(findViewById(R.id.tv_common_action_bar_right));
                break;
            case R.id.iv_seeding_bar_close://正在直播的banner条
                Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                sendBroadcast(seeding);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_ADD_ROOM) {
//			String roomId = data.getStringExtra("roomId");
//			Toast.makeText(HDBaseTabFragmentActivity.this, "添加roomid="+roomId, Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
        if(selectionAdapterList !=null ){
            selectionAdapterList.clear();
        }
        if(userChangedBroadCastReceiver !=null ){
            unregisterReceiver(userChangedBroadCastReceiver);
        }
        if(seedingbarReceiver !=null ){
            unregisterReceiver(seedingbarReceiver);
        }
        if(headsetPlugReceiver !=null){
            unregisterReceiver(headsetPlugReceiver); //耳机广播
        }
        super.onDestroy();
    }

    //下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {
        View menu_content = LayoutInflater.from(this).inflate(R.layout.popmenu, null);
        menu_content.findViewById(R.id.ll_add_room).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_create_room).setOnClickListener(this);

        mPopupWindow = new PopupWindow(menu_content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）

        mPopupWindow.showAsDropDown(parent, 10, 0);//popupwindow 显示在相对父容器x轴0像素，y轴0像素的位置下方

        mPopupWindow.setFocusable(true);// 使其聚集
        mPopupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        mPopupWindow.update();//刷新状态
    }


    public void appLogout() {
        ActivityManager.getInstance().popAllActivityExceptOne(this.getClass());
        if (userChangedBroadCastReceiver != null) {
            userChangedBroadCastReceiver.dismissDialog();
        }
        Intent intentMain = new Intent(this, HDNavigationActivity.class);
        startActivity(intentMain);
        finish();
    }

    private int mExitCounter = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mExitCounter++;

            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();

            if (mExitCounter == 1) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mExitCounter = 0;
                    }
                }, 2000);
                return true;
            }

            ActivityManager.getInstance().popAllActivityExceptOne(this.getClass());// 关闭所有已经打开的activity
            HDBaseTabFragmentActivity.this.finish();
            MobclickAgent.onKillProcess(this);//强杀前，关闭友盟统计
            //强制退出
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkUpdate() {
        final ConfigService configService = ConfigService.getInstance();
        if (configService.getUpdateNeedUpdate()) {
            ButtonType type = null;
            if (configService.getUpdateForceUpdate()) {
                type = ButtonType.oneButton;
            } else {
                type = ButtonType.TowButton;
            }

            CommonDialog commonDialog = new CommonDialog(HDBaseTabFragmentActivity.this, type, new DialogClickListener() {

                @Override
                public void doPositiveClick() {
                    String updateURL = configService.getUpdateUpdateUrl();
                    if (null != updateURL) {
                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(updateURL));
                        HDBaseTabFragmentActivity.this.startActivity(it);
                    }
                    if (configService.getUpdateForceUpdate()) {
                        HDBaseTabFragmentActivity.this.finish();
                    }
                }

                @Override
                public void doNegativeClick() {

                }
            }, configService.getUpdateUpdateDescription(), "", "", 0 == configService.getUpdateUpdateTitle().length() ? "更新提示" : configService.getUpdateUpdateTitle());
            commonDialog.setTouchOutsideAndCalceled(false, false);//设置dialog外部和返回键不可用
        }
    }

    private void checkCpuNeon() {
        RootUtil rootUtil = new RootUtil();
        if (!rootUtil.isNeon()) {
            new CommonDialog(HDBaseTabFragmentActivity.this, ButtonType.oneButton, new DialogClickListener() {

                @Override
                public void doPositiveClick() {
                    HDBaseTabFragmentActivity.this.finish();
                }

                @Override
                public void doNegativeClick() {

                }
            }, "不支持本设备,点击确定退出", "", "", "提示");

        }
    }

    //第三方app调用入口
    public void threeAppEnter(String webaddr) {
        if (webaddr != null) {
            coreService.searchRoom(webaddr, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (CoreService.OK == e) {
                        final Room room = (Room) data.getSerializable("room");
                        AuthService.getInstance().joinRoom(room.getRoomId(), "", new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int error_code = data.getInt("error_code");
                                if (AuthService.OK == error_code) {
                                    HDApp.getInstance().setCurrRoomId(room.getRoomId());

                                    Intent channelIntent = new Intent(HDBaseTabFragmentActivity.this, HDChannelActivity.class);
                                    channelIntent.putExtra("roomId", room.getRoomId());
                                    startActivity(channelIntent);
                                } else {
                                    Toast.makeText(HDBaseTabFragmentActivity.this, "error code=" + error_code, Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    } else {
                        Toast.makeText(HDBaseTabFragmentActivity.this, "webaddr错误", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public class SeedingBarBroadcastReceiver extends BroadcastReceiver {
     public static final String RECEIVER_KEY="fm.hd.hdui.seedingbar";
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",msg_what_tab1_seeding_cancel);
            switch (type){
                case msg_what_tab1_seeding_show:

                    isShowSeedingBar = true;
                    if (authService.getLiveId() != null) {
                        coreService.fetchRoomByRoomId(HDApp.getInstance().getCurrRoomId(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int error_code = data.getInt("error_code");
                                if (CoreService.OK == error_code) {
                                    Room room = (Room) data.getSerializable("room");

                                    sections.clear();
                                    String currLiveName = HDApp.getInstance().getCurrLiveName();
                                    sections.add(new SimpleSectionedListAdapter.Section(0, "".equals(currLiveName) ? room.getName() : currLiveName, room.getAvatar()));
                                    for (SimpleSectionedListAdapter adapter : selectionAdapterList) {
                                        adapter.setSections(sections.toArray(new SimpleSectionedListAdapter.Section[0]));
                                    }
                                    isShowSeedingBar = true;
                                }
                            }
                        });
                    }
                    break;
                case msg_what_tab1_seeding_cancel:
                    sections.clear();
                    for (SimpleSectionedListAdapter adapter : selectionAdapterList) {
                        adapter.setSections(sections.toArray(new SimpleSectionedListAdapter.Section[0]));
                    }
                    isShowSeedingBar = false;
//                    restartMedia();
                    //TODO 关闭录制
                    if (authService.getLiveId() != null) {
                        authService.leaveLive(authService.getLiveId(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int e = data.getInt("error_code");
                                if (AuthService.OK != e) {
                                    Toast.makeText(HDBaseTabFragmentActivity.this, "leave live" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        authService.leaveRoom(authService.getLiveId(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int e = data.getInt("error_code");
                                if (AuthService.OK == e) {
                                    HDApp.getInstance().setCurrRoomId(0);
                                } else {
                                    Toast.makeText(HDBaseTabFragmentActivity.this, "leave room" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    break;
                case msg_what_leave_live:
                    sections.clear();
                    for (SimpleSectionedListAdapter adapter : selectionAdapterList) {
                        adapter.setSections(sections.toArray(new SimpleSectionedListAdapter.Section[0]));
                    }
                    isShowSeedingBar = false;
//                    restartMedia();
                    Log.d(HongdianConstants.DEBUG_LIEYUNYE,"点击了关闭按钮");


                    //TODO 关闭录制

                    if (authService.getLiveId() != null) {
                        authService.leaveLive(authService.getLiveId(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int e = data.getInt("error_code");
                                if (AuthService.OK != e) {
                                    Toast.makeText(HDBaseTabFragmentActivity.this, "leave live" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    break;
                case msg_what_entered_chat_act:
                    isShowSeedingBar = true;
                    if (authService.getLiveId() != null) {
//                        Intent intent = new Intent(HDChatActivity.MediaPublishBradCastResever.RECEIVER_KEY);
//                        intent.putExtra("current_speakers", current_speakers);
//                        sendBroadcast(intent);
                    }
                    break;
                case msg_what_drop_headset:
                    mediaService.stopAudioRecord(new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            int e = data.getInt("error_code");
                            if (MediaService.OK == e) {

                            }
                        }
                    });
                    break;
                default:
                    break;
            }

        }
    }


    class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")){
                if (intent.getIntExtra("state", 0) == 0){ //拔出耳机
                    boolean needEarphone = ConfigService.getInstance().getNeedEarphone();
                    if(needEarphone){
                        boolean recordOpen = MediaService.getInstance().isAudioRecordOpen();
                        if(recordOpen){
                            //下麦   TODO
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_drop_headset);
                            sendBroadcast(seeding);
                            Toast.makeText(context,"请插入耳机后上麦",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else if (intent.getIntExtra("state", 0) == 1){ //插入耳机
//                Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
                }
            }

        }

    }
}
