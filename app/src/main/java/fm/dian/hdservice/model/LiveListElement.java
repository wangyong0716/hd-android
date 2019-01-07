package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.live.HDLiveListInfo.LiveListInfo;
import fm.dian.service.live.HDLiveListInfo.LiveListInfo.Builder;

/**
 * Created by tinx on 4/9/15.
 */
public class LiveListElement implements Serializable {
    private Builder builder;

    public LiveListElement() {
        builder = LiveListInfo.newBuilder();
    }

    public LiveListElement(LiveListInfo liveListInfo) {
        builder = liveListInfo.toBuilder();
    }


    public Live getLive() {
        return builder.hasLiveInfo() ? new Live(builder.getLiveInfo()) : null;
    }

    public Long getUserNumber() {
        return builder.hasUserNumber() ? builder.getUserNumber() : null;
    }

    public Boolean getIsLive() {
        return builder.hasIsLive() ? builder.getIsLive() : null;
    }

}
