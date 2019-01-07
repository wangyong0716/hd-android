package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin;
import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin.AdminLevelType;
import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin.Builder;

/**
 * Created by tinx on 4/8/15.
 */
public class RoomUserAdmin implements Serializable {

    private final Builder builder;

    public RoomUserAdmin() {
        builder = HDRoomUserAdmin.newBuilder();
    }

    public RoomUserAdmin(HDRoomUserAdmin hdRoomUserAdmin) {
        this.builder = hdRoomUserAdmin.toBuilder();
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

    public AdminLevelType getAdminLevel() {
        return builder.hasAdminLevel() ? builder.getAdminLevel() : null;
    }

    public void setAdminLevel(AdminLevelType adminLevel) {
        this.builder.setAdminLevel(adminLevel);
    }

    public boolean isOwner() {
        return getAdminLevel() == AdminLevelType.OWNER;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
