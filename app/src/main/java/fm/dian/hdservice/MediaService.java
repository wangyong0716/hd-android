package fm.dian.hdservice;

import android.os.Bundle;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hddata_android.media.MediaPublish;
import fm.dian.hddata_android.media.MediaRequst;
import fm.dian.hddata_android.media.MediaResponse;
import fm.dian.hdservice.api.MediaServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.rpc.HDServiceType.ServiceType;
import hd.hdmedia.HDMediaModule;

/**
 * Created by tinx on 3/25/15.
 */
public class MediaService extends BaseService implements MediaServiceInterface {


    private static final Logger log = Logger.getLogger(MediaService.class, LOG_TAG);
    public static final int FULL = 1200;
    public static final int UNAUTHORIZED = 1201;
    public static final int ALREADY_SPEAKING = 1202;
    public static final int ALREADY_INVITED = 1203;
    public static final int USER_INVALID = 1204;
    public static final int AUDIO_LIVING = 1205;
    public static final int VIDEO_LIVING = 1206;


    private static MediaService mediaService;
    private final MediaPublish publish;
    private boolean isAudioOpen = false;
    private boolean isVideoOpen = false;
    private boolean isAudioRecordOpen = false;
    private boolean isVideoRecordOpen = false;

    private HDMediaModule hdMediaModule = HDMediaModule.getInstance();


    private MediaService(int serviceType) {
        super(serviceType);
        publish = MediaPublish.getInstance(handler);
        MediaRequst.setPublishHandler(publish);
        MediaRequst.resetLiveVersionNumber();
        MediaRequst.resetUserVersionNumber();
    }

    public static MediaService getInstance() {
        if (mediaService == null) {
            mediaService = new MediaService(ServiceType.MEDIA_VALUE);
        }
        return mediaService;
    }

    @Override
    public void startAudioRecord(final CallBack callBack) {
        MediaRequst.speakStart(new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    Long userId = AuthService.getInstance().getUserId();
                    Long liveId = AuthService.getInstance().getLiveId();
                    startRecord(liveId, userId);
                    isAudioRecordOpen = true;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void stopAudioRecord(final CallBack callBack) {
        MediaRequst.speakStop(new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    stopRecord();
                    isAudioRecordOpen = false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void startVideoRecord(CallBack callBack) {
        MediaRequst.videoStart(new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    Long userId = AuthService.getInstance().getUserId();
                    Long liveId = AuthService.getInstance().getLiveId();
                    hdMediaModule.startVideoRecord(String.valueOf(liveId), String.valueOf(userId));
                    isVideoRecordOpen = true;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void stopVideoRecord(CallBack callBack) {
        MediaRequst.videoStop(new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                int error_code = bundle.getInt("error_code");
                if (error_code == OK) {
                    hdMediaModule.stopVideoRecord();
                    isVideoRecordOpen = false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
        ;
    }

    @Override
    public void startAudioPlay(List<Long> speakers) {
        Long userId = AuthService.getInstance().getUserId();
        Long liveId = AuthService.getInstance().getLiveId();
        ArrayList<String> speakerList = new ArrayList<>();
        for (Long speaker : speakers) {
            speakerList.add(String.valueOf(speaker));
        }
        hdMediaModule.startAudioPlay(String.valueOf(liveId), speakerList, String.valueOf(userId));
        isAudioOpen = true;
    }

    @Override
    public void stopAudioPlay() {
        hdMediaModule.stopAudioPlay();
        isAudioOpen = false;
    }

    @Override
    public void startVideoPlay(Long userId) {
        Long myId = AuthService.getInstance().getUserId();
        Long liveId = AuthService.getInstance().getLiveId();
        hdMediaModule.startVideoPlay(String.valueOf(liveId), String.valueOf(userId), String.valueOf(myId));
        isVideoOpen = true;
    }

    @Override
    public void stopVideoPlay() {

        hdMediaModule.stopVideoPlay();
        isVideoOpen = false;
    }

    private void startRecord(Long liveId, Long userId) {
        String roomId = String.valueOf(liveId);
        String myid = String.valueOf(userId);
        log.debug("RecordTest:start;liveId:{},userId:{}", roomId, myid);
        hdMediaModule.startAudioRecord(roomId, myid);
    }

    private void stopRecord() {
        log.debug("RecordTest:stop");
        hdMediaModule.stopAudioRecord();
    }

    @Override
    public boolean isAudioOpen() {
        return isAudioOpen;
    }

    @Override
    public boolean isAudioRecordOpen() {
        return isAudioRecordOpen;
    }

    @Override
    public boolean isVideoOpen() {
        return isVideoOpen;
    }

    @Override
    public boolean isVideoRecordOpen() {
        return isVideoRecordOpen;
    }

    public void stopPictureOfVideo() {
        MediaRequst.setBlockIncomingVideo(true);
    }

    public void resumePictureOfVideo() {
        MediaRequst.setBlockIncomingVideo(false);
    }

    public void kickSpeaker(Long userId, CallBack callBack) {
        MediaRequst.kickSpeak(userId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void inviteSpeak(long user_id, final CallBack callBack) {
        MediaRequst.inviteSpeak(user_id, new MediaResponse() {
            @Override
            public void response(int error_code, byte[] bytes, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    public List<Long> getCurrentSpeakers() {
        return publish.getCurrentSpeakers();
    }

    public Long getVideoSpeaker() {
        return publish.getVideoSpeaker();
    }

    public void clear() {
        if (isAudioOpen) {
            stopAudioPlay();
        }
        if (isVideoOpen) {
            stopVideoPlay();
        }
        if (isAudioRecordOpen) {
            stopAudioRecord(new CallBack() {
                @Override
                public void process(Bundle data) {
                    //
                }
            });
        }
        if (isVideoRecordOpen) {
            stopVideoRecord(new CallBack() {
                @Override
                public void process(Bundle data) {
                    //
                }
            });
        }
        publish.clearCurrentSpeaker();
        publish.clearVideoSpeaker();
        MediaRequst.resetLiveVersionNumber();
        MediaRequst.resetUserVersionNumber();
        MediaRequst.resetLastSpeakers();
        MediaRequst.resetAudioLiving();
        MediaRequst.resetVideoLivingUserId();
        MediaRequst.resetLastVideoers();
    }
}
