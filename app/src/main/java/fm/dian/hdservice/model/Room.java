package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.core.HDTableRoom.HDRoom;
import fm.dian.service.core.HDTableRoom.HDRoom.Builder;

/**
 * Created by tinx on 4/8/15.
 */
public class Room implements Serializable {

    private final String PASSWD = "passwd";

    private final Builder builder;

    public Room() {
        builder = HDRoom.newBuilder();
    }

    public Room(HDRoom hdRoom) {
        this.builder = hdRoom.toBuilder();
    }

    public HDRoom getHdRoom() {
        return builder.build();
    }

    public Long getRoomId() {
        return builder.hasId() ? builder.getId() : null;
    }

    public void setRoomId(Long roomId) {
        this.builder.setId(roomId);
    }

    public String getAvatar() {
        return builder.hasAvatar() ? builder.getAvatar() : null;
    }

    public void setAvatar(String avatar) {
        this.builder.setAvatar(avatar);
    }

    public String getName() {
        return builder.hasName() ? builder.getName() : null;
    }

    public void setName(String name) {
        this.builder.setName(name);
    }

    public String getWebaddr() {
        return builder.hasWebaddr() ? builder.getWebaddr() : null;
    }

    public void setWebaddr(String webaddr) {
        this.builder.setWebaddr(webaddr);
    }

    public String getDescription() {
        return builder.hasDescription() ? builder.getDescription() : null;
    }

    public void setDescription(String description) {
        this.builder.setDescription(description);
    }

    public String getAuthType() {
        return builder.hasAuthType() ? builder.getAuthType() : null;
    }

    public void setAuthType(String authType) {
        this.builder.setAuthType(authType);
    }

    public String getPasswd() {
        return builder.hasPasswd() ? builder.getPasswd() : null;
    }

    public void setPasswd(String passwd) {
        this.builder.setPasswd(passwd);
    }

    public Boolean getIsCanceled() {
        return builder.hasIsCanceled() ? builder.getIsCanceled() : null;
    }

    public void setIsCanceled(Boolean isCanceled) {
        this.builder.setIsCanceled(isCanceled);
    }

    public boolean isNeedPasswd() {
        return PASSWD.equals(getAuthType());
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
