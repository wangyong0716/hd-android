package fm.dian.hddata_android.groupchat;

/**
 * Created by tinx on 4/7/15.
 */
public interface GroupChatResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
