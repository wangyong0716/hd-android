package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hddata_android.blackboard.BlackBoardPublish;
import fm.dian.hddata_android.core.CorePublish;
import fm.dian.hddata_android.groupchat.GroupChatPublish;
import fm.dian.hddata_android.history.HistoryPublish;
import fm.dian.hddata_android.live.LivePublish;
import fm.dian.hddata_android.media.MediaPublish;
import fm.dian.hddata_android.media.MediaRequst;
import fm.dian.hddata_android.online.OnlinePublish;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.BlackBoardService;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.GroupChatService;
import fm.dian.hdservice.HeartbeatService;
import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.OnlineService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.GroupChat;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.RoomUserIgnore;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDChatActivityMsgAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.GestureLinearLayout;
import fm.dian.hdui.view.GestureLinearLayout.HDChatActivityRecoderListener;
import fm.dian.hdui.view.GestureLinearLayout.onKybdsChangeListener;
import fm.dian.hdui.view.blackboard.BlackBoardLinearlayoutBottom;
import fm.dian.hdui.view.blackboard.BlackBoardLinearlayoutBottom.BlackBoardLinearlayoutListner;
import fm.dian.hdui.view.blackboard.BlackboardRelativeLayoutTop;
import fm.dian.hdui.view.blackboard.BlackboardRelativeLayoutTop.KeyboardListener;
import fm.dian.hdui.view.blackboard.RecordingRelativeLayout;
import fm.dian.hdui.view.blackboard.StageRelativeLayout;
import fm.dian.hdui.view.face.FaceRelativeLayout;
import fm.dian.hdui.view.video.VideoRelativeLayout;
import fm.dian.hdui.wximage.choose.ImageChooseActivity;
import fm.dian.hdui.wximage.choose.utils.ImageItem;
import fm.dian.service.media.HDMediaUserVersion;

@SuppressWarnings({"deprecation"})
@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class HDChatActivity extends HDBaseChatActivity implements
        HDChatActivityRecoderListener, BlackBoardLinearlayoutListner {

    private static final Logger log = Logger.getLogger(HDChatActivity.class);

    // List for chat messages
    ListView mListView;
    // Adaptor for messages
    private HDChatActivityMsgAdapter mAdapter;

    List<GroupChat> dataList = new ArrayList<GroupChat>();
    int lastIndex = 20;//记录上次列表滚动位置
    int page = 0;
    int pageMaxCount = 20;//每次加载数量
    //小黑板，舞台区，订阅条父容器
    LinearLayout ll_blackboard_parent;
    long currRoomId;
    long currUerId;
    long liveId;
    Room roomLocal;
    Live liveLocal;
    HeartbeatService heartbeatService = HeartbeatService.getInstance();
    MediaService mediaService = MediaService.getInstance();
    GroupChatService groupChatService = GroupChatService.getInstance();
    BlackBoardService blackBoardService = BlackBoardService.getInstance();
    ConfigService configService = ConfigService.getInstance();
    Observer liveObserver;
    Observer blackboardObserver;
    Observer groupChatObserver;
    Observer onlineObserver;
    Observer coreObserver;
    Observer historyObserver;
    Observer mediaObserver;
    private BlackBoardLinearlayoutBottom blackBoardLinearlayoutBottom;
    private GestureLinearLayout gr;
    private FaceRelativeLayout fr;
    private RelativeLayout rl_get_mic;
    private TextView tv_text_mic;
    private TextView tv_bg_mic;
    private TextView tv_send;
    private ImageView iv_blackboard;
    private EditText et_sendmessage;

    // 舞台区
    private StageRelativeLayout mStageRelativeLayout;
    // 小黑板(展示区域)
    private BlackboardRelativeLayoutTop blackboardRelativeLayoutTop;
    // 视频
    private VideoRelativeLayout videoRelativeLayout;
    //舞台区，小黑板，视频 容器
    RelativeLayout rl_stage_blackboard_video_container;

    //录制条
    private RecordingRelativeLayout recordingRelativeLayout;
    private boolean isSoftKeyShowed = false;//软键盘是否在最前边
    private CoreService coreService = CoreService.getInstance();
    private ActionAuthService actionAuthService = ActionAuthService.getInstance();
    private OnlineService onlineService = OnlineService.getInstance();
    private LiveService liveService = LiveService.getInstance();
    private HistoryService historyService = HistoryService.getInstance();
    List<RoomUserIgnore> userIgnoreList = new ArrayList<>();

    //    MediaPublishBradCastResever resever;
    private boolean isSameRoomId = false;
    private boolean isSilence = false;
    private HeadsetPlugReceiver headsetPlugReceiver;

    private LinearLayout mediaLineLayout;
    private Button mediaButton;//显示文案“连接中”和停止上麦
    private ImageView audioButton;//语音按钮
    private ImageView videoButton;//视频按钮

    @Override
    public void initUI() {
        super.initChatActionBar(this);
        // Try to fix crash with exception "NullPointerException"
        if (getIntent() == null) {
            finish();
        }

        setChatActionBarBg(R.color.action_bar_bg_color);
        isSameRoomId = getIntent().getBooleanExtra("isSameRoomId", false);
        liveId = getIntent().getLongExtra("liveId", 0);
        currRoomId = authService.getRoomIdOfCurrentLive();
        HDUser loginUser = new HDUserCache().getLoginUser();
        if (loginUser == null) {
            this.finish();
        }
        currUerId = loginUser.userId;

        authService.joinRoom(currRoomId, "", new CallBack() {
            @Override
            public void process(Bundle data) {
                log.debug("join live's room.error_code={}", data.getInt("error_code"));
            }
        });

        if (!HDBaseTabFragmentActivity.isShowSeedingBar || !isSameRoomId) {
            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
            seeding.putExtra("type", HDBaseTabFragmentActivity.msg_what_tab1_seeding_show);
            sendBroadcast(seeding);
        } else {
            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
            seeding.putExtra("type", HDBaseTabFragmentActivity.msg_what_entered_chat_act);
            sendBroadcast(seeding);
        }
        ib_chat_action_bar_right.setBackgroundResource(R.drawable.btn_chat_room_menu_bg_selector);
        ib_chat_action_bar_right.setOnClickListener(this);
        findViewById(R.id.ll_title).setOnClickListener(this);

        blackBoardLinearlayoutBottom = (BlackBoardLinearlayoutBottom) findViewById(R.id.ll_blackbord_bottom);
        iv_blackboard = (ImageView) findViewById(R.id.iv_blackboard);
        blackBoardLinearlayoutBottom.setEnterButtonAndListener(iv_blackboard, this);
        blackBoardLinearlayoutBottom.setFaceRootView(findViewById(R.id.ll_facechoose));

        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_send.setOnClickListener(this);
        et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
        et_sendmessage.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str != null && str.trim().length() > 0) {
                    tv_send.setVisibility(View.VISIBLE);
                    iv_blackboard.setVisibility(View.GONE);
                } else {
                    if (blackboardRelativeLayoutTop.getCanSliding()) {
                        tv_send.setVisibility(View.GONE);
                        iv_blackboard.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mListView = (ListView) findViewById(R.id.mListView);
        // 触摸ListView隐藏表情和输入法
//		mListView.setOnTouchListener(this);
//        mListView.setPullLoadEnable(false);
//        mListView.setPullRefreshEnable(false);
//        mListView.setXListViewListener(this);
        mAdapter = new HDChatActivityMsgAdapter(this, dataList);
        mListView.setAdapter(mAdapter);

        // To make scrolling more smooth
        mListView.setScrollingCacheEnabled(false);
        mListView.setAnimationCacheEnabled(false);

        //小黑板，舞台区，订阅条的父容器
        ll_blackboard_parent = (LinearLayout) this.findViewById(R.id.ll_blackboard_parent);

        // 小黑板
        blackboardRelativeLayoutTop = (BlackboardRelativeLayoutTop) findViewById(R.id.rl_blackboard);// 小黑板区域的麦上区域

        // video
        videoRelativeLayout = (VideoRelativeLayout) findViewById(R.id.rl_video);// 小黑板区域的麦上区域
//        videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(videoRelativeLayout.isBig()){
//                    hideOrShowActionBar();
//                }
//            }
//        });
        // 舞台区
        mStageRelativeLayout = (StageRelativeLayout) findViewById(R.id.mStageRelativeLayout);
//        hs.nobody();
        blackboardRelativeLayoutTop.setStageBar(mStageRelativeLayout);
        blackboardRelativeLayoutTop.addKeyboardListener(new KeyboardListener() {

            @Override
            public boolean isKeyboardShowed2Closed() {
                if (isSoftKeyShowed) {
                    SysTool.hideKeyBoard(HDChatActivity.this);
                }
                return isSoftKeyShowed;
            }

            @Override
            public boolean isFaceShowed2Closed() {
                if (fr.isFaceViewShow()) {
                    fr.hideFaceView();
                    return true;
                }
                return false;
            }

            @Override
            public boolean isBlackboardPanelShowed2Closed() {
                if (blackBoardLinearlayoutBottom.getVisibility() == View.VISIBLE) {
                    blackBoardLinearlayoutBottom.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        recordingRelativeLayout = (RecordingRelativeLayout) findViewById(R.id.rl_chat_room_recording);
        recordingRelativeLayout.setOnClickListener(this);

        rl_get_mic = (RelativeLayout) findViewById(R.id.rl_get_mic);
        tv_text_mic = (TextView) findViewById(R.id.tv_text_mic);
        tv_bg_mic = (TextView) findViewById(R.id.tv_bg_mic);
        gr = (GestureLinearLayout) findViewById(R.id.gr_contianer);
        gr.initData(rl_get_mic, this);


        gr.setOnkbdStateListener(new onKybdsChangeListener() {

            @Override
            public void onKeyBoardStateChange(int state) {
                if (state == GestureLinearLayout.keyboardshow) {
                    //键盘的监听 返回太慢 废弃了
//                    blackboardRelativeLayoutTop.setVisibility(View.INVISIBLE);
//                    mStageRelativeLayout.setVisibility(View.INVISIBLE);
                    isSoftKeyShowed = true;
                } else {
//                    blackboardRelativeLayoutTop.setVisibility(View.VISIBLE);
//                    mStageRelativeLayout.setVisibility(View.VISIBLE);
                    isSoftKeyShowed = false;
                }
            }
        });

        fr = (FaceRelativeLayout) findViewById(R.id.include_bottom);
        fr.setListView(mListView);

        rl_stage_blackboard_video_container = (RelativeLayout) findViewById(R.id.rl_stage_blackboard_video_container);
        addBlackbaordAndVideoListener();

        mediaLineLayout = (LinearLayout) findViewById(R.id.mediaLineLayout);
        mediaButton = (Button) findViewById(R.id.mediaButton);
        audioButton = (ImageView) findViewById(R.id.audioButton);
        videoButton = (ImageView) findViewById(R.id.videoButton);
        mediaButton.setOnClickListener(this);
        audioButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        try {
            // initUI may throw exception.
            initUI();
        } catch (Exception e) {
            finish();
        }

        echo();

        addObserverPublish();
        loadRoomData();
    }

    private void echo() {
        initMicBtnAndStage();//麦上列表
        initBlackboardBar();//小黑板
        updateBlackboardAndVideo();//视频区

        if (MediaService.getInstance().isVideoRecordOpen()) {
            videoRecordStartUI();
        }
        if (MediaService.getInstance().isAudioRecordOpen()) {
            audioRecordStartUI();
        }
    }

    private void addObserverPublish() {
        liveObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    Bundle bundle = (Bundle) data;
                    int type = bundle.getInt("publish_type");
                    if (LivePublish.LIVE_LIVE_PUBLISH == type) {
//                        Live live = (Live) bundle.getSerializable("live");
                        Live live = liveService.getLive();
                        if (live != null) {
                            liveLocal = live;
                            setupLiveProperty(live);
                        }
//                        User user = (User) bundle.getSerializable("user");
//                        if (user != null) {
//                            Toast.makeText(HDChatActivity.this, "live pub =" + user.getAvatar(), Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
            }
        };
        liveService.addObserver(liveObserver);

        blackboardObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    Bundle bundle = (Bundle) data;

                    int type = bundle.getInt("publish_type");
                    if (BlackBoardPublish.BLACKBOARD_PUBLISH == type) {
                        initBlackboardBar();
                    }
                }
            }
        };
        blackBoardService.addObserver(blackboardObserver);

        groupChatObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if (data != null) {
                    Bundle bundle = (Bundle) data;
                    int type = bundle.getInt("publish_type");
                    if (GroupChatPublish.GROUPCHAT_MESSAGE_PUBLISH == type) {
                        boolean isScroll2Bottom = mListView.getLastVisiblePosition() == mListView.getCount() - 1;

                        long last_message_id = bundle.getLong("last_message_id");
                        Set<GroupChat> groupChatArrayList = groupChatService.getChatSet();
                        if (null != groupChatArrayList) {
                            dataList.clear();
                            dataList.addAll(groupChatArrayList);
                            if (dataList.size() > 60) {
                                dataList = dataList.subList(dataList.size() - 60, dataList.size());
                            }
                            //列表在最后一条的位置才自动滚动显示新消息
                            if (isScroll2Bottom) {
                                scrollListView2Bottom();
                            }

                            chatFilter(dataList, userIgnoreList, false);
                        }
                    }
                }
            }
        };

        groupChatService.addObserver(groupChatObserver);
        onlineObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Bundle bundle = (Bundle) data;

                int type = bundle.getInt("publish_type");
                if (OnlinePublish.ONLINE_PUBLISH == type) {
                    long online_user_number = bundle.getLong("online_user_number");
                    setActionBarTitleSub(online_user_number + "人在线");
                }
            }
        };
        onlineService.addObserver(onlineObserver);


        mediaObserver = new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Bundle bundle = (Bundle) o;
                final int type = bundle.getInt("publish_type");
                if (MediaPublish.SPEAK_PUBLISH == type) {//上麦的pub
                    initMicBtnAndStage();
                } else if (MediaPublish.VIDEO_PUBLISH == type) {
                    updateBlackboardAndVideo();
                } else if (MediaPublish.USER_PUBLISH == type) {

                    //目前（2015-09-25）无法邀请Android用户上麦，但是可以被踢下麦
                    final int versionType = bundle.getInt("VersionType");
                    if (versionType == HDMediaUserVersion.MediaUserVersion.VersionType.KICKED.getNumber()) {
                        if (MediaService.getInstance().isAudioRecordOpen()) {
                            audioRecordStop();
                        }
                        if (MediaService.getInstance().isVideoRecordOpen()) {
                            videoRecordStop();
                        }
                    }
                }

            }
        };
        mediaService.addObserver(mediaObserver);

        coreObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Bundle bundle = (Bundle) data;
                int pubType = bundle.getInt("publish_type");
                switch (pubType) {
                    case CorePublish.ROOM_ADMIN_PUBLISH://加管理员
//                        ArrayList<RoomUserAdmin> hdRoomUserAdminArrayList = (ArrayList<RoomUserAdmin>) bundle.getSerializable("room_user_admin_list");
                        setupUserProperty();
                        break;
                    case CorePublish.ROOM_IGNORE_PUBLISH://拉黑
                        //TODO 过滤黑名单
                        userIgnoreList = (ArrayList<RoomUserIgnore>) bundle.getSerializable("room_user_ignore_list");
                        chatFilter(dataList, userIgnoreList, false);
                        setupUserProperty();
                        break;
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

        historyObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Bundle bundle = (Bundle) data;
                if (bundle == null) {
                    return;
                }
                int pubType = bundle.getInt("publish_type");
                switch (pubType) {
                    case HistoryPublish.RECORD_PUBLISH:
                        boolean isRecording = historyService.isRecording();
                        setupRecordingBar(isRecording);
                        break;
                    default:
                        break;
                }
            }
        };

        historyService.addObserver(historyObserver);
        registerHeadsetPlugReceiver(); //耳机插拔监听

//        blackboardRelativeLayoutTop.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                heartbeatService.fire();
//            }
//        }, 500);
    }

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private void initMicBtnAndStage() {//初始化麦上列表和上麦按钮状态
        List<Long> currentSpeakers = mediaService.getCurrentSpeakers();
        updateStage(currentSpeakers);
        if (currentSpeakers != null) {
            for (int i = 0; i < currentSpeakers.size(); i++) {
                if (currentSpeakers.get(i) == currUerId) {//自己在麦上,标红mic图标
//                    tv_text_mic.setText("");
//                    tv_text_mic.setBackgroundResource(R.drawable.btn_common_red_bg);
//                    tv_bg_mic.setVisibility(View.VISIBLE);
//                                mediaService.startRecord(liveId, currUerId);
                    gr.setNextStop(true);//设置按钮状态
                    continue;
                }
            }
        }

    }

    private void initBlackboardBar() {
        List<Long> cardIds = blackBoardService.getCardIds();
        Long currentCardId = blackBoardService.getCurrentCardId();

        if (cardIds != null && cardIds.size() > 0) {
            setChatActionBarBg(R.color.color_transparency);
            getActionBar().getCustomView().invalidate();

            blackboardRelativeLayoutTop.setVisibility(View.VISIBLE);
            blackboardRelativeLayoutTop.loadBlackBoardData(cardIds, currentCardId);
            mStageRelativeLayout.changeHeight(true);

            if (blackboardRelativeLayoutTop.getVisibility() == View.GONE && videoRelativeLayout.getVisibility() == View.VISIBLE) { //如果之前没有显示
                blackboardRelativeLayoutTop.toBig(false);
            }
        } else {
            setChatActionBarBg(R.color.action_bar_bg_color);
            getActionBar().getCustomView().invalidate();

            blackboardRelativeLayoutTop.setVisibility(View.GONE);
            mStageRelativeLayout.changeHeight(false);
            if (videoRelativeLayout.getVisibility() == View.VISIBLE) {
                videoRelativeLayout.toBig(true);
            }
        }
    }

    private void updateBlackboardAndVideo() {//视频窗口设置
        getActionBar().show();
        Long videoSpeaker = mediaService.getVideoSpeaker();
        if (videoSpeaker != null) {
            setChatActionBarBg(R.color.color_transparency);
            videoRelativeLayout.setVisibility(View.VISIBLE);
            videoRelativeLayout.setRoom_id(authService.getCurrentRoomId());
            videoRelativeLayout.setLive_id(authService.getLiveId());
            videoRelativeLayout.setUser_id(videoSpeaker);
            //videoRelativeLayout.startPlayer();
            videoRelativeLayout.startPlayer();

            videoRelativeLayout.toBig(true);
            if (blackboardRelativeLayoutTop.getVisibility() == View.VISIBLE) {
                blackboardRelativeLayoutTop.toBig(false);
                rl_stage_blackboard_video_container.bringChildToFront(blackboardRelativeLayoutTop);
            }
        } else {
            videoRelativeLayout.setVisibility(View.GONE);
            videoRelativeLayout.stopPlayer();

            if (blackboardRelativeLayoutTop.getVisibility() == View.VISIBLE) {
                blackboardRelativeLayoutTop.toBig(true);
            } else {
                setChatActionBarBg(R.color.action_bar_bg_color);
            }
        }


    }

    private void addBlackbaordAndVideoListener() {
        blackboardRelativeLayoutTop.addOnBlackboardToggleListener(new BlackboardRelativeLayoutTop.OnBlackboardToggleListener() {
            public void toBig() {
                if (videoRelativeLayout.getVisibility() == View.VISIBLE) {
                    videoRelativeLayout.toBig(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rl_stage_blackboard_video_container.bringChildToFront(videoRelativeLayout);
                                }
                            });

                        }
                    }, 20);
                }
            }

            public void toSmall() {
                rl_stage_blackboard_video_container.bringChildToFront(blackboardRelativeLayoutTop);
            }
        });

        videoRelativeLayout.addOnVideoToggleListener(new VideoRelativeLayout.OnVideoToggleListener() {
            public void toBig() {
                if (blackboardRelativeLayoutTop.getVisibility() == View.VISIBLE) {
                    blackboardRelativeLayoutTop.toBig(false);
                    rl_stage_blackboard_video_container.bringChildToFront(blackboardRelativeLayoutTop);
                }
            }

            public void toSmall() {
                rl_stage_blackboard_video_container.bringChildToFront(videoRelativeLayout);
            }
        });
    }

    private void chatFilter(List<GroupChat> dataList, List<RoomUserIgnore> roomUserIgnoreList, boolean isLoadHistory) {
        try {
            List<GroupChat> chatList = new ArrayList<>(dataList);
            for (RoomUserIgnore roomUserIgnore : roomUserIgnoreList) {
                Long userId = roomUserIgnore.getUserId();
                for (GroupChat chat : dataList) {
                    if (userId != null && userId.equals(chat.getUserId())) {
                        chatList.remove(chat);
                    }
                }
            }
            dataList = new ArrayList<>(chatList);
            if (dataList != null) {
                mAdapter.resetData(dataList);
                mAdapter.notifyDataSetChanged();
                if (isLoadHistory) {//首次拉去历史不用快速滚动 直接定位底部
                    mListView.setSelection(mAdapter.getCount());
                }
                mListView.setSmoothScrollbarEnabled(true);
            }
        } catch (Exception ex) {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                boolean isUser = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserUser);
                if (isSilence && isUser) {//全体禁言
                    Toast.makeText(HDChatActivity.this, "全体禁言", Toast.LENGTH_SHORT).show();
                    hideKeyBoard();
                    return;
                }
                final String sendMessage = et_sendmessage.getText().toString().trim();
                if ("".equals(sendMessage)) {
                    return;
                }
                GroupChat chat = new GroupChat();
                chat.setText(sendMessage);
                chat.setUserId(currUerId);
                dataList.add(chat);
                mAdapter.resetData(dataList);
                mAdapter.notifyDataSetChanged();
                et_sendmessage.setText("");

                groupChatService.sendText(liveId, currUerId, sendMessage, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        scrollListView2Bottom();
                    }
                });
                break;
            case R.id.ib_action_bar_right:
                mPopupWindow.showAsDropDown(ib_chat_action_bar_right);
                break;
            case R.id.ll_title:
                Intent onlineIntent = new Intent(this, HDOnlieMemberActivity.class);
                Long rid = HDApp.getInstance().getCurrRoomId();
                if (rid != null && rid != 0) {
                    onlineIntent.putExtra(HDOnlieMemberActivity.KEY, "user");
                    onlineIntent.putExtra(HDOnlieMemberActivity.ROOMID, rid);
                    startActivityForResult(onlineIntent, 1);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                break;
//            case R.id.rl_blackboard_mic:// 切换小黑板和当前上麦头像
//                blackboardRelativeLayoutTop.toggle();
//                break;
            case R.id.rl_chat_room_recording:// 录制条点击结束录制
                stopRecordUi();
                break;
            case R.id.ll_item1:// 分享
                Intent shareIntent = new Intent(HDChatActivity.this, HDShareActivity.class);
                shareIntent.putExtra(HDShareActivity.ROOM_ID, currRoomId);
                shareIntent.putExtra(HDShareActivity.LIVE_ID, liveId);
                HDChatActivity.this.startActivity(shareIntent);
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
                mPopupWindow.dismiss();
                break;
            case R.id.ll_item2:// 举报
                // TODO 举报功能未实现
                mPopupWindow.dismiss();
                break;
            case R.id.ll_item3:// 直播设置
                Intent liveSettingIntent = new Intent(this, HDCreateUpdateLiveActivity.class);
                liveSettingIntent.putExtra("roomId", currRoomId);
                liveSettingIntent.putExtra("liveId", liveId);
                liveSettingIntent.putExtra("isUpdate", true);
                startActivityForResult(liveSettingIntent, HDCreateUpdateLiveActivity.result_code_update_success);
                mPopupWindow.dismiss();
                break;
            case R.id.mediaButton:
                if (MediaService.getInstance().isAudioRecordOpen()) {
                    audioRecordStop();
                }
                if (MediaService.getInstance().isVideoRecordOpen()) {
                    videoRecordStop();
                }
                log.error("stop media speak");
                break;
            case R.id.audioButton://语音上麦
                log.error("start audio speak");
                audioRecordStart();
                break;
            case R.id.videoButton://视频上麦
                log.error("start video speak");
                videoRecordStart();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ImageChooseActivity.result_code_selected_images:
                Bundle resultBundle = data.getBundleExtra("resultBundle");
                ArrayList<ImageItem> imagesList = (ArrayList<ImageItem>) resultBundle.getSerializable("imageList");
//                Toast.makeText(HDChatActivity.this, "图片" + imagesList.get(0).getImagePath(), Toast.LENGTH_SHORT).show();
                if (imagesList != null && imagesList.size() > 0) {
                    if (videoRelativeLayout.getVisibility() == View.VISIBLE) {
                        videoRelativeLayout.toBig(false);
                        blackboardRelativeLayoutTop.toBig(true);
                    }
                    //显示上传进度 >开始上传又拍云>关闭进度条
                    uploadBlackboardImage(imagesList, blackboardRelativeLayoutTop);
                }
                break;
            case HDBlackboardTxt.RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS:
                String txt = data.getStringExtra("txt");
//                Toast.makeText(HDChatActivity.this, "图片 文本" + txt, Toast.LENGTH_SHORT).show();
                if (txt != null && txt.length() > 0) {
                    uploadBlackboardTxt(txt, blackboardRelativeLayoutTop);
                }
                break;
            case HDCreateUpdateLiveActivity.result_code_update_success:
                boolean isClosed = data.getBooleanExtra("isClosed", false);
                if (isClosed) {
                    Toast.makeText(HDChatActivity.this, "房间已关闭", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                String liveName = data.getStringExtra("liveName");
                isSilence = data.getBooleanExtra("isSilence", false);
                setActionBarTitle(liveName);
                break;
            default:
                break;
        }
    }

    private void onPubChangeMicBtn(boolean isSpeaking) {
        if (isSpeaking) {
            tv_text_mic.setText("");
            tv_text_mic.setBackgroundResource(R.drawable.btn_common_red_bg);
//            tv_bg_mic.setVisibility(View.VISIBLE);
        } else {
            tv_text_mic.setText("上麦");
            tv_text_mic.setBackgroundResource(R.drawable.btn_common_white_bg);
//            tv_bg_mic.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveService.deleteObserver(liveObserver);
        blackBoardService.deleteObserver(blackboardObserver);
        groupChatService.deleteObserver(groupChatObserver);
        onlineService.deleteObserver(onlineObserver);
        coreService.deleteObserver(coreObserver);
        historyService.deleteObserver(historyObserver);
        mediaService.deleteObserver(mediaObserver);

        unregisterReceiver(headsetPlugReceiver); //耳机广播
//        unregisterReceiver(resever);
    }

    // 上麦
    @Override
    public void start() {
//        audioStart();
    }

    private void audioRecordStartUI() {
        mediaButton.setVisibility(View.VISIBLE);
        mediaLineLayout.setVisibility(View.GONE);
        mediaButton.setText("");
        tv_bg_mic.setBackgroundResource(R.drawable.chat_activity_buttom_bottom_get_mic);
        tv_bg_mic.setVisibility(View.VISIBLE);
    }

    public void audioRecordStart() {
        if (configService.getNeedEarphone()) {
            if (!isHeadset()) {//判断是否有耳机
                new CommonDialog(this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                    }

                    public void doNegativeClick() {
                    }
                }, "当前设备直接上麦可能会出现回声，请插入耳机后上麦");
                gr.setNextStop(false);
                return;
            }
        }

        if (mediaService.getVideoSpeaker() != null) {
            new CommonDialog(this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                public void doPositiveClick() {
                }

                public void doNegativeClick() {
                }
            }, "暂不支持视频直播与语音直播并存");
            gr.setNextStop(false);
            return;
        }

        mediaButton.setVisibility(View.VISIBLE);
        mediaLineLayout.setVisibility(View.GONE);
        mediaButton.setText(R.string.act_chat_room_btn_connecting_text);

        findViewById(R.id.ib_change_type).setTag("speaking");

        List<Long> currentSpeakers = mediaService.getCurrentSpeakers();
        if (currentSpeakers != null && currentSpeakers.size() > 4) {
            showVoicePopupWindow(findViewById(R.id.gr_contianer), 3);
            dismissVoicePopupWindow();

            new CommonDialog(HDChatActivity.this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                public void doPositiveClick() {

                }

                public void doNegativeClick() {
                }
            }, "已达到最大上麦人数", "", "", "上麦失败");
            return;
        }

        showVoicePopupWindow(findViewById(R.id.gr_contianer), 0);
        mediaService.startAudioRecord(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MediaService.OK == e) {
                    mediaButton.setText("");
                    tv_bg_mic.setBackgroundResource(R.drawable.chat_activity_buttom_bottom_get_mic);
                    tv_bg_mic.setVisibility(View.VISIBLE);
                    dismissVoicePopupWindow();
                } else {
                    mediaButton.setVisibility(View.GONE);
                    mediaLineLayout.setVisibility(View.VISIBLE);
                    if (MediaService.FULL == e) {
                        showVoicePopupWindow(findViewById(R.id.gr_contianer), 3);
                        dismissVoicePopupWindow();
                        new CommonDialog(HDChatActivity.this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                            public void doPositiveClick() {
                            }

                            public void doNegativeClick() {
                            }
                        }, "已达到最大上麦人数", "", "", "上麦失败");
                    } else {
                        showVoicePopupWindow(findViewById(R.id.gr_contianer), 3);
                        dismissVoicePopupWindow();
                    }
                }
            }
        });
    }

    public void audioRecordStop() {
        mediaButton.setVisibility(View.GONE);
        mediaLineLayout.setVisibility(View.VISIBLE);
        tv_bg_mic.setVisibility(View.GONE);
        mediaService.stopAudioRecord(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MediaService.OK == e) {
                    log.error("audio stoped");
                }
            }
        });

//        mediaService.isRecordOpen()
    }

    private void videoRecordStartUI() {
        mediaButton.setVisibility(View.VISIBLE);
        mediaLineLayout.setVisibility(View.GONE);
        mediaButton.setText("");
        tv_bg_mic.setVisibility(View.VISIBLE);
        tv_bg_mic.setBackgroundResource(R.drawable.shipinicon);

    }

    private void videoRecordStart() {
        if (configService.getNeedEarphone()) {
            if (!isHeadset()) {//判断是否有耳机
                new CommonDialog(this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                    }

                    public void doNegativeClick() {
                    }
                }, "当前设备直接上麦可能会出现回声，请插入耳机后上麦");
                gr.setNextStop(false);
                return;
            }
        }
        mediaButton.setText(R.string.act_chat_room_btn_connecting_text);
        mediaButton.setVisibility(View.VISIBLE);
        mediaLineLayout.setVisibility(View.GONE);
        showVoicePopupWindow(findViewById(R.id.gr_contianer), 0);
        mediaService.startVideoRecord(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MediaService.OK == e) {
                    mediaButton.setText("");
                    tv_bg_mic.setVisibility(View.VISIBLE);
                    tv_bg_mic.setBackgroundResource(R.drawable.shipinicon);

                    dismissVoicePopupWindow();
                } else {
                    mediaButton.setVisibility(View.GONE);
                    mediaLineLayout.setVisibility(View.VISIBLE);
                    if (MediaService.FULL == e) {
                        showVoicePopupWindow(findViewById(R.id.gr_contianer), 3);
                        dismissVoicePopupWindow();
                        new CommonDialog(HDChatActivity.this, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                            public void doPositiveClick() {
                            }

                            public void doNegativeClick() {
                            }
                        }, "已达到最大上麦人数", "", "", "上麦失败");
                    } else {
                        showVoicePopupWindow(findViewById(R.id.gr_contianer), 3);
                        dismissVoicePopupWindow();
                    }
                }
            }
        });
    }

    private void videoRecordStop() {
        mediaButton.setVisibility(View.GONE);
        mediaLineLayout.setVisibility(View.VISIBLE);
        tv_bg_mic.setVisibility(View.GONE);
        Log.d(HongdianConstants.DEBUG_LIEYUNYE, "" + AuthService.getInstance().getUserId() + "  " + MediaRequst.videoLivingUserId());
        mediaService.stopVideoRecord(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MediaService.OK == e) {
                    log.error("video stoped");
                } else {
                    Log.d(HongdianConstants.DEBUG_LIEYUNYE, "" + e);
                }
            }
        });
    }


    // 结束上麦
    @Override
    public void over() {
        log.debug("stop speak");
        overMic();
        findViewById(R.id.ib_change_type).setTag("");
    }

    private void overMic() {
        mediaService.stopAudioRecord(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (MediaService.OK == e) {
                    tv_text_mic.setText("上麦");
                    mediaButton.setVisibility(View.GONE);
                    tv_bg_mic.setVisibility(View.GONE);
                    mediaLineLayout.setVisibility(View.VISIBLE);
//                    tv_bg_mic.setBackgroundResource(R.drawable.btn_common_white_bg);
//                    tv_bg_mic.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void lockMic() {//禁用滑动锁住上麦
//        showVoicePopupWindow(findViewById(R.id.gr_contianer), 1);
//        dismissVoicePopupWindow();
    }

    @Override
    public void releaseMic() {
//        showVoicePopupWindow(findViewById(R.id.gr_contianer), 2);
    }

    private void inflateView(Room room) {
        setActionBarTitleSub("1人在线");
        if (null != room) {
            mAdapter.setMenuTitle(room.getName());
        }

        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fr.hideFaceView();
                fr.hideKeyBoard();
                blackBoardLinearlayoutBottom.hideBlackBoard();
                return false;
            }
        });
    }

    private void loadRoomData() {
        coreService.fetchIgnoreByRoomId(currRoomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    userIgnoreList = (ArrayList<RoomUserIgnore>) data.getSerializable("room_user_ignore_list");
                }
            }
        });

        groupChatService.loadHistory(liveId, new CallBack() {
            @Override
            public void process(Bundle data) {
                Set<GroupChat> groupChatArrayList = groupChatService.getChatSet();
                if (null != groupChatArrayList) {
                    dataList.addAll(groupChatArrayList);
                    chatFilter(dataList, userIgnoreList, true);
//                        mAdapter.resetData(dataList);
//                        mAdapter = new HDChatActivityMsgAdapter(HDChatActivity.this, dataList);
//                        mAdapter.notifyDataSetChanged();
//                    scrollListView2Bottom();
                }
            }
        });

        setupRecordingBar(historyService.isRecording());

        coreService.fetchRoomByRoomId(currRoomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    roomLocal = (Room) data.getSerializable("room");
                    if (roomLocal != null) {
                        inflateView(roomLocal);
                    }

                }
            }
        });
        setupUserProperty();


        long[] liveIds = new long[1];
        liveIds[0] = liveId;
        liveService.fetchLiveInfo(liveIds, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (LiveService.OK == e) {
                    ArrayList<Live> lives = (ArrayList<Live>) data.getSerializable("lives");
                    if (lives != null && lives.size() > 0) {
                        Live live = lives.get(0);
                        liveLocal = live;
                        setupLiveProperty(live);
                    }
                }
            }
        });
    }

    private void scrollListView2Bottom() {
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }
        }, 300);
    }

    private void setupLiveProperty(Live live) {
        if (live.getClosed()) {
            CommonDialog dialog = new CommonDialog(HDBaseTabFragmentActivity.self, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                public void doPositiveClick() {
                    HDChatActivity.this.finish();
                }

                public void doNegativeClick() {
                }
            }, "当前房间已被管理员删除你将自动退出该房间");
            dialog.setTouchOutsideAndCalceled(false, false);
        }
        if (live == null) {
            return;
        }
        final boolean isIgnore = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserIgnore);
        boolean isAdmin = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserOwner);
        boolean isUser = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserUser);
        setActionBarTitle(live.getName());
        if (live.getSilence()) {//是否被禁言了
            isSilence = true;

            if (!isAdmin && !isOwner) {
                et_sendmessage.setHint("成员禁言状态,暂不能发言");
                et_sendmessage.setEnabled(false);
                fr.disableBlackboardBtn(isSilence);//禁言时不能打开表情和小黑板选项
                blackBoardLinearlayoutBottom.disableFaceBtn(isSilence);
            }
        } else {
            isSilence = false;
            et_sendmessage.setHint("");
            et_sendmessage.setEnabled(true);

            fr.disableBlackboardBtn(isSilence);//禁言时不能打开表情和小黑板选项
            blackBoardLinearlayoutBottom.disableFaceBtn(isSilence);
        }


        if (live.getFree_live()) {
            if (isIgnore) {//黑名单中的人不能上麦
                fr.goneMicOrAddIcon(true, true);
                fr.disableBlackboardBtn(isSilence);//禁言时不能打开表情和小黑板选项
                over();
            } else if (isOwner || isAdmin) {
                fr.goneMicOrAddIcon(false, false);
                fr.disableBlackboardBtn(false);//禁言时不能打开表情和小黑板选项
            } else {//没有订阅的用户也能自由上麦（所有人都能上麦）
                fr.goneMicOrAddIcon(false, true);
                fr.disableBlackboardBtn(isSilence);//禁言时不能打开表情和小黑板选项
                tv_send.setVisibility(View.GONE);
            }
        } else {//非自由上麦
            if (isAdmin || isOwner) {//非自由上麦模式只有管理员和房间主可以上麦
                fr.goneMicOrAddIcon(false, false);
            } else {
                fr.goneMicOrAddIcon(true, true);
                over();
            }
        }
    }

    private void setupUserProperty() {
        boolean isAdmin = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserOwner);
        boolean isUser = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserUser);
        final boolean isIgnore = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserIgnore);

        if (isOwner) {
            createPopupWindow(2);
            fr.goneMicOrAddIcon(false, false);
            blackboardRelativeLayoutTop.setCanSliding(true);
            blackBoardLinearlayoutBottom.disableFaceBtn(false);

            et_sendmessage.setHint("");
            et_sendmessage.setEnabled(true);
            fr.disableBlackboardBtn(false);//禁言时不能打开表情和小黑板选项
            blackBoardLinearlayoutBottom.disableFaceBtn(false);
        } else if (isAdmin) {
            createPopupWindow(1);
            fr.goneMicOrAddIcon(false, false);
            blackboardRelativeLayoutTop.setCanSliding(true);
            blackBoardLinearlayoutBottom.disableFaceBtn(false);
//            blackboardRelativeLayoutTop.refresh();

            et_sendmessage.setHint("");
            et_sendmessage.setEnabled(true);
            fr.disableBlackboardBtn(false);//禁言时不能打开表情和小黑板选项
            blackBoardLinearlayoutBottom.disableFaceBtn(false);
        } else {
            createPopupWindow(0);
//            overMic();
            fr.goneMicOrAddIcon(true, true);
            blackboardRelativeLayoutTop.setCanSliding(false);
        }

        if (isIgnore) {
            over();
            blackListMan();
        } else {
            tv_send.setOnClickListener(HDChatActivity.this);
        }

        if (liveLocal != null) {//角色变化了 房间的相应设置也不适用了
            setupLiveProperty(liveLocal);
        }
    }

    private void setupRoomProperty(Room room) {
        Boolean isCanceled = room.getIsCanceled();
        if (isCanceled) {//房间注销了
            CommonDialog dialog = new CommonDialog(HDChatActivity.this,
                    CommonDialog.ButtonType.oneButton,
                    new CommonDialog.DialogClickListener() {
                        public void doPositiveClick() {
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type", HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
                            sendBroadcast(seeding);
                            HDApp.getInstance().currRoomDel = true;
                            HDChatActivity.this.finish();
                        }

                        public void doNegativeClick() {
                        }
                    }, "该频道已被频道主注销,你将自动退出该频道");
            dialog.setTouchOutsideAndCalceled(false, false);
        }
    }

    private void blackListMan() {
        new CommonDialog(HDChatActivity.this,
                CommonDialog.ButtonType.oneButton,
                new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                        Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                        seeding.putExtra("type", HDBaseTabFragmentActivity.msg_what_leave_live);
                        sendBroadcast(seeding);
                        HDApp.getInstance().currUserPulledblack = true;
                        HDChatActivity.this.finish();
                    }

                    public void doNegativeClick() {
                    }
                }, "你已被频道管理员拉黑");
    }

    // 更新舞台区头像
    private void updateStage(List<Long> userIds) {
        if (userIds != null && userIds.size() > 0) {
            mStageRelativeLayout.setVisibility(View.VISIBLE);
            mStageRelativeLayout.updateUsers(userIds);
        } else {
            mStageRelativeLayout.nobody();
            tv_text_mic.setText("上麦");
            tv_text_mic.setBackgroundResource(R.drawable.btn_common_white_bg);
//            tv_bg_mic.setVisibility(View.GONE);
            // 隐藏小黑板上的麦
            blackboardRelativeLayoutTop.nobodyOnMic();
        }
    }

    private void stopRecordUi() {
        new CommonDialog(this, CommonDialog.ButtonType.TowButton, new CommonDialog.DialogClickListener() {

            @Override
            public void doPositiveClick() {//结束录制
                historyService.stopRecord(new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (HistoryService.OK == e) {
                            long history_id = data.getLong("history_id");
                            recordingRelativeLayout.stop(history_id);
                        }
                    }
                });
            }

            @Override
            public void doNegativeClick() {
            }
        }, "确认结束录制？");
    }

    private void setupRecordingBar(boolean isRecording) {
        boolean isAdmin = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = actionAuthService.isRole(currUerId, currRoomId, liveId, AuthActionRequest.UserAuthType.UserOwner);
        if (isAdmin || isOwner) {//管理员和频道主
            if (isRecording) {
                historyService.getRecordTime(new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (HistoryService.OK == e) {
                            long record_time = data.getLong("record_time");
                            recordingRelativeLayout.start(record_time);
                            recordingRelativeLayout.setVisibility(View.VISIBLE);
                            blackBoardLinearlayoutBottom.setupRecordingState(true);
                        }
                    }
                });
            } else {
                recordingRelativeLayout.setVisibility(View.GONE);
                blackBoardLinearlayoutBottom.setupRecordingState(false);
            }
        }
    }

    @Override
    public void startRecording() {
        blackBoardLinearlayoutBottom.setVisibility(View.GONE);//关闭底部菜单
        historyService.startRecord(new CallBack() {//开启录制
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HistoryService.OK == e) {
                    recordingRelativeLayout.start(0);//从0开启计时器
                }
            }
        });
    }

    @Override
    public void stopRecording(long id) {
        recordingRelativeLayout.stop(id);
    }

    @Override
    public void onBlackBoard() {
        Intent chooseImage = new Intent(this, ImageChooseActivity.class);
        chooseImage.putExtra("isFromChatAct", true);
        chooseImage.putExtra("type", 2);
        startActivityForResult(chooseImage, 1);
        blackBoardLinearlayoutBottom.setVisibility(View.GONE);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard() {
        // 收起软键盘
        InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) { //拔出耳机
                    boolean audioRecordOpen = MediaService.getInstance().isAudioRecordOpen(); //音频上麦
                    if (audioRecordOpen) {
                        new CommonDialog(context, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                            public void doPositiveClick() {
                            }

                            public void doNegativeClick() {
                            }
                        }, "请插入耳机后再上麦");
                        over();
                    }

                    boolean isVideoRecord = MediaService.getInstance().isVideoRecordOpen(); //视频直播
                    if (isVideoRecord) {
                        new CommonDialog(context, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                            public void doPositiveClick() {
                            }

                            public void doNegativeClick() {
                            }
                        }, "请插入耳机后再直播");
                        videoRecordStop();
                    }

                } else if (intent.getIntExtra("state", 0) == 1) { //插入耳机
//                Toast.makeText(context, "headset connected", Toast.LENGTH_LONG).show();
                }
            }

        }

    }
}
