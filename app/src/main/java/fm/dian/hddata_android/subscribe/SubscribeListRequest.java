package fm.dian.hddata_android.subscribe;

/**
 * Created by tinx on 4/7/15.
 */
public class SubscribeListRequest {

    public static native void getSubscribeRoomsByOffset(
            int offset,
            int count,
            SubscribeListResponse response,
            Object client_data,
            int timeoutMilliSeconds);
}
