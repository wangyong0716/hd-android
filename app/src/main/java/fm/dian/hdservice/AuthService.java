package fm.dian.hdservice;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

import fm.dian.hddata_android.auth.AuthRequest;
import fm.dian.hddata_android.auth.AuthResponse;
import fm.dian.hddata_android.subscribe.SubscribeListRequest;
import fm.dian.hddata_android.subscribe.SubscribeListResponse;
import fm.dian.hdservice.api.AuthServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class AuthService extends BaseService implements AuthServiceInterface {

    private static final Logger log = Logger.getLogger(AuthService.class, LOG_TAG);
    private static AuthService authService;

    public static final int NEED_PASSWORD = 1100;
    public static final int PASSWORD_INVALID = 1101;
    public static final int IN_BLACKLIST = 1102;
    private long userId;
    private Stack<Long> roomIds;
    private Long liveId;
    private Long roomIdOfCurrentLive;
    private String password;

    private final MediaService mediaService = MediaService.getInstance();
    private final BlackBoardService blackBoardService = BlackBoardService.getInstance();
    private final GroupChatService groupChatService = GroupChatService.getInstance();
    private final HeartbeatService heartbeatService = HeartbeatService.getInstance();
    private final LiveService liveService = LiveService.getInstance();
    private final HistoryService historyService = HistoryService.getInstance();


    private AuthService(int serviceType) {
        super(serviceType);
        roomIds = new Stack<Long>();
    }

    public static AuthService getInstance() {
        if (authService == null) {
            authService = new AuthService(ServiceType.AUTH_VALUE);
        }
        return authService;
    }

    @Override
    public void joinRoom(final long roomId, String passwd, final CallBack callBack) {
        AuthRequest.joinRoom(roomId, passwd, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    roomIds.push(roomId);
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void leaveRoom(final long roomId, final CallBack callBack) {
        AuthRequest.leaveRoom(roomId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    roomIds.remove(roomId);
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void joinLive(final long lid, final String passwd, final CallBack callBack) {

        AuthRequest.joinLive(lid, passwd, new AuthResponse() {
            @Override
            public void response(int error_code, Object client_data) {
                final Message message = Message.obtain(handler, BaseService.RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                if (error_code == OK) {
                    if (liveId == null || lid != liveId) {
                        liveId = lid;
                        password = passwd;
                        heartbeatService.fire();
//                        clear();
                        LiveService.getInstance().fetchLiveInfo(new long[]{liveId}, new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int error_code = data.getInt("error_code");
                                if (error_code == LiveService.OK) {
                                    ArrayList<Live> lives = (ArrayList<Live>) data.get("lives");
                                    if (lives != null && lives.size() == 1) {
                                        Long roomid = lives.get(0).getRoom_id();
                                        roomIdOfCurrentLive = roomid;
                                        message.sendToTarget();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void leaveLive(final long lid, final CallBack callBack) {
        if (mediaService.isAudioRecordOpen()) {
            mediaService.stopAudioRecord(new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (MediaService.OK == e) {
                        Log.d(HongdianConstants.DEBUG_LIEYUNYE, "audio stoped");
                    }
                }
            });
        }

        if (mediaService.isVideoRecordOpen()) {
            mediaService.stopVideoRecord(new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (MediaService.OK == e) {
                        Log.d(HongdianConstants.DEBUG_LIEYUNYE, "video stoped");
                    } else {
                        Log.d(HongdianConstants.DEBUG_LIEYUNYE, "" + e);
                    }
                }
            });
        }
        clear();
        Log.d(HongdianConstants.DEBUG_LIEYUNYE, "leaveLive");

        AuthRequest.leaveLive(lid, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK && liveId != null && lid == liveId) {
//                    clear();
                    liveId = null;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);


        log.debug("leaveLive:liveId={}", lid);
    }

    @Override
    public void setUserId(long userId) {
        AuthRequest.setUserId(userId);
        this.userId = userId;
        SubscribeListRequest.getSubscribeRoomsByOffset(0, Integer.MAX_VALUE, new SubscribeListResponse() {
            @Override
            public void response(int error_code, Object[] bytes, Object client_data) {
                log.debug("initial subscribe list.error_code={}", error_code);
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setToken(String token) {
        AuthRequest.setUserToken(token);
    }

    public Long getCurrentRoomId() {
        return roomIds.peek();
    }

    public Long getLiveId() {
        return liveId;
    }

    public String getPassword() {
        return password;
    }

    public void reset() {
        long[] roomIds = AuthRequest.getRoomIds();
        for (long roomid : roomIds) {
            AuthRequest.removeRoomId(roomid);
        }
        AuthRequest.resetLiveId();
    }

    public Long getRoomIdOfCurrentLive() {
        return roomIdOfCurrentLive;
    }

    private void clear() {
        mediaService.clear();
        blackBoardService.clear();
        groupChatService.clear();
        liveService.clear();
        historyService.clear();
    }
}
