package fm.dian.hdservice.model;

import com.google.protobuf.ByteString;

import java.io.Serializable;

import fm.dian.service.group_chat.HDGroupChatClientMessage;
import fm.dian.service.group_chat.HDGroupChatMessage.GroupChatMessage;
import fm.dian.service.group_chat.HDGroupChatMessage.GroupChatMessage.Builder;
import fm.dian.service.group_chat.HDGroupChatMessage.GroupChatMessageText;
import fm.dian.service.group_chat.HDGroupChatMessage.GroupChatMessageType;

/**
 * Created by tinx on 4/9/15.
 */
public class GroupChat implements Serializable,Comparable<GroupChat>{

    private final Builder builder;

    public GroupChat() {
        this.builder = GroupChatMessage.newBuilder();
    }

    public GroupChat(GroupChatMessage groupChatMessage) {
        this.builder = groupChatMessage.toBuilder();
    }

    public GroupChatMessage getGroupChatMessage() {
        return builder.build();
    }

    public GroupChatMessageType getGroupChatMessageType() {
        return builder.hasGroupChatMessageType() ? builder.getGroupChatMessageType() : null;
    }

    public void setGroupChatMessageType(GroupChatMessageType groupChatMessageType) {
        this.builder.setGroupChatMessageType(getGroupChatMessageType());
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

    public Long getChatId() {
        return builder.hasGroupChatId() ? builder.getGroupChatId() : null;
    }

    public void setChatId(Long chatId) {
        this.builder.setGroupChatId(chatId);
    }

    public Long getTimestamp() {
        return builder.hasTimestamp() ? builder.getTimestamp() : null;
    }

    public void setTimestamp(Long timestamp) {
        this.builder.setTimestamp(timestamp);
    }

    public Long getLiveId() {
        return builder.hasLiveId() ? builder.getLiveId() : null;
    }

    public void setLiveId(Long liveId) {
        this.builder.setLiveId(liveId);
    }

    public Long getClientId() {
        return builder.hasExtension(HDGroupChatClientMessage.GroupChatClientMessage.groupChatClientMessage)
                ? builder.getExtension(HDGroupChatClientMessage.GroupChatClientMessage.groupChatClientMessage).getClientId()
                : null;
    }

    public void setText(String text) {
        //TODO 根据不通的类型构造不同的pb，目前只有Text
        GroupChatMessageText groupChatMessageText = GroupChatMessageText.newBuilder()
                .setData(ByteString.copyFrom(text.getBytes()))
                .build();
        builder.setExtension(GroupChatMessageText.groupChatMessageText, groupChatMessageText);
    }

    public String getText() {
        return builder.hasExtension(GroupChatMessageText.groupChatMessageText)
                ? builder.getExtension(GroupChatMessageText.groupChatMessageText).getData().toStringUtf8()
                : null;
    }

    @Override
    public boolean equals(Object o) {
        GroupChat chat = (GroupChat) o;
        if (this.getChatId().equals(chat.getChatId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return builder.hasGroupChatId() ? Long.valueOf(builder.getGroupChatId()).hashCode() : 0;
    }


    @Override
    public int compareTo(GroupChat another) {
        return this.getChatId().compareTo(another.getChatId());
    }
}
