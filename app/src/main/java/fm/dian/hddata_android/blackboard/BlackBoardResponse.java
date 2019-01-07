package fm.dian.hddata_android.blackboard;

/**
 * Created by tinx on 4/8/15.
 */
public interface BlackBoardResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
