package fm.dian.hddata_android.auth;

/**
 * Created by tinx on 3/31/15.
 */
public class AuthRequest {
    public static native void setUserId(long userId);

    public static native void setUserToken(String token);

    public static native void setDeviceIdentifier(String deviceId);

    public static native void joinRoom(long roomId, String password, AuthResponse response, Object clientData, int timeoutMilliSeconds);

    public static native void leaveRoom(long roomId, AuthResponse response, Object clientData, int timeoutMilliSeconds);

    public static native void joinLive(long liveId, String password, AuthResponse response, Object clientData, int timeoutMilliSeconds);

    public static native void leaveLive(long liveId, AuthResponse response, Object clientData, int timeoutMilliSeconds);


    public static native long getUserId();

    public static native String getDeviceIdentifier();

    public static native String getUserToken();

    public static native void resetUserToken();

    public static native long[] getRoomIds();

    public static native void insertRoomId(long roomId);

    public static native void removeRoomId(long roomId);

    public static native long lastRoomId();

    public static native boolean hasRoomId(long room_id);

    public static native long getLiveId();

    public static native void resetLiveId();

    public static native void setLiveId(long liveId);

}
