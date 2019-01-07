package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.core.HDTableRoomUserFollow.HDRoomUserFollow;
import fm.dian.service.core.HDTableRoomUserFollow.HDRoomUserFollow.Builder;

/**
 * Created by tinx on 4/8/15.
 */
public class RoomUserFollow implements Serializable {
    private final Builder builder;

    public RoomUserFollow() {
        this.builder = HDRoomUserFollow.newBuilder();
    }

    public RoomUserFollow(HDRoomUserFollow hdRoomUserFollow) {
        this.builder = hdRoomUserFollow.toBuilder();
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

    @Override
    public String toString() {
        return builder.toString();
    }
}
