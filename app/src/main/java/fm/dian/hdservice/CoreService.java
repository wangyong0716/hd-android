package fm.dian.hdservice;

import android.os.Bundle;

import java.util.ArrayList;

import fm.dian.hddata_android.core.CorePublish;
import fm.dian.hddata_android.core.CoreRequest;
import fm.dian.hdservice.api.CoreServiceInterface;
import fm.dian.hdservice.base.BaseResponse;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.RoomUserAdmin;
import fm.dian.hdservice.model.RoomUserFollow;
import fm.dian.hdservice.model.RoomUserIgnore;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.util.Logger;
import fm.dian.service.core.HDTableRoom;
import fm.dian.service.core.HDTableRoom.HDRoom;
import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin;
import fm.dian.service.core.HDTableRoomUserAdmin.HDRoomUserAdmin.AdminLevelType;
import fm.dian.service.core.HDTableRoomUserFollow.HDRoomUserFollow;
import fm.dian.service.core.HDTableRoomUserIgnore.HDRoomUserIgnore;
import fm.dian.service.core.HDTableUser.HDUser;
import fm.dian.service.rpc.HDServiceType.ServiceType;

/**
 * Created by tinx on 3/23/15.
 */
public class CoreService extends BaseService implements CoreServiceInterface {

    private static final Logger log = Logger.getLogger(CoreService.class, LOG_TAG);

    private static CoreService coreService;

    private final int cacheTimeoutSeconds = 10;

    private final CorePublish publish;

    private CoreService(int serviceType) {
        super(serviceType);
        publish = new CorePublish(handler);
        CoreRequest.setPublishHandler(publish);
    }

    public static CoreService getInstance() {
        if (coreService == null) {
            coreService = new CoreService(ServiceType.CORE_VALUE);
        }
        return coreService;
    }

    @Override
    public void fetchUserInfo(Long userId, final CallBack callBack) {


        CoreRequest.getUserByUserId(cacheTimeoutSeconds, userId, new BaseResponse(callBack, handler) {

            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    HDUser pbUser = HDUser.parseFrom((byte[]) data[0]);
                    if (pbUser != null) {
                        User user = new User(pbUser);
                        bundle.putSerializable("user", user);
                    }
                } catch (Exception e) {
                    log.error("fetchUserInfo error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void updateUserInfo(User user, final CallBack callBack) {
        HDUser pbUser = user.getHdUser();
        CoreRequest.updateUser(pbUser.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    HDUser pbUser = HDUser.parseFrom((byte[]) data[0]);
                    if (pbUser != null) {
                        User user = new User(pbUser);
                        bundle.putSerializable("user", user);
                    }
                } catch (Exception e) {
                    log.error("updateUserInfo error", e);
                    return false;
                }
                return true;
            }
        }, null);

    }

    @Override
    public void fetchRoomByRoomId(long roomId, CallBack callBack) {
        CoreRequest.getRoomByRoomId(cacheTimeoutSeconds, roomId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    HDRoom pbRoom = HDTableRoom.HDRoom.parseFrom((byte[]) data[0]);
                    if (pbRoom != null) {
                        Room room = new Room(pbRoom);
                        bundle.putSerializable("room", room);
                    }
                } catch (Exception e) {
                    log.error("fetchRoomByRoomId error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void updateRoom(Room room, CallBack callBack) {
        HDRoom pbRoom = room.getHdRoom();
        CoreRequest.updateRoom(pbRoom.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    HDRoom pbRoom = HDTableRoom.HDRoom.parseFrom((byte[]) data[0]);
                    Room room = new Room(pbRoom);
                    bundle.putSerializable("room", room);
                } catch (Exception e) {
                    log.error("updateRoom error", e);
                    return false;
                }
                return true;
            }
        }, null);
    }

    @Override
    public void createRoom(Room room, CallBack callBack) {
        HDRoom pbRoom = room.getHdRoom();
        CoreRequest.insertRoom(pbRoom.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    HDRoom pbRoom = HDTableRoom.HDRoom.parseFrom((byte[]) data[0]);
                    Room room = new Room(pbRoom);
                    bundle.putSerializable("room", room);
                } catch (Exception e) {
                    log.error("createRoom error", e);
                    return false;
                }
                return true;
            }
        }, null);
    }

    @Override
    public void searchRoom(String webaddr, CallBack callBack) {
        CoreRequest.getRoomByWebAddress(cacheTimeoutSeconds, webaddr, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    if (null == data || data.length <= 0) {
                        return false;
                    }
                    HDRoom pbRoom = HDTableRoom.HDRoom.parseFrom((byte[]) data[0]);
                    Room room = new Room(pbRoom);
                    bundle.putSerializable("room", room);
                } catch (Exception e) {
                    log.error("searchRoom error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void addFollow(long roomId, long userId, CallBack callBack) {
        HDRoomUserFollow roomUserFollow = HDRoomUserFollow.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .build();
        CoreRequest.insertRoomUserFollow(roomUserFollow.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);
    }

    @Override
    public void cancelFollow(long roomId, long userId, CallBack callBack) {
        HDRoomUserFollow roomUserFollow = HDRoomUserFollow.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .build();
        CoreRequest.deleteRoomUserFollow(roomUserFollow.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);

    }

    @Override
    public void fetchFollowByRoomId(long roomId, CallBack callBack) {
        CoreRequest.getRoomUserFollowByRoomId(cacheTimeoutSeconds, roomId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<RoomUserFollow> roomUserFollowList = new ArrayList<RoomUserFollow>(data.length);
                    for (Object obj : data) {

                        HDRoomUserFollow pbRoomUserFollow = HDRoomUserFollow.parseFrom((byte[]) obj);
                        RoomUserFollow roomUserFollow = new RoomUserFollow(pbRoomUserFollow);
                        if (roomUserFollow != null) {
                            roomUserFollowList.add(roomUserFollow);
                        }

                    }
                    bundle.putSerializable("room_user_follow_list", roomUserFollowList);
                } catch (Exception e) {
                    log.error("fetchFollowByRoomId error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);
    }

    @Override
    public void fetchAdminsByRoomId(long roomId, CallBack callBack) {
        CoreRequest.getRoomUserAdminByRoomId(cacheTimeoutSeconds, roomId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<RoomUserAdmin> roomUserAdminList = new ArrayList<RoomUserAdmin>(data.length);
                    for (Object obj : data) {

                        HDRoomUserAdmin pbRoomUserAdmin = HDRoomUserAdmin.parseFrom((byte[]) obj);
                        RoomUserAdmin roomUserAdmin = new RoomUserAdmin(pbRoomUserAdmin);
                        if (roomUserAdmin != null) {
                            roomUserAdminList.add(roomUserAdmin);
                        }

                    }
                    bundle.putSerializable("room_user_admin_list", roomUserAdminList);
                } catch (Exception e) {
                    log.error("fetchAdminsByRoomId error", e);
                    return false;
                }
                return true;
            }

        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void addAdmin(long roomId, long userId, CallBack callBack) {
        HDRoomUserAdmin roomUserAdmin = HDRoomUserAdmin.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .setAdminLevel(AdminLevelType.ADMIN)
                .build();
        CoreRequest.insertRoomUserAdmin(roomUserAdmin.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);

    }

    @Override
    public void cancelAdmin(long roomId, long userId, CallBack callBack) {
        HDRoomUserAdmin roomUserAdmin = HDRoomUserAdmin.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .setAdminLevel(AdminLevelType.ADMIN)
                .build();
        CoreRequest.deleteRoomUserAdmin(roomUserAdmin.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);
    }

    @Override
    public void fetchIgnoreByRoomId(long roomId, CallBack callBack) {
        CoreRequest.getRoomUserIgnoreByRoomId(cacheTimeoutSeconds, roomId, new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                try {
                    Object[] data = (Object[]) bytes;
                    ArrayList<RoomUserIgnore> roomUserIgnoreList = new ArrayList<RoomUserIgnore>(data.length);
                    for (Object obj : data) {

                        HDRoomUserIgnore pbRoomUserIgnore = HDRoomUserIgnore.parseFrom((byte[]) obj);
                        RoomUserIgnore roomUserIgnore = new RoomUserIgnore(pbRoomUserIgnore);
                        if (roomUserIgnore != null) {
                            roomUserIgnoreList.add(roomUserIgnore);
                        }

                    }
                    bundle.putSerializable("room_user_ignore_list", roomUserIgnoreList);
                } catch (Exception e) {
                    log.error("fetchIgnoreByRoomId error", e);
                    return false;
                }
                return true;
            }
        }, null, TIMEOUT_MILLISECONDS);

    }

    @Override
    public void addIgnore(long roomId, long userId, CallBack callBack) {
        HDRoomUserIgnore roomUserIgnore = HDRoomUserIgnore.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .build();
        CoreRequest.insertRoomUserIgnore(roomUserIgnore.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);
    }

    @Override
    public void cancelIgnore(long roomId, long userId, CallBack callBack) {
        HDRoomUserIgnore roomUserIgnore = HDRoomUserIgnore.newBuilder()
                .setRoomId(roomId)
                .setUserId(userId)
                .build();
        CoreRequest.deleteRoomUserIgnore(roomUserIgnore.toByteArray(), new BaseResponse(callBack, handler) {
            @Override
            protected boolean parseData(Object bytes, Bundle bundle) {
                return true;
            }
        }, null);

    }

    @Override
    public void cancelRoomByRoomId(long roomId, CallBack callBack) {

        //TODO 注销频道
    }
}
