package fm.dian.hdservice.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tinx on 3/26/15.
 */
public class Publish {

    private Map<String, Object> map = new HashMap<String, Object>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

}
