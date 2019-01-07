package fm.dian.hdservice.api;

import java.util.List;
import java.util.Set;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 3/25/15.
 */
public interface MediaServiceInterface {

    /**
     * 开始上麦
     *
     * @param callBack
     */
    public void startAudioRecord(CallBack callBack);

    /**
     * 视频上麦请求
     * @param callBack
     */
    public void startVideoRecord(CallBack callBack);

    /**
     * 视频下麦请求
     * @param callBack
     */
    public void stopVideoRecord(CallBack callBack);

    /**
     * 结束上麦
     *
     * @param callBack
     */
    public void stopAudioRecord(CallBack callBack);

    /**
     * 设置麦上成员
     *
     * @param speakers
     */
    public void startAudioPlay(List<Long> speakers);

    public void stopAudioPlay();

    public void startVideoPlay(Long userId);

    public void stopVideoPlay();

    public boolean isAudioOpen();

    public boolean isAudioRecordOpen();

    public boolean isVideoOpen();

    public boolean isVideoRecordOpen();


    /*
     * invite speak
     */
    public void inviteSpeak(long user_id, CallBack callBack);

}
