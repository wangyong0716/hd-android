package fm.dian.hddata_android.live;

/**
 * Created by tinx on 4/7/15.
 */
public interface LiveResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
