package fm.dian.hdservice;

import android.os.Bundle;

import java.util.ArrayList;

import fm.dian.hddata_android.live.LivePublish;
import fm.dian.hddata_android.live.LiveRequest;
import fm.dian.hdservice.api.LiveServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.live.HDLiveInfo.LiveInfo;
import fm.dian.service.live.HDLiveListInfo.LiveListInfo;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class LiveService extends BaseService implements LiveServiceInterface {

    private static final Logger log = Logger.getLogger(LiveService.class, LOG_TAG);
    private static LiveService liveService;
    private final LivePublish publish;

    private LiveService(int serviceType) {
        super(serviceType);
        publish = LivePublish.getInstance(handler);
        LiveRequest.setPublishHandler(publish);
    }

    public static LiveService getInstance() {
        if (liveService == null) {
            liveService = new LiveService(ServiceType.LIVE_VALUE);
        }
        return liveService;
    }

    @Override
    public void startLive(final Live live, final CallBack callBack) {

        LiveRequest.startLive(live.getLiveInfo().toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    LiveInfo liveInfo = LiveInfo.parseFrom((byte[]) data[0]);
                    if (liveInfo != null) {
                        bundle.putSerializable("live", new Live(liveInfo));
                    }
                } catch (Exception e) {
                    log.error("startLive error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchLiveList(long roomId, int offset, int limit,
                              final CallBack callBack) {
        LiveRequest.fetchLiveList(roomId, offset, limit, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<LiveListElement> liveListElements = new ArrayList<LiveListElement>(data.length);
                    for (Object obj : data) {

                        LiveListInfo liveListInfo = LiveListInfo.parseFrom((byte[]) obj);
                        if (liveListInfo != null) {
                            liveListElements.add(new LiveListElement(liveListInfo));
                        }

                    }
                    bundle.putSerializable("live_list", liveListElements);
                } catch (Exception e) {
                    log.error("fetchLiveList error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void updateLive(Live live, final CallBack callBack) {
        LiveRequest.updateLive(live.getLiveInfo().toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    LiveInfo liveInfo = LiveInfo.parseFrom((byte[]) data[0]);
                    if (liveInfo != null) {
                        bundle.putSerializable("live", new Live(liveInfo));
                    }
                } catch (Exception e) {
                    log.error("updateLive error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchLiveInfo(final long[] liveIds, final CallBack callBack) {

        LiveRequest.fetchLiveInfo(liveIds, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<Live> lives = new ArrayList<Live>(data.length);

                    for (Object obj : data) {
                        LiveInfo liveInfo = LiveInfo.parseFrom((byte[]) obj);
                        if (liveInfo != null) {
                            if (AuthService.getInstance().getLiveId() != null && liveInfo.getId() == AuthService.getInstance().getLiveId()) {
                                publish.setLive(new Live(liveInfo));
                            }
                            lives.add(new Live(liveInfo));
                        }
                    }
                    bundle.putSerializable("lives", lives);
                } catch (Exception e) {
                    log.error("fetchLiveInfo error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void closeLive(long liveId, final CallBack callBack) {
        LiveRequest.closeLive(liveId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    /*
     * Deprecated
     */
    @Override
    public void kickUser(long userId, CallBack callBack) {
        // TODO Auto-generated method stub

    }

    /*
     * Deprecated
     */
    @Override
    public void stopLive(long liveId, CallBack callBack) {
        // TODO Auto-generated method stub

    }

    public void clear() {
        LiveRequest.resetLiveVersionNumber();
        LiveRequest.resetUserVersionNumber();
        publish.clearLive();
    }

    public Live getLive() {
        return publish.getLive();
    }


}
