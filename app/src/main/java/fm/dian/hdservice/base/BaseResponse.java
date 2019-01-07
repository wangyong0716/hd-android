package fm.dian.hdservice.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import fm.dian.hddata_android.HDData;
import fm.dian.hddata_android.auth.AuthRequest;
import fm.dian.hddata_android.auth.AuthResponse;
import fm.dian.hddata_android.blackboard.BlackBoardResponse;
import fm.dian.hddata_android.core.CoreResponse;
import fm.dian.hddata_android.groupchat.GroupChatResponse;
import fm.dian.hddata_android.homepage.HomePageResponse;
import fm.dian.hddata_android.live.LiveResponse;
import fm.dian.hddata_android.media.MediaResponse;
import fm.dian.hddata_android.online.OnlineResponse;
import fm.dian.hddata_android.subscribe.SubscribeListResponse;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.HDDataService;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.broadcast.HDUserChangedBroadCastReceiver;

/**
 * Created by tinx on 5/7/15.
 */
public abstract class BaseResponse implements AuthResponse,
        CoreResponse,
        LiveResponse,
        BlackBoardResponse,
        GroupChatResponse,
        HomePageResponse,
        MediaResponse,
        OnlineResponse,
        SubscribeListResponse {
    private static final Logger log = Logger.getLogger(BaseResponse.class);

    private AuthService authService = AuthService.getInstance();

    protected CallBack callBack;

    protected Handler handler;

    private HDDataService hdDataService = HDDataService.getInstance();

    protected BaseResponse(CallBack callBack, Handler handler) {
        this.callBack = callBack;
        this.handler = handler;
    }

    @Override
    public void response(int error_code, Object client_data) {
        process(error_code, null, client_data);
    }

    @Override
    public void response(int error_code, Object[] bytes, Object client_data) {
        process(error_code, bytes, client_data);

    }

    protected abstract boolean parseData(Object bytes, Bundle bundle);

    @Override
    public void response(int error_code, byte[] bytes, Object client_data) {
        process(error_code, bytes, client_data);
    }

    private void process(int error_code, Object data, Object client_data) {
        Message message = Message.obtain(handler, BaseService.RESPONSE, callBack);
        Bundle bundle = new Bundle();
        bundle.putInt("error_code", error_code);
//        if (error_code == BaseService.OK && null != data) {
        if (error_code == BaseService.OK) {
            if (!parseData(data, bundle)) {
                //解析数据失败直接返回避免ui层崩溃
                return;
            }
        } else {
            //TODO error_code 处理
            switch (error_code) {
                case BaseService.FORBIDDEN://403，一般是没有加入房间或者直播间
                    log.debug("403 error");
                    Long liveId = authService.getLiveId();
                    if (liveId != null) {
                        authService.leaveLive(liveId, new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                log.debug("receive 403 leave live");
                            }
                        });

                        authService.joinLive(liveId, authService.getPassword(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                log.debug("receive 403 join live");
                            }
                        });

                    }
                    break;
                case BaseService.UNAUTHORIZED://401，一般是在其他设备上登录了
                    log.debug("401 error");
                    authService.reset();
                    log.debug("reset room & live");
//                    hdDataService.stop();
                    //TODO 发送广播
                    Intent broadCastIntent = new Intent(HDUserChangedBroadCastReceiver.INTENT_FILTER_ACTION);
                    HDBaseTabFragmentActivity.self.sendBroadcast(broadCastIntent);
                    log.debug("send broadcast to ui");
                    break;
                case BaseService.INVALID_USERID:
                    Long userId = authService.getUserId();
                    AuthRequest.setUserId(userId);
                    break;
                default:
                    break;
            }
        }
        message.setData(bundle);
        message.sendToTarget();
    }
}
