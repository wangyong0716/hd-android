package fm.dian.hddata_android.core;

/**
 * Created by tinx on 4/1/15.
 */
public class CoreRequest {


    public static native Object getRoomByWebAddress(int cacheTimeoutSeconds,
                                                    String webaddr,
                                                    CoreResponse response,
                                                    Object client_data,
                                                    int timeout_milliseconds);

    public static native Object getRoomByRoomId(int cacheTimeoutSeconds,
                                                long room_id,
                                                CoreResponse response,
                                                Object client_data,
                                                int timeout_milliseconds);

    public static native Object getUserByUserId(int cacheTimeoutSeconds,
                                                long target_user_id,
                                                CoreResponse response,
                                                Object client_data,
                                                int timeout_milliseconds);

    public static native Object getRoomUserAdminByRoomId(int cacheTimeoutSeconds,
                                                         long room_id,
                                                         CoreResponse response,
                                                         Object client_data,
                                                         int timeout_milliseconds);

    public static native void getRoomUserAdminByUserId(long target_user_id,
                                                       CoreResponse response,
                                                       Object client_data);

    public static native Object getRoomUserIgnoreByRoomId(int cacheTimeoutSeconds,
                                                          long room_id,
                                                          CoreResponse response,
                                                          Object client_data,
                                                          int timeout_milliseconds);

    public static native Object getRoomUserFollowByRoomId(int cacheTimeoutSeconds,
                                                          long room_id,
                                                          CoreResponse response,
                                                          Object client_data,
                                                          int timeout_milliseconds);

    public static native void getRoomUserFollowByUserId(long target_user_id,
                                                        CoreResponse response,
                                                        Object client_data);

    public static native void FetchRoomMemberCountByRoomID(long room_id,
                                                           CoreResponse response,
                                                           Object client_data);

    public static native void insertRoom(byte[] room,
                                         CoreResponse response,
                                         Object client_data);

    public static native void updateRoom(byte[] room,
                                         CoreResponse response,
                                         Object client_data);

    public static native void updateUser(byte[] user,
                                         CoreResponse response,
                                         Object client_data);

    public static native void insertRoomUserAdmin(byte[] roomUserAdmin,
                                                  CoreResponse response,
                                                  Object client_data);

    public static native void insertRoomUserIgnore(byte[] roomUserIgnore,
                                                   CoreResponse response,
                                                   Object client_data);

    public static native void insertRoomUserFollow(byte[] roomUserFollow,
                                                   CoreResponse response,
                                                   Object client_data);

    public static native void deleteRoomUserAdmin(byte[] roomUserAdmin,
                                                  CoreResponse response,
                                                  Object client_data);

    public static native void deleteRoomUserIgnore(byte[] roomUserIgnore,
                                                   CoreResponse response,
                                                   Object client_data);

    public static native void deleteRoomUserFollow(byte[] roomUserFollow,
                                                   CoreResponse response,
                                                   Object client_data);

    public static native void setPublishHandler(CorePublish publish);

    public static native void resetVersionNumber(long room_id);

    public static native void clearAllVersionNumber();

}
