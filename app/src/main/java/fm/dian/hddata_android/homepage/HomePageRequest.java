package fm.dian.hddata_android.homepage;

/**
 * Created by tinx on 4/7/15.
 */
public class HomePageRequest {

    public static native void getIndexRoomsWithOffset(
            int offset,
            int limit,
            HomePageResponse response,
            Object client_data,
            int timeoutMilliSeconds);
}
