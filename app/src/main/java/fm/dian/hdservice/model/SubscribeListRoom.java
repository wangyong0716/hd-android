package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.subscribelist.HDSubscribelistResponseRoomList.SubscribelistRoom;
import fm.dian.service.subscribelist.HDSubscribelistResponseRoomList.SubscribelistRoom.Builder;

/**
 * Created by tinx on 4/9/15.
 */
public class SubscribeListRoom implements Serializable {

    private Builder builder;

    public SubscribeListRoom() {
        this.builder = SubscribelistRoom.newBuilder();
    }

    public SubscribeListRoom(SubscribelistRoom subscribelistRoom) {
        this.builder = subscribelistRoom.toBuilder();
    }

    public Long getRoomId() {
        return builder.hasRoomId() ? builder.getRoomId() : null;
    }

    public void setRoomId(Long roomId) {
        this.builder.setRoomId(roomId);
    }


    public Integer getOnlineUserNumber() {
        return builder.hasOnlineUserNumber() ? builder.getOnlineUserNumber() : null;
    }

    public void setOnlineUserNumber(Integer onlineUserNumber) {
        this.builder.setOnlineUserNumber(onlineUserNumber);
    }

    public Boolean getIsLive() {
        return builder.hasIsLive() ? builder.getIsLive() : null;
    }

    public void setIsLive(Boolean isLive) {
        this.builder.setIsLive(isLive);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscribeListRoom that = (SubscribeListRoom) o;
        Long roomId = getRoomId();

        if (roomId != null ? !roomId.equals(that.getRoomId()) : that.getRoomId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getRoomId() != null ? getRoomId().hashCode() : 0;
    }
}
