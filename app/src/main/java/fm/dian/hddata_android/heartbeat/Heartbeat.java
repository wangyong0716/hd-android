package fm.dian.hddata_android.heartbeat;

/**
 * Created by tinx on 4/7/15.
 */
public class Heartbeat {
    public static native int getTimeIntervalSeconds();

    public static native void setTimeIntervalSeconds(int time_interval_seconds);

    public static native void start();

    public static native void stop();

    public static native void fire();
}
