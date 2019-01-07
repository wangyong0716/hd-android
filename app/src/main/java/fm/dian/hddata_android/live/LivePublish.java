package fm.dian.hddata_android.live;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.core.HDTableUser;
import fm.dian.service.live.HDLiveInfo;

/**
 * Created by tinx on 4/7/15.
 */
public class LivePublish {

    private static final Logger log = Logger.getLogger(LivePublish.class);
    private static LivePublish livePublish;
    private final Handler handler;
    private volatile Live live;

    public static final int LIVE_USER_PUBLISH = 1;
    public static final int LIVE_LIVE_PUBLISH = 2;

    private LivePublish(Handler handler) {
        this.handler = handler;
    }

    public static LivePublish getInstance(Handler handler) {
        if (livePublish == null) {
            livePublish = new LivePublish(handler);
        }
        return livePublish;
    }


    public void liveUserPublish(byte[] data) {
        try {
            HDTableUser.HDUser pbUser = HDTableUser.HDUser.parseFrom(data);
            if (pbUser != null) {
                Message message = Message.obtain(handler, LiveService.PUBLISH);
                User user = new User(pbUser);
                Bundle bundle = new Bundle();
                bundle.putInt("publish_type", LIVE_USER_PUBLISH);
                bundle.putSerializable("user", user);
                message.setData(bundle);
                message.sendToTarget();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void liveLivePublish(byte[] data, boolean closed) {
        try {
            HDLiveInfo.LiveInfo liveInfo = HDLiveInfo.LiveInfo.parseFrom(data);
            Message message = Message.obtain(handler, LiveService.PUBLISH);
            Bundle bundle = new Bundle();
            bundle.putInt("publish_type", LIVE_LIVE_PUBLISH);
//            bundle.putSerializable("live", new Live(liveInfo));
            this.live = new Live(liveInfo);
            message.setData(bundle);
            message.sendToTarget();
            log.debug("receive live publish.live={}", liveInfo);
        } catch (Exception e) {
            log.error("liveLivePublish error", e);
        }
    }

    public Live getLive() {
        return live;
    }

    public void clearLive() {
        this.live = null;
    }

    public void setLive(Live live) {
        this.live = live;
    }
}
