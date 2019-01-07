package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;

/**
 * Created by tinx on 3/25/15.
 */
public interface LiveServiceInterface {
    /**
     * 获取频道中的直播间列表
     *
     * @param roomId
     * @param offset
     * @param limit
     * @param callBack
     */
    public void fetchLiveList(long roomId, int offset, int limit, CallBack callBack);

    /**
     * 获取直播间信息
     *
     * @param liveIds
     * @param callBack
     */
    public void fetchLiveInfo(long[] liveIds, CallBack callBack);

    /**
     * 注销直播间
     *
     * @param liveId
     * @param callBack
     */
    public void closeLive(long liveId, CallBack callBack);

    /**
     * 踢人
     *
     * @param userId
     * @param callBack
     */
    public void kickUser(long userId, CallBack callBack);

    /**
     * 开启直播间
     *
     * @param liveId
     * @param callBack
     */
    public void startLive(Live live, CallBack callBack);

    /**
     * 关闭直播间
     *
     * @param liveId
     * @param callBack
     */
    public void stopLive(long liveId, CallBack callBack);

    /**
     * 关闭直播间
     *
     * @param live
     * @param callBack
     */
    public void updateLive(Live live, CallBack callBack);


}
