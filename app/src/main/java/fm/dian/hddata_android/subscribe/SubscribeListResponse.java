package fm.dian.hddata_android.subscribe;

/**
 * Created by tinx on 4/7/15.
 */
public interface SubscribeListResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
