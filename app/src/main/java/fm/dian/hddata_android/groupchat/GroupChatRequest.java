package fm.dian.hddata_android.groupchat;

/**
 * Created by tinx on 4/7/15.
 */
public class GroupChatRequest {

    public static native long getLastMessageId();

    public static native void reset_message_last_id();

    public static native void getChatMessage(
            long live_id,
            long last_group_chat_id,
            boolean is_older,
            int count,
            GroupChatResponse response,
            Object client_data,
            int timeout_milliseconds);

    public static native Object sendChatMessage(long client_id,
                                                long live_id,
                                                long user_id,
                                                String content,
                                                GroupChatResponse response,
                                                Object client_data,
                                                int timeout_milliseconds);

    public static native void getKeywords(long live_id,
                                          GroupChatResponse response,
                                          Object client_data,
                                          int timeout_milliseconds);

    public static native void addKeyword(long live_id,
                                         String keyword,
                                         GroupChatResponse response,
                                         Object client_data,
                                         int timeout_milliseconds);

    public static native void removeKeywords(long id,
                                             GroupChatResponse response,
                                             Object client_data,
                                             int timeout_milliseconds);

    public static native void setPublishHandler(GroupChatPublish publishHandler);
}
