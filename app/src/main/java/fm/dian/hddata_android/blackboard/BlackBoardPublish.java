package fm.dian.hddata_android.blackboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.BlackBoardService;

/**
 * Created by tinx on 4/8/15.
 */
public class BlackBoardPublish {

    private static BlackBoardPublish blackBoardPublish;
    public static final int BLACKBOARD_PUBLISH = 1;
    private final Handler handler;
    private volatile long currentCardId;
    private List<Long> cardIds;

    private BlackBoardPublish(Handler handler) {
        this.handler = handler;
        cardIds = new ArrayList<>();
    }

    public static BlackBoardPublish getInstance(Handler handler) {
        if (blackBoardPublish == null) {
            blackBoardPublish = new BlackBoardPublish(handler);
        }
        return blackBoardPublish;
    }

    public void blackboardPublish(Object[] card_ids, long current_card_id) {
        Message message = Message.obtain(handler, BlackBoardService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", BLACKBOARD_PUBLISH);
//        bundle.putLong("current_card_id", current_card_id);
//        bundle.putLongArray("card_ids", (long[]) card_ids[0]);
        long[] cardids = (long[]) card_ids[0];
        resetCardId(cardids);
        this.currentCardId = current_card_id;
        message.setData(bundle);
        message.sendToTarget();
    }

    private void resetCardId(long[] card_ids) {
        synchronized (cardIds) {
            cardIds.clear();
            if (card_ids != null && card_ids.length > 0) {
                for (long id : card_ids) {
                    cardIds.add(id);
                }
            }
        }
    }

    public List<Long> getCardIds() {
        synchronized (cardIds) {
            return new ArrayList<>(cardIds);
        }
    }

    public Long getCurrentCardId() {
        return currentCardId;
    }

    public void clearCardIds() {
        synchronized (cardIds) {
            cardIds.clear();
        }
    }

    public void clearCurrentCardId() {
        currentCardId = 0;
    }
}