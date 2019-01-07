package fm.dian.hddata_android.blackboard;

/**
 * Created by tinx on 4/8/15.
 */
public class BlackBoardRequest {

    public static native void resetVersionNumber();

    public static native void sendCards(Object[] cards,
                                        long card_id,
                                        BlackBoardResponse response,
                                        Object client_data,
                                        int timeout_milliseconds);

    public static native void fetchCards(long[] card_ids,
                                         BlackBoardResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void deleteCards(long[] card_ids,
                                          BlackBoardResponse response,
                                          Object client_data,
                                          int timeout_milliseconds);

    public static native void changeToCard(long card_id,
                                           BlackBoardResponse response,
                                           Object client_data,
                                           int timeout_milliseconds);

    public static native void closeBlackboard(BlackBoardResponse response,
                                              Object client_data,
                                              int timeout_milliseconds);

    public static native void setPublishHandler(BlackBoardPublish publishHandler);
}
