package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface HistoryServiceInterface {

    /**
     * 获取录制历史条数
     *
     * @param roomId
     * @param callBack
     */
    public void fetchHistoryCount(long roomId, CallBack callBack);

    /**
     * 获取录制历史记录
     *
     * @param roomId
     * @param offset
     * @param limit
     * @param callBack
     */
    public void fetchHistoryList(long roomId, int publish, int offset, int limit, CallBack callBack);

    /**
     * 获取指定历史录制数据
     *
     * @param historyId
     * @param callBack
     */
    public void fetchHistory(long historyId, CallBack callBack);


    /**
     * 更新指定历史录制信息
     *
     * @param historyId
     * @param name
     * @param isPublish
     * @param isDelete
     * @param callBack
     */
    public void updateHistory(long historyId, String name, boolean isPublish, boolean isDelete, CallBack callBack);


//    public static native void getRecordTime(HistoryResponse response,
//                                            Object client_data,
//                                            int timeout_milliseconds);

    /**
     * 开始录制节目
     */
    public void startRecord(CallBack callBack);

    /**
     * 结束录制节目
     */
    public void stopRecord(CallBack callBack);


    /**
     * 获取录制时间
     *
     * @param callBack
     */
    public void getRecordTime(CallBack callBack);
}
