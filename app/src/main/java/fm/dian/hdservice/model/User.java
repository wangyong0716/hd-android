package fm.dian.hdservice.model;

import java.io.Serializable;

import fm.dian.service.core.HDTableUser.HDUser;
import fm.dian.service.core.HDTableUser.HDUser.Builder;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;

/**
 * Created by tinx on 4/7/15.
 */
public class User implements Serializable {
    private final Builder builder;

    public User() {
        builder = HDUser.newBuilder();
    }

    public User(HDUser hdUser) {
        this.builder = hdUser.toBuilder();
    }

    public HDUser getHdUser() {
        return builder.build();
    }

    public Long getUserId() {
        return builder.hasId() ? builder.getId() : null;
    }

    public void setUserId(Long userId) {
        builder.setId(userId);
    }

    public String getAvatar() {
        return builder.hasAvatar() ? builder.getAvatar() : null;
    }

    public void setAvatar(String avatar) {
        builder.setAvatar(avatar);
    }

    public String getNickname() {
        return builder.hasNickname() ? builder.getNickname() : null;
    }

    public void setNickname(String nickname) {
        builder.setNickname(nickname);
    }

    public String getPhoneNumber() {
        return builder.hasPhoneNumber() ? builder.getPhoneNumber() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        builder.setPhoneNumber(phoneNumber);
    }

    public String getSignature() {
        return builder.hasSignature() ? builder.getSignature() : null;
    }

    public void setSignature(String signature) {
        builder.setSignature(signature);
    }

    public String getLocation() {
        return builder.hasLocation() ? builder.getLocation() : null;
    }

    public void setLocation(String location) {
        builder.setLocation(location);
    }

    public GenderType getGender() {
        return builder.hasGender() ? builder.getGender() : GenderType.UNKNOWN;
    }

    public void setGender(GenderType gender) {
        builder.setGender(gender);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
