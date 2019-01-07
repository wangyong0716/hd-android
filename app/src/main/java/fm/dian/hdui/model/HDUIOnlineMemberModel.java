package fm.dian.hdui.model;

import fm.dian.hdservice.model.User;

/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2013-10-21 上午11:32:15
 */
public class HDUIOnlineMemberModel implements Comparable<Object> {
    private static final long serialVersionUID = 1L;
    public long userId;
    boolean isOwner = false;
    public java.lang.String avatar;
    public java.lang.String nickname;
    public java.lang.String phoneNumber;
    public java.lang.String signature;
    public java.lang.String location;
    public fm.dian.service.core.HDTableUser.HDUser.GenderType gender;
    private String uiType;
    private long unLoginNum = 0;

    public HDUIOnlineMemberModel(User u) {
        super();
        this.avatar = u.getAvatar();
        this.gender = u.getGender();
        this.location = u.getLocation();
        this.nickname = u.getNickname();
        this.phoneNumber = u.getPhoneNumber();
        this.signature = u.getSignature();
        this.userId = u.getUserId();
    }

    public HDUIOnlineMemberModel() {
        super();
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public long getUnLoginNum() {
        return unLoginNum;
    }

    public void setUnLoginNum(long unLoginNum) {
        this.unLoginNum = unLoginNum;
    }

    @Override
    public int compareTo(Object another) {
        HDUIOnlineMemberModel other = (HDUIOnlineMemberModel) another;
        if (getUiType().charAt(0) > other.getUiType().charAt(0)) {
            return 1;
        } else if (getUiType().charAt(0) < other.getUiType().charAt(0)) {
            return -1;
        }
        return 0;
    }
}
