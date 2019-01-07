package fm.dian.hddata_android.live;

/**
 * Created by tinx on 4/7/15.
 */
public class LiveRequest {
    public static native long getLiveVersionNumber();

    public static native void resetLiveVersionNumber();

    public static native long getUserVersionNumber();

    public static native void resetUserVersionNumber();

    public static native void startLive(byte[] live,
                                        LiveResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void updateLive(byte[] live,
                                         LiveResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void stopLive(long live_id,
                                       LiveResponse response,
                                       Object client_data,
                                       int timeout_milliseconds);

    public static native void closeLive(long live_id,
                                        LiveResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void kickLive(long user_id,
                                       LiveResponse response,
                                       Object client_data,
                                       int timeout_milliseconds);

    public static native void fetchLiveInfo(long[] liveIds,
                                            LiveResponse response,
                                            Object client_data,
                                            int timeout_milliseconds);

    public static native void fetchLiveList(long room_id,
                                            int offset,
                                            int limit,
                                            LiveResponse response,
                                            Object client_data,
                                            int timeout_milliseconds);

    public static native void setPublishHandler(LivePublish publish);
}
