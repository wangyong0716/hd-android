package fm.dian.hdservice;

import android.util.Log;

import fm.dian.hddata_android.heartbeat.Heartbeat;

/**
 * Created by tinx on 4/7/15.
 */
public class HeartbeatService {

    private static HeartbeatService heartbeatService;

    private HeartbeatService() {
    }

    public static HeartbeatService getInstance() {
        if (heartbeatService == null)
            heartbeatService = new HeartbeatService();
        return heartbeatService;
    }

    public void setTimeIntervalSeconds(int time_interval_seconds) {
        Heartbeat.setTimeIntervalSeconds(time_interval_seconds);
    }

    public void start() {
        Heartbeat.start();
    }

    public void stop() {
        Heartbeat.stop();
    }

    public void fire() {
        Heartbeat.fire();
        Log.i("HeartbeatService", "fire a heartbeat");
    }
}
