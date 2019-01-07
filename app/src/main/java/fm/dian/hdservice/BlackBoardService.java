package fm.dian.hdservice;

import android.os.Bundle;
import android.util.Log;

import com.google.protobuf.ExtensionRegistry;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hddata_android.blackboard.BlackBoardPublish;
import fm.dian.hddata_android.blackboard.BlackBoardRequest;
import fm.dian.hdservice.api.BlackBoardServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Card;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.service.blackboard.HDBlackboardCard;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class BlackBoardService extends BaseService implements BlackBoardServiceInterface {

    private static final Logger log = Logger.getLogger(BlackBoardService.class, LOG_TAG);

    private static BlackBoardService blackBoardService;
    private final ExtensionRegistry registry = ExtensionRegistry.newInstance();

    private final BlackBoardPublish publish;

    private BlackBoardService(int serviceType) {
        super(serviceType);
        HDBlackboardCard.registerAllExtensions(registry);
        publish = BlackBoardPublish.getInstance(handler);
        BlackBoardRequest.setPublishHandler(publish);
        BlackBoardRequest.resetVersionNumber();
    }

    public static BlackBoardService getInstance() {
        if (blackBoardService == null) {
            blackBoardService = new BlackBoardService(ServiceType.BLACKBOARD_VALUE);
        }
        return blackBoardService;
    }

    @Override
    public void sendBlackBoard(long cardId, List<Card> cards,
                               final CallBack callBack) {

        log.debug("sendBlackBoard:cardId={},cards={}", cardId, cards);
        byte[][] hdCards = new byte[cards.size()][];
        for (int i = 0; i < cards.size(); i++) {
            hdCards[i] = cards.get(i).getCard().toByteArray();
        }
        BlackBoardRequest.sendCards(hdCards, cardId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                Object[] data = (Object[]) bytes;
                long[] card_id_list = (long[]) data[0];
                bundle.putLongArray("card_id_list", card_id_list);
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchBlackBoardList(final long[] cardIds, final CallBack callBack) {
        BlackBoardRequest.fetchCards(cardIds, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<Card> cards = new ArrayList<Card>(data.length);
                    for (Object obj : data) {
                        HDBlackboardCard.Card hdCard = HDBlackboardCard.Card.parseFrom((byte[]) obj, registry);
                        if (hdCard != null) {
                            cards.add(new Card(hdCard));
                        }
                    }
                    bundle.putSerializable("cards", cards);
                } catch (Exception e) {
                    log.error("fetchBlackBoardList error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void deleteBlackBoardList(long[] cardIds, final CallBack callBack) {
        BlackBoardRequest.deleteCards(cardIds, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void changeBlackBoardList(Long cardId, final CallBack callBack) {
        Long userId = AuthService.getInstance().getUserId();
        Long roomId = AuthService.getInstance().getCurrentRoomId();
        boolean isAdmin = ActionAuthService.getInstance().isRole(userId,roomId,0L, AuthActionRequest.UserAuthType.UserAdmin);
        boolean isOwner = ActionAuthService.getInstance().isRole(userId,roomId,0L, AuthActionRequest.UserAuthType.UserOwner);
        if(isAdmin || isOwner){
            BlackBoardRequest.changeToCard(cardId, new BaseResponse(callBack, handler) {
                @Override
                protected boolean parseData(Object bytes, Bundle bundle) {
                    Log.d(HongdianConstants.DEBUG_LIEYUNYE,"切换小黑板成功"+bundle.getInt("error_code"));
                    return true;
                }
            }, null, TIMEOUT_MILLISECONDS);
        }else {
            log.debug("no right to change blackboard");
        }
    }

    @Override
    public void closeBlackBoardList(final CallBack callBack) {
        BlackBoardRequest.closeBlackboard(new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    public Long getCurrentCardId() {
        return publish.getCurrentCardId();
    }

    public List<Long> getCardIds() {
        return publish.getCardIds();
    }

    public void clear() {
        publish.clearCardIds();
        publish.clearCurrentCardId();
        BlackBoardRequest.resetVersionNumber();
    }
}
