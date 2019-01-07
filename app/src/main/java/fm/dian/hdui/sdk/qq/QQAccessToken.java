package fm.dian.hdui.sdk.qq;

/**
 * Created by mac on 14-4-2.
 */
public class QQAccessToken {
    private String openId;
    private String accessToken;
    private Long expiresIn;

    public QQAccessToken(String openId, String accessToken, Long expiresIn) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public QQAccessToken() {

    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
