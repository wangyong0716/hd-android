package fm.dian.hddata_android.online;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import fm.dian.hdservice.OnlineService;

/**
 * Created by tinx on 4/8/15.
 */
public class OnlinePublish {

    public static final int ONLINE_PUBLISH = 1;
    private final Handler handler;

    public OnlinePublish(Handler handler) {
        this.handler = handler;
    }

    public void onlinePublish(long online_user_number) {
        Message message = Message.obtain(handler, OnlineService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", ONLINE_PUBLISH);
        bundle.putLong("online_user_number", online_user_number);
        message.setData(bundle);
        message.sendToTarget();
    }
}
