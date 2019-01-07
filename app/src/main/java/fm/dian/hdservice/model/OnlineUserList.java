package fm.dian.hdservice.model;

import java.io.Serializable;
import java.util.List;

import fm.dian.service.online.HDOnlineResponseOnlineUserList.ResponseOnlineUserList;
import fm.dian.service.online.HDOnlineResponseOnlineUserList.ResponseOnlineUserList.Builder;

/**
 * Created by tinx on 4/10/15.
 */
public class OnlineUserList implements Serializable {
    private Builder builder;

    public OnlineUserList(ResponseOnlineUserList responseOnlineUserList) {
        this.builder = responseOnlineUserList.toBuilder();
    }


    public Long getAnonymousUserNumber() {
        return builder.hasWebAnonymousUserNumber() ? builder.getWebAnonymousUserNumber() : null;
    }

    public List<Long> getOwnerUserIdList() {
        return builder.getOwnerUserIdList();
    }

    public List<Long> getAdminUserIdList() {
        return builder.getAdminUserIdList();
    }

    public List<Long> getNormalUserIdList() {
        return builder.getNormalUserIdList();
    }
}
