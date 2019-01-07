package fm.dian.hddata_android.history;

/**
 * Created by tinx on 4/8/15.
 */
public class HistoryRequest {
    public static native void resetVersionNumber();

    public static native void getCountByRoomId(long room_id,
                                               HistoryCountResponse response,
                                               Object client_data,
                                               int timeout_milliseconds);

    public static native void getListByRoomId(long room_id,
                                              int fetch_type,
                                              int offset,
                                              int limit,
                                              HistoryListResponse response,
                                              Object client_data,
                                              int timeout_milliseconds);

    public static native void getRecordTime(HistoryResponse response,
                                            Object client_data,
                                            int timeout_milliseconds);

    public static native void startRecord(HistoryResponse response,
                                          Object client_data,
                                          int timeout_milliseconds);

    public static native void stopRecord(HistoryResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void updateRecordByHistoryId(long history_id,
                                                      String name,
                                                      boolean is_publish,
                                                      boolean is_delete,
                                                      HistoryResponse response,
                                                      Object client_data,
                                                      int timeout_milliseconds);

    public static native void setPublishHandler(HistoryPublish publishHandler);
}
