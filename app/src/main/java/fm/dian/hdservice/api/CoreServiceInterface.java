package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.User;

/**
 * Created by tinx on 3/23/15.
 */
public interface CoreServiceInterface {
    /**
     * 获取用户信息
     *
     * @param userId
     * @param callBack
     * @return
     */
    public void fetchUserInfo(Long userId, CallBack callBack);


    /**
     * 更新用户信息
     *
     * @param user
     * @param callBack
     */
    public void updateUserInfo(User user, CallBack callBack);

    /**
     * 获取频道内所有管理员
     *
     * @param roomId
     * @param callBack
     */
    public void fetchAdminsByRoomId(long roomId, CallBack callBack);

    /**
     * 获取所有被拉黑人员
     *
     * @param roomId
     * @param callBack
     */
    public void fetchIgnoreByRoomId(long roomId, CallBack callBack);

    /**
     * 拉黑某人
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void addIgnore(long roomId, long userId, CallBack callBack);

    /**
     * 取消某人的拉黑
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void cancelIgnore(long roomId, long userId, CallBack callBack);

    /**
     * 添加管理员
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void addAdmin(long roomId, long userId, CallBack callBack);

    /**
     * 取消某人管理员权限
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void cancelAdmin(long roomId, long userId, CallBack callBack);

    /**
     * 获取频道的所有订阅者
     *
     * @param roomId
     * @param callBack
     */
    public void fetchFollowByRoomId(long roomId, CallBack callBack);

    /**
     * 添加订阅
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void addFollow(long roomId, long userId, CallBack callBack);

    /**
     * 取消订阅
     *
     * @param roomId
     * @param userId
     * @param callBack
     */
    public void cancelFollow(long roomId, long userId, CallBack callBack);

    /**
     * 获取频道信息
     *
     * @param roomId
     */
    public void fetchRoomByRoomId(long roomId, CallBack callBack);

    /**
     * 注销频道
     *
     * @param roomId
     */
    public void cancelRoomByRoomId(long roomId, CallBack callBack);

    /**
     * 更新频道信息
     *
     * @param room
     */
    public void updateRoom(Room room, CallBack callBack);

    /**
     * 创建频道
     *
     * @param room
     * @return
     */
    public void createRoom(Room room, CallBack callBack);

    /**
     * 搜索频道
     *
     * @param webaddr web分享地址搜索
     */
    public void searchRoom(String webaddr, CallBack callBack);

}
