package fm.dian.hddata_android.groupchat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.GroupChatService;
import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 4/7/15.
 */
public class GroupChatPublish {

    private static GroupChatPublish groupChatPublish;

    public static final int GROUPCHAT_MESSAGE_PUBLISH = 1;

    private final Handler handler;

    private volatile long lastMessageId;

    private GroupChatPublish(Handler handler) {
        this.handler = handler;
    }

    public static GroupChatPublish getInstance(Handler handler) {
        if (groupChatPublish == null) {
            groupChatPublish = new GroupChatPublish(handler);
        }
        return groupChatPublish;
    }

    public void groupChatMessagePublish(long publish_serialize_number) {
        Message message = Message.obtain(handler, CoreService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", GROUPCHAT_MESSAGE_PUBLISH);
        bundle.putLong("last_message_id", publish_serialize_number);
        message.setData(bundle);
        fetchMessage(publish_serialize_number, message);
//        message.sendToTarget();
        Log.i("GroupChatPublish", "last_message_id=" + publish_serialize_number);
    }

    private synchronized void fetchMessage(final long last_message_id, final Message message) {
        if (lastMessageId < last_message_id) {
            GroupChatService chatService = GroupChatService.getInstance();
            Long liveId = AuthService.getInstance().getLiveId();
            chatService.fetchChatList(liveId, lastMessageId, false, 20, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int count = data.getInt("count");
                    if (count < 20) {
                        message.sendToTarget();
                    } else {
                        fetchMessage(last_message_id, message);
                    }
                }
            });
        }
    }

    public void clearLastMessageId() {
        this.lastMessageId = 0;
    }

    public void setLastMessageId(long last) {
        this.lastMessageId = last;
    }

}
