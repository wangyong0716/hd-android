package fm.dian.hddata_android.auth;

/**
 * Created by tinx on 4/16/15.
 */
public class AuthActionRequest {

    public static enum ActionType {
        ActionAddAdmin,
        ActionRemoveAdmin,
        ActionAddIgnore,
        ActionRemoveIgnore,
        ActionInviteSpeak,
        ActionKickSpeak,
    }

    public static enum ActionPermit {
        PermitNone,
        PermitAllow,
        PermitDeny,
    }

    public static enum UserAuthType {
        UserOwner,
        UserAdmin,
        UserUser,
        UserIgnore,
        UserSlience,
        UserFreeSpeak,
    }

    public static native int authAction(long from_user_id,
                                        long to_user_id,
                                        long room_id,
                                        int action_type);


    public static native int authUser(long user_id,
                                      long room_id,
                                      long live_id,
                                      int user_auth_type);
}
