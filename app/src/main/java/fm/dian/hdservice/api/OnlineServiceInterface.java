package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface OnlineServiceInterface {

    /**
     * 获取频道的在线用户人数
     *
     * @param roomId
     * @param callBack
     */
    public void fetchOnlineUserNumber(long roomId, CallBack callBack);

    /**
     * 获取频道的在线用户成员列表
     */
    public void fetchOnlineUserList(int offset, int limit, CallBack callBack);

}
