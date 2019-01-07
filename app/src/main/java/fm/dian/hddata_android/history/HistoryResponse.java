package fm.dian.hddata_android.history;

/**
 * Created by tinx on 4/8/15.
 */
public interface HistoryResponse {
    void response(int error_code, long result, Object client_data);
}
