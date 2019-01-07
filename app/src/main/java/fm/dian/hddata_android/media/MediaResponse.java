package fm.dian.hddata_android.media;

/**
 * Created by tinx on 4/8/15.
 */
public interface MediaResponse {
    void response(int error_code, byte[] bytes, Object client_data);
}
