package fm.dian.hdservice.model;

import fm.dian.service.history.HDHistoryRecord.HistoryRecord;
import fm.dian.service.history.HDHistoryRecord.HistoryRecord.Builder;

/**
 * Created by tinx on 4/15/15.
 */
public class HistoryListElement {
    private final Builder builder;

    public HistoryListElement() {
        this.builder = HistoryRecord.newBuilder();
    }

    public HistoryListElement(HistoryRecord historyRecord) {
        this.builder = historyRecord.toBuilder();
    }


    public Long getHistoryId() {
        return builder.hasHistoryId() ? builder.getHistoryId() : null;
    }

    public Long getRoomId() {
        return builder.hasRoomId() ? builder.getRoomId() : null;
    }

    public String getName() {
        return builder.hasName() ? builder.getName() : null;
    }

    public Long getStartTime() {
        return builder.hasName() ? builder.getStart() : null;
    }

    public Long getEndTime() {
        return builder.hasEnd() ? builder.getEnd() : null;
    }

    public Boolean getPublish() {
        return builder.hasPublish() ? builder.getPublish() : null;
    }

    public Long getPublishTime() {
        return builder.hasPubTime() ? builder.getPubTime() : null;
    }

    public String getAppUrl() {
        return builder.hasAppUrl() ? builder.getAppUrl() : null;
    }

    public String getShareUrl() {
        return builder.hasShareUrl() ? builder.getShareUrl() : null;
    }

    public Long getDuration() {
        return builder.hasDuration() ? builder.getDuration() : null;
    }

    public Boolean getComplete() {
        return builder.hasComplete() ? builder.getComplete() : null;
    }

    public Long getPlayCount() {
        return builder.hasPlayCount() ? builder.getPlayCount() : null;
    }
}
