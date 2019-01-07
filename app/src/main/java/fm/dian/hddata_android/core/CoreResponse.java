package fm.dian.hddata_android.core;

/**
 * Created by tinx on 4/1/15.
 */
public interface CoreResponse {
    void response(int error_code, Object[] bytes, Object client_data);
}
