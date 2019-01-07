package fm.dian.hdui.model;

/**
 * Created by mac on 14-4-2.
 */
public class HDUIUserInfo {
    public final static String NAME = "name";
    public final static String GENDER = "gender";
    public final static String AVATAR_URL = "avatar_url";
    public final static String LOCATION = "location";
    public final static String DESCRIPTION = "description";
    public final static String SNS_ID = "sns_id";
    public final static String SNS_DOMAIN = "sns_domain";

    private String name;
    private String gender;
    private String avatarUrl;
    private String location;
    private String description;
    private String snsId;
    private String snsDomain;

    public HDUIUserInfo() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsDomain(String snsDomain) {
        this.snsDomain = snsDomain;
    }

    public String getSnsDomain() {
        return snsDomain;
    }


}

