package fm.dian.hddata_android.history;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import fm.dian.hdservice.HistoryService;

/**
 * Created by tinx on 4/8/15.
 */
public class HistoryPublish {

    private static HistoryPublish historyPublish;

    public static final int RECORD_PUBLISH = 1;
    private final Handler handler;

    private volatile boolean isRecording;

    private HistoryPublish(Handler handler) {
        this.handler = handler;
    }

    public static HistoryPublish getInstance(Handler handler) {
        if (historyPublish == null) {
            historyPublish = new HistoryPublish(handler);
        }
        return historyPublish;
    }

    public void recordPublish(boolean isRecording) {
        Message message = Message.obtain(handler, HistoryService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", RECORD_PUBLISH);
//        bundle.putBoolean("isRecording", isRecording);
        this.isRecording = isRecording;
        message.setData(bundle);
        message.sendToTarget();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void clearIsRecording() {
        this.isRecording = false;
    }

}
