package fm.dian.hdservice;

import android.os.Bundle;

import fm.dian.hddata_android.online.OnlinePublish;
import fm.dian.hddata_android.online.OnlineRequest;
import fm.dian.hdservice.api.OnlineServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.OnlineUserList;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.online.HDOnlineResponseOnlineUserList.ResponseOnlineUserList;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class OnlineService extends BaseService implements OnlineServiceInterface {
    private static final Logger log = Logger.getLogger(OnlineService.class);
    private static OnlineService onlineService;

    private final OnlinePublish publish;

    private OnlineService(int serviceType) {
        super(serviceType);
        publish = new OnlinePublish(handler);
        OnlineRequest.setPublishHandler(publish);
    }

    public static OnlineService getInstance() {
        if (onlineService == null) {
            onlineService = new OnlineService(ServiceType.ONLINE_VALUE);
        }
        return onlineService;
    }

    @Override
    public void fetchOnlineUserNumber(long roomId, CallBack callBack) {
        // TODO Auto-generated method stub

    }

    @Override
    public void fetchOnlineUserList(int offset, int limit, final CallBack callBack) {
        OnlineRequest.getOnlineUserListByOffset(offset, limit, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ResponseOnlineUserList responseOnlineUserList = ResponseOnlineUserList.parseFrom((byte[]) data[0]);
                    if (responseOnlineUserList != null) {
                        bundle.putSerializable("online_user_list", new OnlineUserList(responseOnlineUserList));
                    }
                } catch (Exception e) {
                    log.error("fetchOnlineUserList error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }
}
