package fm.dian.hdui.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 类说明：    网络监测类
 *
 * @author @wxm
 * @version 2.2
 */
public class NetworkUtils {
    public final static int NONE = 0;    // 无网络
    public final static int WIFI = 1;    // Wi-Fi
    public final static int MOBILE = 2; // 3G,GPRS

    /**
     * 获取当前网络状态
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 手机网络判断
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        State state = null;
        if (netInfo != null) {
            state = netInfo.getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return MOBILE;
            }
        }
        netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null) {
            state = netInfo.getState();
            if (state == State.CONNECTED || state == State.CONNECTING) {
                return WIFI;
            }
        }
        return NONE;
    }


}