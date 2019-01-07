package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface HomePageServiceInterface {
    /**
     * 获取首页banner数据列表
     *
     * @param callBack
     */
    public void fetchBanner(CallBack callBack);

    /**
     * 获取首页频道数据列表
     *
     * @param callBack
     */
    public void fetchRoomList(int offset, int limit, CallBack callBack);

}
