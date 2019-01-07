package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface SubscribeListInterface {

    /**
     * 获取订阅频道列表数据
     *
     * @param startIndex
     * @param end
     * @param callBack
     */
    public void fetchSubscribelist(int startIndex, int endIndex, CallBack callBack);

}
