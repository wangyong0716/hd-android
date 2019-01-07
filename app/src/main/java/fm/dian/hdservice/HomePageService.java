package fm.dian.hdservice;

import android.os.Bundle;

import java.util.ArrayList;

import fm.dian.hddata_android.homepage.HomePageRequest;
import fm.dian.hdservice.api.HomePageServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HomePageRoom;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.homepage.HDHomepageRoomStatus.HomepageRoomStatus;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class HomePageService extends BaseService implements HomePageServiceInterface {

    private static HomePageService homePageService;
    private static final Logger log = Logger.getLogger(HomePageService.class, LOG_TAG);

    private HomePageService(int serviceType) {
        super(serviceType);
    }

    public static HomePageService getInstance() {
        if (homePageService == null) {
            homePageService = new HomePageService(ServiceType.HOMEPAGE_VALUE);
        }
        return homePageService;
    }

    @Override
    public void fetchBanner(CallBack callBack) {

        // TODO Auto-generated method stub

    }

    @Override
    public void fetchRoomList(int offset, int limit, final CallBack callBack) {

        HomePageRequest.getIndexRoomsWithOffset(offset, limit, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<HomePageRoom> homePageRoomArrayList = new ArrayList<HomePageRoom>(data.length);
                    for (Object obj : data) {

                        HomepageRoomStatus homepageRoomStatus = HomepageRoomStatus.parseFrom((byte[]) obj);
                        homePageRoomArrayList.add(new HomePageRoom(homepageRoomStatus));

                    }
                    bundle.putSerializable("room_list", homePageRoomArrayList);
                } catch (Exception e) {
                    log.error("fetchRoomList error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }
}
