package fm.dian.hdservice.api;

import fm.dian.hdservice.base.CallBack;

/**
 * Created by tinx on 4/24/15.
 */
public interface LoginServiceInterface {

    void signup();

    void loginByPhoneNumber(String phoneNumber, String password, CallBack callBack);

    void loginByAccountId(String accountId, CallBack callBack);
}
