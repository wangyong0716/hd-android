package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface AuthServiceInterface {

    /**
     * 加入频道
     *
     * @param roomId
     * @param passwd
     * @param callBack
     */
    public void joinRoom(long roomId, String passwd, CallBack callBack);

    /**
     * 离开频道
     *
     * @param roomId
     * @param callBack
     */
    public void leaveRoom(long roomId, CallBack callBack);

    /**
     * 加入直播间
     *
     * @param liveId
     * @param passwd
     * @param callBack
     */
    public void joinLive(long liveId, String passwd, CallBack callBack);

    /**
     * 离开直播间
     *
     * @param liveId
     * @param callBack
     */
    public void leaveLive(long liveId, CallBack callBack);

    /**
     * 登录后设置登录用户的id
     *
     * @param userId
     */
    public void setUserId(long userId);

    /**
     * 设置用户的Token
     *
     * @param token
     */
    public void setToken(String token);

    /**
     * 获取当前用户的id
     *
     * @return
     */
    public long getUserId();


}
