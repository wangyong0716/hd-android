package fm.dian.hddata_android.history;

/**
 * Created by tinx on 4/14/15.
 */
public interface HistoryCountResponse {

    void response(int error_code, int published, int unPublished, Object client_data);

}
