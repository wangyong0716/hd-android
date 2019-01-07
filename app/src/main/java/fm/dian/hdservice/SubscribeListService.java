package fm.dian.hdservice;

import android.os.Bundle;

import java.util.ArrayList;

import fm.dian.hddata_android.subscribe.SubscribeListRequest;
import fm.dian.hdservice.api.SubscribeListInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.SubscribeListRoom;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.rpc.HDServiceType.ServiceType;
import fm.dian.service.subscribelist.HDSubscribelistResponseRoomList.SubscribelistRoom;

/**
 * Created by tinx on 3/25/15.
 */
public class SubscribeListService extends BaseService implements SubscribeListInterface {

    private static final Logger log = Logger.getLogger(SubscribeListService.class, LOG_TAG);
    private static SubscribeListService subscribeListService;

    private SubscribeListService(int serviceType) {
        super(serviceType);
    }

    public static SubscribeListService getInstance() {
        if (subscribeListService == null) {
            subscribeListService = new SubscribeListService(ServiceType.SUBSCRIBE_LIST_VALUE);
        }
        return subscribeListService;
    }

    @Override
    public void fetchSubscribelist(int startIndex, int endIndex,
                                   final CallBack callBack) {

        SubscribeListRequest.getSubscribeRoomsByOffset(startIndex, endIndex, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<SubscribeListRoom> subscribeListRoomArrayList = new ArrayList<SubscribeListRoom>(data.length);
                    for (Object obj : data) {
                        SubscribelistRoom subscribelistRoom = SubscribelistRoom.parseFrom((byte[]) obj);
                        subscribeListRoomArrayList.add(new SubscribeListRoom(subscribelistRoom));
                    }
                    bundle.putSerializable("room_list", subscribeListRoomArrayList);
                } catch (Exception e) {
                    log.error("fetchSubscribelist error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }
}
