package fm.dian.hddata_android.online;

/**
 * Created by tinx on 4/8/15.
 */
public class OnlineRequest {

    public static native void resetVersionNumber();

    public static native void getOnlineUserListByOffset(int offset,
                                                        int limit,
                                                        OnlineResponse response,
                                                        Object client_data,
                                                        int timeout_milliseconds);

    public static native void getOnlineUserNumberByLiveId(long live_id,
                                                          OnlineResponse response,
                                                          Object client_data,
                                                          int timeout_milliseconds);

    public static native void setPublishHandler(OnlinePublish publishHandler);
}
