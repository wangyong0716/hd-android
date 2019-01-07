package fm.dian.hddata_android.media;

/**
 * Created by tinx on 4/8/15.
 */
public class MediaRequst {

    public static native void resetLiveVersionNumber();

    public static native void resetUserVersionNumber();

    public static native boolean isAudioLiving();

    public static native void setIsAudioLiving(boolean is_living);

    public static native void speakStart(MediaResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void speakStop(MediaResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void videoStart(MediaResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void videoStop(MediaResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);


    public static native void inviteSpeak(long user_id,
                                          MediaResponse response,
                                          Object client_data,
                                          int timeout_milliseconds);

    public static native void cancelInvite(long user_id,
                                           MediaResponse response,
                                           Object client_data,
                                           int timeout_milliseconds);

    public static native void acceptInvite(long user_id,
                                           MediaResponse response,
                                           Object client_data,
                                           int timeout_milliseconds);

    public static native void rejectInvite(long user_id,
                                           MediaResponse response,
                                           Object client_data,
                                           int timeout_milliseconds);

    public static native void kickSpeak(long user_id,
                                        MediaResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void getOnlineListByRoomId(long room_id,
                                                    MediaResponse response,
                                                    Object client_data,
                                                    int timeout_milliseconds);

    public static native void VideoStart(MediaResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void VideoStop(MediaResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void setPublishHandler(MediaPublish publishHandler);


    public static native void resetAudioLiving();

    public static native long videoLivingUserId();

    public static native void resetVideoLivingUserId();

    public static native boolean isUserSpeaking(long userId);

    public static native boolean blockIncomingVideo();

    public static native void setBlockIncomingVideo(boolean blocked);

    public static native void resetLastVideoers();

    public static native void resetLastSpeakers();
}
