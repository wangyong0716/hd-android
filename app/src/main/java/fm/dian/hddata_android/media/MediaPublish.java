package fm.dian.hddata_android.media;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.service.media.HDMediaUserVersion;

/**
 * Created by tinx on 4/8/15.
 */
public class MediaPublish {

    private static MediaPublish mediaPublish;

    public static final Logger log = Logger.getLogger(MediaPublish.class);
    public static final int SPEAK_PUBLISH = 1;
    public static final int VIDEO_PUBLISH = 2;
    public static final int USER_PUBLISH = 3;
    private final Handler handler;

    private final List<Long> currentSpeakers;

    private volatile long videoUser;


    private MediaPublish(Handler handler) {
        this.handler = handler;
        currentSpeakers = new ArrayList<>();
    }

    public static MediaPublish getInstance(Handler handler) {
        if (mediaPublish == null) {
            mediaPublish = new MediaPublish(handler);
        }
        return mediaPublish;
    }

    public void speakPublish(long[] current_speakers, long[] old_speakers) {
        Message message = Message.obtain(handler, MediaService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", SPEAK_PUBLISH);
//        bundle.putLongArray("current_speakers", current_speakers);
//        bundle.putLongArray("old_speakers", old_speakers);
        resetCurrentSpeaker(current_speakers);
        message.setData(bundle);
        message.sendToTarget();
        log.debug(HongdianConstants.DEBUG_LIEYUNYE, "receive speak publish");
    }

    public void videoPublish(long[] video_user_id_list) {
        Message message = Message.obtain(handler, MediaService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", VIDEO_PUBLISH);
        if (video_user_id_list != null && video_user_id_list.length > 0) {
            this.videoUser = video_user_id_list[0];
        } else {
            this.videoUser = 0;
        }
//        bundle.putLongArray("video_user_id_list", video_user_id_list);
        message.setData(bundle);
        message.sendToTarget();
        log.debug("receive video publish");
    }

    public void userPublish(byte[] media_user_version) {
        Message message = Message.obtain(handler, MediaService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", USER_PUBLISH);
        bundle.putByteArray("media_user_version", media_user_version);
        try {
            HDMediaUserVersion.MediaUserVersion mediaUserVersion = HDMediaUserVersion.MediaUserVersion.parseFrom(media_user_version);
            Log.d(HongdianConstants.DEBUG_LIEYUNYE, "media_user_version" + mediaUserVersion.getVersionType());
            if (mediaUserVersion.getVersionType().equals(HDMediaUserVersion.MediaUserVersion.VersionType.KICKED)) {

            }
            bundle.putInt("Versig fm.dian.hdservice.model.User.getUserId()' on a null objeconType", mediaUserVersion.getVersionType().getNumber());

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        message.setData(bundle);
        message.sendToTarget();
    }

    private void resetCurrentSpeaker(long[] current_speakers) {
//        synchronized (currentSpeakers) {
//            Long userId = AuthService.getInstance().getUserId();
//            MediaService mediaService = MediaService.getInstance();
//            Long liveId = AuthService.getInstance().getLiveId();
//            currentSpeakers.remove(userId);
//            if (currentSpeakers.isEmpty()) {
//                //之前麦上无人，收到publish启动播放器
//                mediaService.startPlayAudio(liveId);
//            }
//            currentSpeakers.clear();
//            if (current_speakers != null && current_speakers.length > 0) {
//                for (long id : current_speakers) {
//                    currentSpeakers.add(id);
//                }
//            }
//            Set<Long> speakerSet = new HashSet<>(currentSpeakers);
//            speakerSet.remove(userId);
//            if (speakerSet.isEmpty()) {
//                //现在麦上无人，关闭播放器
//                mediaService.stopPlayAudio();
//            } else {
//                mediaService.startAudioPlay(speakerSet);
//            }
//        }

        synchronized (currentSpeakers) {
            currentSpeakers.clear();
            if (current_speakers != null && current_speakers.length > 0) {
                for (long id : current_speakers) {
                    currentSpeakers.add(id);
                }
            }
            if (!currentSpeakers.isEmpty()) {
                MediaService.getInstance().startAudioPlay(currentSpeakers);
            } else {
                MediaService.getInstance().stopAudioPlay();
            }
        }
    }

    public List<Long> getCurrentSpeakers() {
        synchronized (currentSpeakers) {
            return new ArrayList<>(this.currentSpeakers);
        }
    }

    public void clearCurrentSpeaker() {
        synchronized (currentSpeakers) {
            currentSpeakers.clear();
        }
    }

    public Long getVideoSpeaker() {
        if (videoUser != 0) {
            return videoUser;
        } else {
            return null;
        }
    }

    public void clearVideoSpeaker() {
        this.videoUser = 0;
    }
}
