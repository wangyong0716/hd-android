package fm.dian.hdservice.base;

import java.util.HashMap;
import java.util.Map;

import fm.dian.service.rpc.HDResponse.Response.ResponseStatus;

/**
 * Created by tinx on 3/26/15.
 */
public class Response {
    private ResponseStatus status = ResponseStatus.OK;
    private Map<String, Object> map = new HashMap<String, Object>();

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

}
