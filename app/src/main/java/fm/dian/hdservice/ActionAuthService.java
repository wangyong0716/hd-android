package fm.dian.hdservice;

import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hddata_android.auth.AuthActionRequest.ActionType;
import fm.dian.hddata_android.auth.AuthActionRequest.UserAuthType;
import fm.dian.hdservice.util.Logger;

/**
 * Created by tinx on 4/16/15.
 */
public class ActionAuthService {

    private static final Logger log = Logger.getLogger(ActionAuthService.class);
    private static ActionAuthService actionAuthService;

    private ActionAuthService() {
    }

    public static ActionAuthService getInstance() {
        if (actionAuthService == null) {
            actionAuthService = new ActionAuthService();
        }
        return actionAuthService;
    }

    public boolean hasPermission(Long fromUserId, Long toUserId, Long roomId, ActionType type) {
        int result = AuthActionRequest.authAction(fromUserId, toUserId, roomId, type.ordinal());
        log.debug("permission check: fromUserId={},toUserId={},roomId={},type={},result={}", fromUserId, toUserId, roomId, type, result);
        return result == 1;
    }

    public boolean isRole(Long userId, Long roomId, Long liveId, UserAuthType type) {
        int result = AuthActionRequest.authUser(userId, roomId, liveId, type.ordinal());
        log.debug("role check : userId={},roomId={},liveId={},type={},result={}", userId, roomId, liveId, type, result);
        return result == 1;
    }
}
