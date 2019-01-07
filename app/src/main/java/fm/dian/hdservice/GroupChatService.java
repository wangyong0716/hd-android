package fm.dian.hdservice;

import android.os.Bundle;

import com.google.protobuf.ExtensionRegistry;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import fm.dian.hddata_android.groupchat.GroupChatPublish;
import fm.dian.hddata_android.groupchat.GroupChatRequest;
import fm.dian.hdservice.api.GroupChatServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.GroupChat;
import fm.dian.hdservice.model.KeyWord;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.group_chat.HDGroupChatKeyword.GroupChatKeyword;
import fm.dian.service.group_chat.HDGroupChatMessage;
import fm.dian.service.group_chat.HDGroupChatMessage.GroupChatMessage;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class GroupChatService extends BaseService implements GroupChatServiceInterface {

    private static final Logger log = Logger.getLogger(GroupChatService.class, LOG_TAG);
    public static final int MESSAGE_SENDING = 1400;
    public static final int MESSAGE_SIZE_LIMIT = 1401;
    public static final int MESSAGE_CONTAIN_KEYWORD = 1402;
    public static final int KEYWORD_ALREADY_EXISTS = 1403;
    public static final int CHAT_SILENCE = 1404;

    private static GroupChatService groupChatService;
    private final GroupChatPublish publish;

    private final ConcurrentSkipListSet<GroupChat> chatSet;

    private final ExtensionRegistry registry = ExtensionRegistry.newInstance();

    private GroupChatService(int serviceType) {
        super(serviceType);
        HDGroupChatMessage.registerAllExtensions(registry);
        publish = GroupChatPublish.getInstance(handler);
        GroupChatRequest.setPublishHandler(publish);
        GroupChatRequest.reset_message_last_id();
        chatSet = new ConcurrentSkipListSet<>();
    }

    public static GroupChatService getInstance() {
        if (groupChatService == null) {
            groupChatService = new GroupChatService(ServiceType.GROUP_CHAT_VALUE);
        }
        return groupChatService;
    }

    @Override
    public void sendText(long liveId, long userid, String txt, final CallBack callBack) {

        GroupChatRequest.sendChatMessage(-1L, liveId, userid, txt, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    GroupChatMessage groupChatMessage = GroupChatMessage.parseFrom((byte[]) data[0], registry);
                    if (groupChatMessage.hasGroupChatId()) {
//                        chatSet.add(new GroupChat(groupChatMessage));
                    }
                    //                if (groupChatMessage.hasExtension(GroupChatClientMessage.groupChatClientMessage)) {
////                    GroupChatClientMessage groupChatClientMessage = groupChatMessage.getExtension(GroupChatClientMessage.groupChatClientMessage);
////                    Long clientId = groupChatClientMessage.getClientId();
////                    GroupChatMessageClientStatus status = groupChatClientMessage.getStatus();
////                    bundle.putString("send_status", status.name());
//                }
                } catch (Exception e) {
                    log.error("sendText error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchChatList(long liveId, final long lastGroupChatId,
                              boolean isOlder, int count, final CallBack callBack) {
        log.debug("fetch chat list: liveId={},lastChatId={},isOlder={},count={}", liveId, lastGroupChatId, isOlder, count);
        GroupChatRequest.getChatMessage(liveId, lastGroupChatId, isOlder, count, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    if (data.length > 0) {
                        for (Object obj : data) {
                            GroupChatMessage groupChatMessage = GroupChatMessage.parseFrom((byte[]) obj, registry);
                            if (null != groupChatMessage) {
                                log.debug("receive chat message:{}", groupChatMessage.toString());
//                                chatSet.add(new GroupChat(groupChatMessage));
                                addChat(new GroupChat(groupChatMessage));
                            }
                        }
                        GroupChatMessage lastChatMessage = GroupChatMessage.parseFrom((byte[]) data[data.length - 1], registry);
                        bundle.putInt("count", data.length);
                        publish.setLastMessageId(lastChatMessage.getGroupChatId());
                    }
                } catch (Exception e) {
                    log.error("fetchChatList error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void getKeywords(long live_id, final CallBack callBack) {

        GroupChatRequest.getKeywords(live_id, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    List<KeyWord> keyWordList = new ArrayList<KeyWord>(data.length);
                    for (Object obj : data) {
                        GroupChatKeyword groupChatKeyWord = GroupChatKeyword.parseFrom((byte[]) obj);
                        if (groupChatKeyWord != null) {
                            keyWordList.add(new KeyWord(groupChatKeyWord));
                        }
                    }
                    bundle.putSerializable("keyword_list", (java.io.Serializable) keyWordList);
                } catch (Exception e) {
                    log.error("getKeywords error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void addKeyword(long live_id, String keyword, final CallBack callBack) {

        GroupChatRequest.addKeyword(live_id, keyword, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void removeKeywords(long id, final CallBack callBack) {
        GroupChatRequest.removeKeywords(id, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    private void addChat(GroupChat chat) {
        synchronized (chatSet){
            chatSet.add(chat);
            if (chatSet.size() >= 60) {
                chatSet.pollFirst();
            }
        }
    }

    public void clear() {
        synchronized (chatSet){
            chatSet.clear();
        }
        publish.clearLastMessageId();
        GroupChatRequest.reset_message_last_id();
    }

    public Set<GroupChat> getChatSet() {
        try {
            synchronized (chatSet){
                return new LinkedHashSet<>(chatSet);
            }
        } catch (Exception e) {
            log.error("get chatset error", e);
            return new LinkedHashSet<>();
        }
    }

    public void loadHistory(Long liveId, CallBack callBack) {
        if (chatSet.isEmpty()) {
            fetchChatList(liveId, 0, false, 20, callBack);
        } else {
            callBack.process(null);
        }
    }
}
