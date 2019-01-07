package fm.dian.hdservice.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by tinx on 3/23/15.
 */
public abstract class BaseService {

    public static final String LOG_TAG = "HDService";

    public final int TIMEOUT_MILLISECONDS = 10000;

    public static final int OK = 0;

    public static final int RESPONSE = 1;

    public static final int PUBLISH = 2;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;


    public static final int TIMEOUT = 1000;
    public static final int RESPONSE_NULL = 1001;
    public static final int PROTORESPONSE_PARSEFAIL = 1002;
    public static final int RESPONSE_ROOMID_INVALID = 1003;  // If room id is not equal to current room id; result should be discarded.
    public static final int RESPONSE_USERID_INVALID = 1004;  // If user id is not equal to current user id; result should be discarded.
    public static final int INVALIDPARAM = 1005;
    public static final int NOT_HIT_CACHE = 1006;
    public static final int CACHEDATA_EXPIRED = 1007;
    public static final int INVALID_USERID = 1008;
    public static final int INVALID_PLATFORMTYPE = 1009;



    protected final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESPONSE:
                    CallBack callBack = (CallBack) msg.obj;
                    Bundle data = msg.getData();
                    callBack.process(data);
                    break;
                case PUBLISH:
                    Bundle publishData = msg.getData();
                    observable.notifyChanged(publishData);
                    break;
                default:
                    Log.e("BaseService", "bad message");
            }
        }
    };

    protected int serviceType;

    protected BaseService(int serviceType) {
        this.serviceType = serviceType;
    }

    private final ServiceObservable observable = new ServiceObservable();


    private class ServiceObservable extends Observable {
        public void notifyChanged(Bundle publishData) {
            this.setChanged();
            this.notifyObservers(publishData);
        }
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        observable.deleteObserver(observer);
    }

}
