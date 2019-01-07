package fm.dian.hdservice;

import android.os.Bundle;
import android.os.Message;

import java.util.LinkedHashSet;
import java.util.Set;

import fm.dian.hddata_android.history.HistoryCountResponse;
import fm.dian.hddata_android.history.HistoryListResponse;
import fm.dian.hddata_android.history.HistoryPublish;
import fm.dian.hddata_android.history.HistoryRequest;
import fm.dian.hddata_android.history.HistoryResponse;
import fm.dian.hdservice.api.HistoryServiceInterface;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HistoryListElement;
import fm.dian.service.history.HDHistoryRecord.HistoryRecord;
import fm.dian.service.history.HDHistoryRequestFetchList.RequestFetchList.FetchType;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/25/15.
 */
public class HistoryService extends BaseService implements HistoryServiceInterface {


    public static final int NotAdmin = 1300;
    public static final int AlreadyStart = 1301;
    public static final int NotStartYet = 1302;
    public static final int HistoryIdError = 1303;

    private static HistoryService historyService;
    private final HistoryPublish publish;

    private HistoryService(int serviceType) {
        super(serviceType);
        publish = HistoryPublish.getInstance(handler);
        HistoryRequest.setPublishHandler(publish);
        HistoryRequest.resetVersionNumber();
    }

    public static HistoryService getInstance() {
        if (historyService == null) {
            historyService = new HistoryService(ServiceType.HISTORY_VALUE);
        }
        return historyService;
    }

    @Override
    public void fetchHistoryCount(long roomId, final CallBack callBack) {
        HistoryRequest.getCountByRoomId(roomId, new HistoryCountResponse() {
            @Override
            public void response(int error_code, int published, int unPublished, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                if (error_code == OK) {
                    bundle.putInt("published_count", published);
                    bundle.putInt("unPublished_count", unPublished);
                }
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void fetchHistoryList(long roomId, int publish, int offset, int limit,
                                 final CallBack callBack) {
        int fetchType;
        if (publish == 0) {
            fetchType = FetchType.PUBLISHED_VALUE;
        } else if (publish == 1) {
            fetchType = FetchType.UNPUBLISHED_VALUE;
        } else {
            fetchType = FetchType.BOTH_VALUE;
        }

        HistoryRequest.getListByRoomId(roomId, fetchType, offset, limit, new HistoryListResponse() {
            @Override
            public void response(int error_code, Object[] published, Object[] unPublished, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                if (error_code == OK) {

                    Set<HistoryListElement> publishSet = new LinkedHashSet<HistoryListElement>(published.length);
                    for (Object obj : published) {
                        try {
                            HistoryRecord record = HistoryRecord.parseFrom((byte[]) obj);
                            if (record != null) {
                                publishSet.add(new HistoryListElement(record));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Set<HistoryListElement> unpublishSet = new LinkedHashSet<HistoryListElement>(unPublished.length);
                    for (Object obj : unPublished) {
                        try {
                            HistoryRecord record = HistoryRecord.parseFrom((byte[]) obj);
                            if (record != null) {
                                unpublishSet.add(new HistoryListElement(record));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    bundle.putSerializable("published", (java.io.Serializable) publishSet);
                    bundle.putSerializable("unpublished", (java.io.Serializable) unpublishSet);
                }
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchHistory(long historyId, CallBack callBack) {
        // TODO Auto-generated method stub


    }

    @Override
    public void updateHistory(long historyId, String name, boolean isPublish,
                              boolean isDelete, final CallBack callBack) {
        HistoryRequest.updateRecordByHistoryId(historyId, name, isPublish, isDelete, new HistoryResponse() {
            @Override
            public void response(int error_code, long result, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void startRecord(final CallBack callBack) {
        HistoryRequest.startRecord(new HistoryResponse() {
            @Override
            public void response(int error_code, long result, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void stopRecord(final CallBack callBack) {
        HistoryRequest.stopRecord(new HistoryResponse() {
            @Override
            public void response(int error_code, long result, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                bundle.putLong("history_id", result);
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void getRecordTime(final CallBack callBack) {
        HistoryRequest.getRecordTime(new HistoryResponse() {
            @Override
            public void response(int error_code, long result, Object client_data) {
                Message message = Message.obtain(handler, RESPONSE, callBack);
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", error_code);
                bundle.putLong("record_time", result);
                message.setData(bundle);
                message.sendToTarget();
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    public boolean isRecording(){
        return publish.isRecording();
    }

    public void clear(){
        HistoryRequest.resetVersionNumber();
        publish.clearIsRecording();
    }

}
