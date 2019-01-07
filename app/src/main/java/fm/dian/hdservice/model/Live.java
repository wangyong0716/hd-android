package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.live.HDLiveInfo.LiveInfo;
import fm.dian.service.live.HDLiveInfo.LiveInfo.Builder;

/**
 * Created by tinx on 4/9/15.
 */
public class Live implements Serializable {

    private final Builder builder;

    public Live() {
        this.builder = LiveInfo.newBuilder();
    }

    public Live(LiveInfo liveInfo) {
        this.builder = liveInfo.toBuilder();
    }

    public LiveInfo getLiveInfo() {
        return builder.build();
    }

    public Long getId() {
        return builder.hasId() ? builder.getId() : null;
    }

    public void setId(Long id) {
        this.builder.setId(id);
    }

    public String getName() {
        return builder.hasName() ? builder.getName() : null;
    }

    public void setName(String name) {
        this.builder.setName(name);
    }

    public Boolean getLocked() {
        return builder.hasLocked() ? builder.getLocked() : null;
    }

    public void setLocked(Boolean locked) {
        this.builder.setLocked(locked);
    }

    public String getPasswd() {
        return builder.hasPasswd() ? builder.getPasswd() : null;
    }

    public void setPasswd(String passwd) {
        this.builder.setPasswd(passwd);
    }

    public Boolean getFree_live() {
        return builder.hasFreeLive() ? builder.getFreeLive() : null;
    }

    public void setFree_live(Boolean free_live) {
        this.builder.setFreeLive(free_live);
    }

    public Boolean getClosed() {
        return builder.hasClosed() ? builder.getClosed() : null;
    }

    public void setClosed(Boolean closed) {
        this.builder.setClosed(closed);
    }

    public Long getRoom_id() {
        return builder.hasRoomId() ? builder.getRoomId() : null;
    }

    public void setRoom_id(Long room_id) {
        this.builder.setRoomId(room_id);
    }

    public Long getUser_id() {
        return builder.hasUserId() ? builder.getUserId() : null;
    }

    public void setUser_id(Long user_id) {
        this.builder.setUserId(user_id);
    }

    public Boolean getSilence() {
        return builder.hasSilence() ? builder.getSilence() : null;
    }

    public void setSilence(Boolean silence) {
        this.builder.setSilence(silence);
    }
}
