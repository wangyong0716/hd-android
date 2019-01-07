package fm.dian.hdservice;

import android.os.Bundle;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fm.dian.hdservice.api.LoginServiceInterface;
import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.util.HttpClient;
import fm.dian.hdservice.util.HttpClient.RequestMethod;

/**
 * Created by tinx on 4/24/15.
 */
public class LoginService extends BaseService implements LoginServiceInterface {

    private static LoginService loginService;
    private ConfigService configService;

    private LoginService(int serviceType) {
        super(serviceType);
        configService = ConfigService.getInstance();
    }

    public static LoginService getInstance() {
        if (loginService != null) {
            loginService = new LoginService(0);
        }
        return loginService;
    }


    @Override
    public void signup() {

    }

    @Override
    public void loginByPhoneNumber(String phoneNumber, String password, CallBack callBack) {
        HttpClient httpClient = new HttpClient(configService.getLoginNewLogin(), RequestMethod.POST);
        httpClient.addParam("phoneNumber", phoneNumber);
        httpClient.addParam("password", password);
        httpClient.addHeader("platform", "ANDROID");
        //TODO device-id
        httpClient.addHeader("device-id", "xxxxxxxxx");
        httpClient.addHeader("app-name", "hd");
        try {
            Message message = Message.obtain(handler, RESPONSE, callBack);
            Bundle bundle = new Bundle();
            JSONObject json = httpClient.requestJson();
            int errorCode = json.getInt("errorCode");
            bundle.putInt("error_code", errorCode);
            if (errorCode == 0) {
                if (json.has("user")) {
                    JSONObject userJson = json.getJSONObject("user");
                    User user = new User();
                    user.setUserId(userJson.getLong("id"));
                    //TODO 填充User对象
                    bundle.putSerializable("user", user);
                }
            } else {
                String errorCodeString = getErrorCodeString(errorCode);
                bundle.putString("error_string", errorCodeString);
            }
            message.setData(bundle);
            message.sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginByAccountId(String accountId, CallBack callBack) {

    }

    private String getErrorCodeString(int errorCode) {
        String error = "ERROR.";
        switch (errorCode) {
            case 0: // ok
                error = null;
                break;
            case 1001: // not_exists
                error = "号码不存在！";
                break;
            case 1002: // already_exists
                error = "号码已注册！";
                break;
            case 1003: // confirm_count_limit
                error = "该号码已超过今天的有效验证次数！";
                break;
            case 1004: // confirmation_code_expired
                error = "验证码已过期！";
                break;
            case 1005: // confirmation_code_invalid
                error = "验证码不正确！";
                break;
            case 1006: // password_format_invalid
                error = "密码格式不正确！";
                break;
            case 1007: // userid_password_invalid
                error = "密码不正确！";
                break;
            case 1008: // token_invalid
                error = "请求失败！";
                break;
            case 1009: // token_expired
                error = "请求失败！";
                break;
            case 1010: // phone_number_format_invalid
                error = "号码不正确！";
                break;
            case 11: // phone_number_format_invalid
                error = "号码长度必须为11位！";
                break;
            case -1: // phone_number_format_invalid
                error = "发生错误！";
                break;

            default:
                break;
        }
        return error;
    }

}
