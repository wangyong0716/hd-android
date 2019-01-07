package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.group_chat.HDGroupChatKeyword.GroupChatKeyword;
import fm.dian.service.group_chat.HDGroupChatKeyword.GroupChatKeyword.Builder;

/**
 * Created by tinx on 4/24/15.
 */
public class KeyWord implements Serializable {
    private final Builder builder;

    public KeyWord() {
        this.builder = GroupChatKeyword.newBuilder();
    }

    public KeyWord(GroupChatKeyword groupChatKeyword) {
        this.builder = groupChatKeyword.toBuilder();
    }

    public Long getId() {
        return builder.hasId() ? builder.getId() : null;
    }

    public void setId(Long id) {
        this.builder.setId(id);
    }

    public Long getLiveId() {
        return builder.hasLiveId() ? builder.getLiveId() : null;
    }

    public void setLiveId(Long liveId) {
        this.builder.setLiveId(liveId);
    }

    public String getWord() {
        return builder.hasWord() ? builder.getWord() : null;
    }

    public void setWord(String word) {
        this.builder.setWord(word);
    }
}
