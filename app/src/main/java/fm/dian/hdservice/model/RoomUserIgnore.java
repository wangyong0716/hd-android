package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.core.HDTableRoomUserIgnore.HDRoomUserIgnore;
import fm.dian.service.core.HDTableRoomUserIgnore.HDRoomUserIgnore.Builder;

/**
 * Created by tinx on 4/8/15.
 */
public class RoomUserIgnore implements Serializable {

    private Builder builder;

    public RoomUserIgnore() {
        this.builder = HDRoomUserIgnore.newBuilder();
    }

    public RoomUserIgnore(HDRoomUserIgnore hdRoomUserIgnore) {
        this.builder = hdRoomUserIgnore.toBuilder();
    }

    public Long getId() {
        return builder.hasId() ? builder.getId() : null;
    }

    public void setId(Long id) {
        this.builder.setId(id);
    }

    public Long getUserId() {
        return builder.hasUserId() ? builder.getUserId() : null;
    }

    public void setUserId(Long userId) {
        this.builder.setUserId(userId);
    }

    public Long getRoomId() {
        return builder.hasRoomId() ? builder.getRoomId() : null;
    }

    public void setRoomId(Long roomId) {
        this.builder.setRoomId(roomId);
    }

    public Long getDuration() {
        return builder.hasDuration() ? builder.getDuration() : null;
    }

    public void setDuration(Long duration) {
        this.builder.setDuration(duration);
    }

    public String getDescription() {
        return builder.hasDescription() ? builder.getDescription() : null;
    }

    public void setDescription(String description) {
        this.builder.setDescription(description);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
