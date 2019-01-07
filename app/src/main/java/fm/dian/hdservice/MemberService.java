package fm.dian.hdservice;

import android.os.Bundle;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.RoomUserAdmin;
import fm.dian.hdservice.model.RoomUserFollow;

/**
 * Created by tinx on 4/14/15.
 */
public class MemberService extends BaseService {

    private static MemberService memberService;

    private CoreService coreService = CoreService.getInstance();

    private MemberService(int serviceType) {
        super(serviceType);
    }

    public static MemberService getInstance() {
        if (memberService == null) {
            memberService = new MemberService(0);
        }
        return memberService;
    }


    public void getMemberInOrder(final Long roomId, final CallBack callBack) {
        coreService.fetchAdminsByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int error_code = data.getInt("error_code");
                if (error_code == OK) {
                    ArrayList<RoomUserAdmin> roomUserAdminList = (ArrayList<RoomUserAdmin>) data.getSerializable("room_user_admin_list");
                    final Set<Long> adminSet = new HashSet<Long>();
                    final Set<Long> owners = new HashSet<Long>();
                    for (RoomUserAdmin roomUserAdmin : roomUserAdminList) {
                        if (roomUserAdmin.isOwner()) {
                            owners.add(roomUserAdmin.getUserId());
                        } else {
                            adminSet.add(roomUserAdmin.getUserId());
                        }
                    }
                    coreService.fetchFollowByRoomId(roomId, new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            Message message = Message.obtain(handler, RESPONSE, callBack);
                            Bundle bundle = new Bundle();
                            int error_code_follow = data.getInt("error_code");
                            bundle.putInt("error_code", error_code_follow);
                            if (error_code_follow == OK) {
                                ArrayList<RoomUserFollow> roomUserFollowList = (ArrayList<RoomUserFollow>) data.getSerializable("room_user_follow_list");
                                Set<Long> followSet = new HashSet<Long>();
                                for (RoomUserFollow roomUserFollow : roomUserFollowList) {
                                    followSet.add(roomUserFollow.getUserId());
                                }
                                followSet.removeAll(adminSet);
                                followSet.removeAll(owners);
                                Long owner = owners.iterator().next();
                                bundle.putLong("owner", owner);
                                bundle.putSerializable("admin_set", (java.io.Serializable) adminSet);
                                bundle.putSerializable("follow_set", (java.io.Serializable) followSet);
                                message.setData(bundle);
                                message.sendToTarget();
                            } else {
                                message.setData(bundle);
                                message.sendToTarget();
                            }
                        }
                    });
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("error_code", error_code);
                    final Message message = Message.obtain(handler, RESPONSE, callBack);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        });

    }
}
