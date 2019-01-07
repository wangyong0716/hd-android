package fm.dian.hddata_android.online;

/**
 * Created by tinx on 4/8/15.
 */
public interface OnlineResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
