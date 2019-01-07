package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface GroupChatServiceInterface {

    /**
     * 发送文本消息
     *
     * @param txt
     */
    public void sendText(long liveId, long userId, String txt, CallBack callBack);

    public void fetchChatList(long liveId, long lastGroupChatId, boolean isOlder, int count, CallBack callBack);

    public void getKeywords(long live_id, CallBack callBack);

    public void addKeyword(long live_id, String keyword, CallBack callBack);

    public void removeKeywords(long id, CallBack callBack);


}
