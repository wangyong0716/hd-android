package fm.dian.hdservice.api;

import java.util.List;

import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Card;

/**
 * Created by tinx on 3/25/15.
 */
public interface BlackBoardServiceInterface {

    /**
     * 发送小黑板
     *
     * @param cardId   插入到哪个后面，不填就插入到最前面
     * @param cards
     * @param callBack
     */
    public void sendBlackBoard(long cardId, List<Card> cards, CallBack callBack);

    /**
     * 获取小黑板列表
     *
     * @param cardIds
     * @param callBack
     */
    public void fetchBlackBoardList(long[] cardIds, CallBack callBack);

    /**
     * 删除多张小黑板图
     *
     * @param cardIds
     * @param callBack
     */
    public void deleteBlackBoardList(long[] cardIds, CallBack callBack);

    /**
     * 主动滚动小黑板
     *
     * @param callBack
     * @cardId 当前显示的小黑板图片id
     */
    public void changeBlackBoardList(Long cardId, CallBack callBack);

    /**
     * 关闭小黑板
     *
     * @param callBack
     */
    public void closeBlackBoardList(CallBack callBack);


}
