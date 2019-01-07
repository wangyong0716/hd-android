package fm.dian.hddata_android.history;

/**
 * Created by tinx on 4/14/15.
 */
public interface HistoryListResponse {

    void response(int error_code, Object[] published, Object[] unPublished, Object client_data);
}
