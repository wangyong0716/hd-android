package fm.dian.hdservice.model;

/**
 * Created by tinx on 4/24/15.
 */
public class LoginUser extends User {
    private LoginType loginType;
    private String accountId;
    private String password;
    private String confirmCode;

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    public enum LoginType {
        qq,
        weixin,
        phone;
    }
}
