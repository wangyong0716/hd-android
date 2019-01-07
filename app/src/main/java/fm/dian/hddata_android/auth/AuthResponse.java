package fm.dian.hddata_android.auth;

/**
 * Created by tinx on 3/31/15.
 */
public interface AuthResponse {
    void response(int error_code, Object client_data);
}