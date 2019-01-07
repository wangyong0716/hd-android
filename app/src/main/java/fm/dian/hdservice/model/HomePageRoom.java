package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.homepage.HDHomepageRoomStatus.HomepageRoomStatus;
import fm.dian.service.homepage.HDHomepageRoomStatus.HomepageRoomStatus.Builder;

/**
 * Created by tinx on 4/9/15.
 */
public class HomePageRoom implements Serializable {
    private final Builder builder;

    public HomePageRoom() {
        builder = HomepageRoomStatus.newBuilder();
    }

    public HomePageRoom(HomepageRoomStatus homepageRoomStatus) {
        this.builder = homepageRoomStatus.toBuilder();
    }

    public Long getRoomId() {
        return builder.hasRoomId() ? builder.getRoomId() : null;
    }

    public void setRoomId(Long roomId) {
        this.builder.setRoomId(roomId);
    }


    public Long getOnlineUserNumber() {
        return builder.hasOnlineUserNumber() ? builder.getOnlineUserNumber() : null;
    }

    public void setOnlineUserNumber(Long onlineUserNumber) {
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
}
