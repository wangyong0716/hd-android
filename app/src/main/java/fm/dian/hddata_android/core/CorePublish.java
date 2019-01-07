package fm.dian.hddata_android.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.RoomUserAdmin;
import fm.dian.hdservice.model.RoomUserIgnore;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.core.HDTableRoom.HDRoom;
import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin;
import fm.dian.service.core.HDTableRoomUserIgnore.HDRoomUserIgnore;

/**
 * Created by tinx on 4/3/15.
 */
public class CorePublish {

    private static final Logger log = Logger.getLogger(CorePublish.class);

    public static final int ROOM_PUBLISH = 1;
    public static final int ROOM_ADMIN_PUBLISH = 2;
    public static final int ROOM_IGNORE_PUBLISH = 3;

    private Handler handler;

    public CorePublish(Handler handler) {
        this.handler = handler;
    }

    public void roomPublish(byte[] bytes) {
        Message message = Message.obtain(handler, CoreService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", ROOM_PUBLISH);
        try {
            HDRoom pbRoom = HDRoom.parseFrom(bytes);
            if (pbRoom != null) {
                bundle.putSerializable("room", new Room(pbRoom));
                log.debug("roomPublish: room={}", pbRoom);
            }
        } catch (Exception e) {
            log.error("roomPublish error", e);
        }
        message.setData(bundle);
        message.sendToTarget();
    }

    public void roomAdminPublish(Object[] data) {
        Message message = Message.obtain(handler, CoreService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", ROOM_ADMIN_PUBLISH);
        try {
            ArrayList<RoomUserAdmin> RoomUserAdminArrayList = new ArrayList<RoomUserAdmin>(data.length);
            for (Object obj : data) {
                HDRoomUserAdmin pbRoomUserAdmin = HDRoomUserAdmin.parseFrom((byte[]) obj);
                if (pbRoomUserAdmin != null) {
                    RoomUserAdminArrayList.add(new RoomUserAdmin(pbRoomUserAdmin));
                    log.debug("roomAdminPublish:room_user_admin={}", pbRoomUserAdmin);
                }
            }
            bundle.putSerializable("room_user_admin_list", RoomUserAdminArrayList);
        } catch (Exception e) {
            log.error("roomAdminPublish error", e);
        }
        message.setData(bundle);
        message.sendToTarget();
    }

    public void roomIgnorePublish(Object[] data) {
        Message message = Message.obtain(handler, CoreService.PUBLISH);
        Bundle bundle = new Bundle();
        bundle.putInt("publish_type", ROOM_IGNORE_PUBLISH);
        try {
            ArrayList<RoomUserIgnore> hdRoomUserIgnoreArrayList = new ArrayList<RoomUserIgnore>(data.length);
            for (Object obj : data) {
                HDRoomUserIgnore pbRoomUserIgnore = HDRoomUserIgnore.parseFrom((byte[]) obj);
                if (pbRoomUserIgnore != null) {
                    hdRoomUserIgnoreArrayList.add(new RoomUserIgnore(pbRoomUserIgnore));
                    log.debug("roomIgnorePublish:room_user_ignore={}", pbRoomUserIgnore);
                }
            }
            bundle.putSerializable("room_user_ignore_list", hdRoomUserIgnoreArrayList);
        } catch (Exception e) {
            log.error("roomIgnorePublish error", e);
        }
        message.setData(bundle);
        message.sendToTarget();
    }
}
