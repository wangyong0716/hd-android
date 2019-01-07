package fm.dian.hdui.net.oldinterface;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fm.dian.hddata.HDData;
import fm.dian.hddata.business.model.HDDataTypeEnum.HDUserLoginType;
import fm.dian.hddata.business.model.HDLoginUser;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata.business.service.core.user.HDUserCache;
import fm.dian.hddata.cache.HDCache;
import fm.dian.hddata.util.HDContext;
import fm.dian.hddata.util.HDHandler;
import fm.dian.hddata.util.HDLog;
import fm.dian.hddata.util.HDThreadPool;
import fm.dian.hddata.util.RootUtil;
import fm.dian.hdservice.ConfigService;
import fm.dian.service.core.HDTableUser;

public final class Login {

    private HDLog log = new HDLog();
    private ConfigService configService = ConfigService.getInstance();

    public void exchangeNewToken(final boolean isSynchronized, final ExchangeNewTokenCallback callback) {
        if (isSynchronized) {
            HttpResult httpResult = exchangeNewToken();
            if (callback != null) {
                callback.onExchange(httpResult.isSuccess, httpResult.error);
            }
        } else {
            HDThreadPool.execute(new Runnable() {
                public void run() {
                    HttpResult httpResult = exchangeNewToken();

                    if (callback != null) {
                        new Login().onExchange(httpResult, callback);
                    }
                }
            });
        }
    }

    public void signupConfirm(final HDLoginUser loginUser, final SignupCallback callback) {
        if (!checkSignupLoginUser(loginUser)) {
            if (callback != null) {
                callback.onSignup(false, "ERROR.");
            } else {
                log.e("[ERROR] signup: callback is null.");
            }
            return;
        }

        if (loginUser.confirmationCode == null || loginUser.confirmationCode.length() == 0) {
            log.e("[ERROR] Login: confirmationCode is null.");
            if (callback != null) {
                callback.onSignup(false, "ERROR.");
            } else {
                log.e("[ERROR] signup: callback is null.");
            }
            return;
        }
        signup(true, loginUser, callback);
    }

    public void signup(final HDLoginUser loginUser, final SignupCallback callback) {
        signup(false, loginUser, callback);
    }

    public void loginByPhoneNumber(final HDLoginUser loginUser, final LoginCallback2 callback) {
        if (!checkSignupLoginUser(loginUser)) {
            if (callback != null) {
                callback.onLogin(false, -1, null);
            } else {
                log.e("[ERROR] loginByPhoneNumber: callback is null.");
            }
            return;
        }

        if (loginUser.phoneNumber.length() != 11) {
            log.e("[ERROR] Signup: 号码长度必须为11位.");
            callback.onLogin(false, 11, null);
            return;
        }

//		HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//		//没有配置，先读配置
//		if (config == null) {
//			new URLConfig().fetchURLConfig(new URLConfigCallback() {
//				public void onFetch(boolean isSuccess, HDURLConfig config) {
//					if (isSuccess && config != null) {
//						new Login().loginByPhoneNumber(loginUser, callback);
//					} else {
//						new Login().onLogin2(-1, null, callback);
//					}
//				}
//			});
//			return ;
//		}
        if (configService.getLoginNewLogin() == null) {
            return;
        }
        cacheLoginUser(loginUser);

        HDThreadPool.execute(new Runnable() {
            public void run() {
                try {
                    if (configService.getLoginNewLogin() == null || configService.getLoginNewLogin().length() == 0) {
                        log.e("loginByPhoneNumber: config is null.");
                        new Login().onLogin2(-1, null, callback);
                        return;
                    }

                    String url = configService.getLoginNewLogin();

                    log.i("starting loginByPhoneNumber loginUser: " + loginUser);
                    log.i("starting loginByPhoneNumber url: " + url);


                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", loginUser.phoneNumber));
                    nameValuePairs.add(new BasicNameValuePair("password", loginUser.password));

                    HttpEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    post.addHeader("platform", "ANDROID");
                    post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
                    post.addHeader("app-name", "hd");
                    post.setEntity(formEntity);

                    HttpResponse response = client.execute(post);

                    InputStream is = response.getEntity().getContent();
                    String result = inStream2String(is);
                    log.i("loginByPhoneNumber done [%d]: %s", response.getStatusLine().getStatusCode(), result);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        int errorCode = checkSignup(true, result, loginUser.phoneNumber);
                        String error = getErrorCodeString(errorCode);
                        if (error == null) {
                            HDUser user = checkLoginByPhoneNumber(result);

                            //缓存User
                            boolean isOK = new HDUserCache().cacheLoginUser(user);
                            if (isOK) {
                                new Login().onLogin2(errorCode, user, callback);
                            } else {
                                log.e("Cache Login User [ERROR].");
                                new Login().onLogin2(errorCode, null, callback);
                            }
                        } else {
                            new Login().onLogin2(errorCode, null, callback);
                        }
                    } else {
                        new Login().onLogin2(-1, null, callback);
                    }
                } catch (Throwable e) {
                    log.e("[ERROR] loginByPhoneNumber: " + e.getMessage(), e);
                    new Login().onLogin2(-1, null, callback);
                }
            }
        });
    }

    public void loginByAccountId(final HDLoginUser loginUser, final LoginCallback callback) {
        if (!checkLoginUser(loginUser)) {
            if (callback != null) {
                callback.onLogin(false, null);
            } else {
                log.e("[ERROR] loginByAccountId: callback is null.");
            }
            return;
        }

//		HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//		//没有配置，先读配置，再登录
//		if (config == null) {
//			new URLConfig().fetchURLConfig(new URLConfigCallback() {
//				public void onFetch(boolean isSuccess, HDURLConfig config) {
//					if (isSuccess && config != null) {
//						new Login().loginByAccountId(loginUser, callback);
//					} else {
//						new Login().onLogin(null, callback);
//					}
//				}
//			});
//			return ;
//		}
//        if(configService.getLoginLogin()==null){
//            configService.fetchURLConfig();
//            return ;
//        }
        cacheLoginUser(loginUser);

        HDThreadPool.execute(new Runnable() {
            public void run() {
                try {
//					HDURLConfig config = new URLConfig().getCacheURLConfig();

                    if (configService.getLoginLogin() == null || configService.getLoginLogin().length() == 0) {
                        log.i("loginByAccountId: config is null.");
                        new Login().onLogin(null, callback);
                        return;
                    }
                    String type = "qq";
                    if (loginUser.loginType == HDUserLoginType.weixin) {
                        type = "weixin";
                    }

                    String url = String.format(Locale.CHINA, "%s?type=%s", configService.getLoginLogin(), type);

                    log.i("starting loginByAccountId url: " + url);
                    log.i("starting loginByAccountId loginUser: " + loginUser.toString());

                    JSONObject json = new JSONObject();
                    json.put("account_id", loginUser.account_id);
                    json.put("nickname", loginUser.nickname);
                    json.put("avatar", loginUser.avatar);
                    json.put("gender", loginUser.genderStr());

                    HttpEntity formEntity = new StringEntity(json.toString(), "UTF-8");

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    post.addHeader("platform", "ANDROID");
                    post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
                    post.addHeader("app-name", "hd");
                    post.setEntity(formEntity);
                    HttpResponse response = client.execute(post);

                    log.i("loginByAccountId done.");
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        InputStream is = response.getEntity().getContent();
                        String result = inStream2String(is);
                        log.i(result);
                        HDUser user = checkLogin(result, loginUser.account_id);

                        //缓存User
                        boolean isOK = new HDUserCache().cacheLoginUser(user);
                        if (isOK) {
                            new Login().onLogin(user, callback);
                        } else {
                            log.e("Cache Login User [ERROR].");
                            new Login().onLogin(null, callback);
                        }
                    } else {
                        InputStream is = response.getEntity().getContent();
                        String result = inStream2String(is);
                        log.e("Response Code: " + response.getStatusLine().getStatusCode());
                        log.e(result);
                        new Login().onLogin(null, callback);
                    }
                } catch (Throwable e) {
                    log.e("[ERROR] loginByAccountId.", e);
                    new Login().onLogin(null, callback);
                }
            }

        });
    }

    public void resetPassword1(String phoneNumber, final ResetPasswordCallback callback) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            log.e("resetPassword1 [ERROR]: phoneNumber is null.");
            if (callback != null) {
                callback.onReset(false, "号码为空.");
            }
            return;
        }

        resetPassword(1, phoneNumber, null, null, callback);
    }

    public void resetPassword2(String phoneNumber, String confirmationCode, final ResetPasswordCallback callback) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            log.e("resetPassword2 [ERROR]: phoneNumber is null.");
            if (callback != null) {
                callback.onReset(false, "号码为空.");
            }
            return;
        }
        if (confirmationCode == null || confirmationCode.length() == 0) {
            log.e("resetPassword2 [ERROR]: confirmationCode is null.");
            if (callback != null) {
                callback.onReset(false, "验证码为空.");
            }
            return;
        }

        resetPassword(2, phoneNumber, confirmationCode, null, callback);
    }

    public void resetPassword3(String phoneNumber, String password, final ResetPasswordCallback callback) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            log.e("resetPassword3 [ERROR]: phoneNumber is null.");
            if (callback != null) {
                callback.onReset(false, "号码为空.");
            }
            return;
        }
        if (password == null || password.length() == 0) {
            log.e("resetPassword3 [ERROR]: password is null.");
            if (callback != null) {
                callback.onReset(false, "密码为空.");
            }
            return;
        }

        resetPassword(3, phoneNumber, null, password, callback);
    }

    private void resetPassword(final int index, final String phoneNumber, final String confirmationCode, final String password, final ResetPasswordCallback callback) {
        HDThreadPool.execute(new Runnable() {
            public void run() {
//				HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//				//没有配置，先读配置
//				if (config == null) {
//					URLConfig.HttpResult hr = new URLConfig().fetchURLConfig();
//					if (hr.config == null) {
//						onResetPassword(false, callback, "服务维护中，请稍候再试！");
//						return ;
//					}
//					config = hr.config;
//				}
//                if(configService.getLoginResetPassword()==null){
//                    configService.fetchURLConfig();
//                    return ;
//                }

                try {
                    if (configService.getLoginResetPassword() == null || configService.getLoginResetPassword().length() == 0) {
                        log.e("exchangeNewToken: ResetPassword is null.");
                        onResetPassword(false, callback, "服务维护中，请稍候再试！");
                        return;
                    }

                    String url = configService.getLoginResetPassword();

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", phoneNumber));

                    switch (index) {
                        case 2:
                            nameValuePairs.add(new BasicNameValuePair("confirmationCode", confirmationCode));
                            break;
                        case 3:
                            nameValuePairs.add(new BasicNameValuePair("password", password));
                            break;
                        default:
                            break;
                    }

                    HttpEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    post.addHeader("platform", "ANDROID");
                    post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
                    post.addHeader("app-name", "hd");
                    post.setEntity(formEntity);

                    HttpResponse response = client.execute(post);

                    InputStream is = response.getEntity().getContent();
                    String result = inStream2String(is);
                    log.i("exchangeNewToken done [%d]: %s", response.getStatusLine().getStatusCode(), result);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        int errorCode = checkSignup(true, result, phoneNumber);
                        String error = getErrorCodeString(errorCode);
                        boolean isSuccess = (error == null);
                        onResetPassword(isSuccess, callback, error);
                    } else {
                        onResetPassword(false, callback, "请求失败！");
                    }
                } catch (Throwable e) {
                    onResetPassword(false, callback, "请求失败！");
                    log.e("[ERROR] exchangeNewToken: " + e.getMessage(), e);
                }
            }
        });
    }

    private boolean checkLoginUser(final HDLoginUser loginUser) {
        if (loginUser == null) {
            log.e("[ERROR] Login: loginUser is null.");
            return false;
        }

        if (!(HDUserLoginType.qq == loginUser.loginType ||
                HDUserLoginType.weixin == loginUser.loginType ||
                HDUserLoginType.phone == loginUser.loginType)) {
            log.e("[ERROR] Login: UserLoginType is not QQ or Weixin or PhoneNumber.");
            return false;
        }

        if (loginUser.account_id == null || loginUser.account_id.length() == 0) {
            log.e("[ERROR] Login: account_id is null.");
            return false;
        }

        return true;
    }

    private String inStream2String(InputStream inputStream) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray());
    }

    private HDUser checkLoginByPhoneNumber(String json) {
        return checkLogin(json, null);
    }

    private HDUser checkLogin(String json, String accountId) {
        HDUser user = null;
        try {
            JSONObject jo = new JSONObject(json);

            if (jo.has("userAccessToken")) {
                String userAccessToken = (String) jo.get("userAccessToken");
                if (userAccessToken != null && userAccessToken.length() > 0 && accountId != null && accountId.length() > 0) {
                    HDToken.getInstance().setTokenByAccountId(accountId, userAccessToken);
                }
            }
            if (jo.has("user")) {
                jo = jo.getJSONObject("user");
            }

            user = new HDUser();
            user.userId = (Integer) jo.get("id");

            if (jo.has("avatar") && jo.isNull("avatar")) {
                Object avatar = jo.get("avatar");
                user.avatar = avatar.toString();
            }

            if (jo.has("nickname") && jo.isNull("nickname")) {
                Object nickname = jo.get("nickname");
                user.nickname = nickname.toString();
            }

            if (jo.has("phone_number") && !jo.isNull("phone_number")) {
                Object phone_number = jo.get("phone_number");
                user.phoneNumber = phone_number.toString();
            }

            String gender = (String) jo.get("gender");
            if ("1".equals(gender) || "MALE".equalsIgnoreCase(gender)) {
                user.gender = HDTableUser.HDUser.GenderType.MALE;
            } else if ("2".equals(gender) || "FEMALE".equalsIgnoreCase(gender)) {
                user.gender = HDTableUser.HDUser.GenderType.FEMALE;
            } else {
                user.gender = HDTableUser.HDUser.GenderType.UNKNOWN;
            }
        } catch (Throwable e) {
            log.e("[ERROR] login checkLogin.", e);
            user = null;
        }
        return user;
    }


    private boolean checkSignupLoginUser(final HDLoginUser loginUser) {
        if (loginUser == null) {
            log.e("[ERROR] Signup: loginUser is null.");
            return false;
        }

        if (loginUser.loginType != HDUserLoginType.phone) {
            log.e("[ERROR] Signup: LoginType is not PhoneNumber.");
            return false;
        }

        if (loginUser.phoneNumber == null || loginUser.phoneNumber.length() == 0) {
            log.e("[ERROR] Login: phoneNumber is null.");
            return false;
        }

        if (loginUser.password == null || loginUser.password.length() == 0) {
            log.e("[ERROR] Login: password is null.");
            return false;
        }

        return true;
    }

    private HttpResult exchangeNewToken() {
        HDToken.TokenValue tokenValue = HDToken.getInstance().getToken();
        if (tokenValue == null) {
            return new HttpResult();
        }
        String token = tokenValue.getToken();

        if (token == null || token.length() == 0) {
            log.e("exchangeNewToken: token is null.");
            return new HttpResult();
        }

        if (tokenValue.isTokenByPhoneNumber()) {
            return exchangeNewTokenByPhoneNumber();
        } else if (tokenValue.isTokenByAccountId()) {
            return exchangeNewTokenByAccountId();
        } else {
            return new HttpResult();
        }

    }

    private HttpResult exchangeNewTokenByAccountId() {
        HttpResult httpResult = new HttpResult();

        try {
            HDLoginUser loginUser = getCacheLoginUser();

            if (loginUser == null) {
                log.e("exchangeNewTokenByAccountId: loginUser is null.");
                return httpResult;
            }

//			HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//			if (config == null || config.login.Login == null || config.login.Login.length() == 0) {
//				log.e("exchangeNewTokenByAccountId: config is null.");
//				return httpResult;
//			}
//            if(configService.getLoginLogin()==null || configService.getLoginLogin().length() == 0){
//                configService.fetchURLConfig();
//                return httpResult;
//            }
            String type = "qq";
            if (loginUser.loginType == HDUserLoginType.weixin) {
                type = "weixin";
            }

            String url = String.format(Locale.CHINA, "%s?type=%s", configService.getLoginLogin(), type);

            log.i("starting exchangeNewTokenByAccountId url: " + url);
            log.i("starting exchangeNewTokenByAccountId loginUser: " + loginUser.toString());

            JSONObject json = new JSONObject();
            json.put("account_id", loginUser.account_id);
            json.put("nickname", loginUser.nickname);
            json.put("avatar", loginUser.avatar);
            json.put("gender", loginUser.genderStr());

            HttpEntity formEntity = new StringEntity(json.toString(), "UTF-8");

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.addHeader("platform", "ANDROID");
            post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
            post.addHeader("app-name", "hd");
            post.setEntity(formEntity);
            HttpResponse response = client.execute(post);

            log.i("exchangeNewTokenByAccountId done.");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                String result = inStream2String(is);
                log.i(result);
                HDUser user = checkLogin(result, loginUser.account_id);

                //缓存User
                boolean isOK = new HDUserCache().cacheLoginUser(user);
                if (isOK) {
                    httpResult.isSuccess = true;
                } else {
                    log.e("Cache Login User [ERROR].");
                    httpResult.error = "Cache Login User [ERROR].";
                }
            } else {
                httpResult.error = "请求失败！";
            }
        } catch (Throwable e) {
            log.e("[ERROR] exchangeNewTokenByAccountId.", e);
        }

        return httpResult;
    }

    private HttpResult exchangeNewTokenByPhoneNumber() {
        HttpResult httpResult = new HttpResult();

//		HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//		//没有配置，先读配置
//		if (config == null) {
//			URLConfig.HttpResult hr = new URLConfig().fetchURLConfig();
//			if (hr.config == null) {
//				return httpResult;
//			}
//			config = hr.config;
//		}
//        if(configService.getLoginNewLogin()==null){
//            configService.fetchURLConfig();
//            return httpResult;
//        }

        try {
            if (configService.getLoginNewLogin() == null || configService.getLoginNewLogin().length() == 0) {
                log.e("exchangeNewTokenByPhoneNumber: config is null.");
                return httpResult;
            }

            String token = null;

            HDToken.TokenValue tokenValue = HDToken.getInstance().getToken();
            if (tokenValue != null) {
                if (!tokenValue.isTokenByPhoneNumber()) {
                    log.e("exchangeNewTokenByPhoneNumber: token is not By PhoneNumber.");
                    return httpResult;
                }
                token = tokenValue.getToken();
            }

            if (token == null || token.length() == 0) {
                log.e("exchangeNewTokenByPhoneNumber: token is null.");
                return httpResult;
            }

            String url = configService.getLoginNewLogin();

            HDUser user = new HDData().getLoginUser();

            log.i("starting exchangeNewTokenByPhoneNumber url: " + url);
            log.i("starting exchangeNewTokenByPhoneNumber token: " + token);
            log.i("starting exchangeNewTokenByPhoneNumber phoneNumber: " + user.phoneNumber);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("phoneNumber", user.phoneNumber));

            HttpEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.addHeader("platform", "ANDROID");
            post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
            post.addHeader("app-name", "hd");
            post.addHeader("user-token", token);
            post.setEntity(formEntity);

            HttpResponse response = client.execute(post);

            InputStream is = response.getEntity().getContent();
            String result = inStream2String(is);
            log.i("exchangeNewTokenByPhoneNumber done [%d]: %s", response.getStatusLine().getStatusCode(), result);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                int errorCode = checkSignup(true, result, user.phoneNumber);
                String error = getErrorCodeString(errorCode);

                httpResult.isSuccess = (error == null);
                httpResult.error = error;
            }
        } catch (Throwable e) {
            log.e("[ERROR] exchangeNewTokenByPhoneNumber: " + e.getMessage(), e);
        }

        return httpResult;
    }

    private void signup(final boolean isConfirm, final HDLoginUser loginUser, final SignupCallback callback) {
        if (!checkSignupLoginUser(loginUser)) {
            if (callback != null) {
                callback.onSignup(false, "ERROR.");
            } else {
                log.e("[ERROR] signup: callback is null.");
            }
            return;
        }

//		HDURLConfig config = new URLConfig().getCacheURLConfig();
//
//		//没有配置，先读配置
//		if (config == null) {
//			new URLConfig().fetchURLConfig(new URLConfigCallback() {
//				public void onFetch(boolean isSuccess, HDURLConfig config) {
//					if (isSuccess && config != null) {
//						new Login().signup(isConfirm, loginUser, callback);
//					} else {
//						new Login().onSignup(false, callback, "服务维护中，请稍候再试！");
//					}
//				}
//			});
//			return ;
//		}
//        if(configService.getLoginSignup()==null){
//            configService.fetchURLConfig();
//            return ;
//        }

        HDThreadPool.execute(new Runnable() {
            public void run() {
                try {
//					HDURLConfig config = new URLConfig().getCacheURLConfig();

                    if (configService.getLoginSignup() == null || configService.getLoginSignup().length() == 0) {
                        log.e("Signup: config is null.");
                        new Login().onSignup(false, callback, "服务维护中，请稍候再试！");
                        return;
                    }

                    String url = configService.getLoginSignup();

                    log.i("starting Signup loginUser: " + loginUser);
                    log.i("starting Signup url: " + url);


                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("phoneNumber", loginUser.phoneNumber));
                    nameValuePairs.add(new BasicNameValuePair("password", loginUser.password));
                    if (isConfirm) {
                        nameValuePairs.add(new BasicNameValuePair("confirmationCode", loginUser.confirmationCode));
                    }

                    HttpEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url);
                    post.addHeader("platform", "ANDROID");
                    post.addHeader("device-id", new RootUtil().getDeviceUDID(HDContext.getInstance().getContext()));
                    post.addHeader("app-name", "hd");
                    post.setEntity(formEntity);

                    HttpResponse response = client.execute(post);

                    InputStream is = response.getEntity().getContent();
                    String result = inStream2String(is);
                    log.i("Signup done [%d]: %s", response.getStatusLine().getStatusCode(), result);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        int errorCode = checkSignup(isConfirm, result, loginUser.phoneNumber);
                        String error = getErrorCodeString(errorCode);
                        new Login().onSignup(error == null, callback, error);
                    } else {
                        new Login().onSignup(false, callback, "注册失败，请稍候再试！");
                    }
                } catch (Throwable e) {
                    log.e("[ERROR] Signup: " + e.getMessage(), e);
                    new Login().onSignup(false, callback, "注册失败，请稍候再试！");
                }
            }
        });
    }

    public static String getErrorCodeString(int errorCode) {
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


    private int checkSignup(boolean isConfirm, String json, String phoneNumber) {
        int errorCode = -1;
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.has("errorCode")) {
                errorCode = (Integer) jo.get("errorCode");
            }
            if (isConfirm) {
                if (jo.has("userAccessToken")) {
                    String userAccessToken = (String) jo.get("userAccessToken");
                    if (userAccessToken != null && userAccessToken.length() > 0) {
                        HDToken.getInstance().setTokenByPhoneNumber(phoneNumber, userAccessToken);
                    }
                }
            }
        } catch (Throwable e) {
            log.e("[ERROR] checkSignup: " + e.getMessage(), e);
        }
        return errorCode;
    }

    private void onExchange(final HttpResult httpResult, final ExchangeNewTokenCallback callback) {
        if (callback != null) {
            HDHandler.handlerOnUIThread(new Runnable() {
                public void run() {
                    callback.onExchange(httpResult.isSuccess, httpResult.error);
                }
            });
        }
    }

    private void onResetPassword(final boolean isSuccess, final ResetPasswordCallback callback, final String error) {
        if (callback != null) {
            HDHandler.handlerOnUIThread(new Runnable() {
                public void run() {
                    callback.onReset(isSuccess, error);
                }
            });
        }
    }

    private void onSignup(final boolean isSuccess, final SignupCallback callback, final String error) {
        if (callback != null) {
            HDHandler.handlerOnUIThread(new Runnable() {
                public void run() {
                    callback.onSignup(isSuccess, error);
                }
            });
        }
    }

    private void onLogin(final HDUser user, final LoginCallback callback) {
        if (callback != null) {
            HDHandler.handlerOnUIThread(new Runnable() {
                public void run() {
                    callback.onLogin(user != null, user);
                }
            });
        }
    }

    private void onLogin2(final int errorCode, final HDUser user, final LoginCallback2 callback) {
        if (callback != null) {
            HDHandler.handlerOnUIThread(new Runnable() {
                public void run() {
                    callback.onLogin(user != null, errorCode, user);
                }
            });
        }
    }

    private static final String CACHE_LOGINUSER_KEY = "CACHE_LOGINUSER_KEY";

    private boolean cacheLoginUser(HDLoginUser loginUser) {
        log.e("cacheLoginUser loginUser: " + loginUser);
        if (loginUser != null) {
            try {
                HDCache cache = new HDCache();
                String json = new Gson().toJson(loginUser);
                cache.setString(json, CACHE_LOGINUSER_KEY);
                return true;
            } catch (Throwable e) {
                log.e("cacheLoginUser [ERROR]: " + e.getMessage(), e);
            }
        }
        return false;
    }

    private HDLoginUser getCacheLoginUser() {
        HDLoginUser loginUser = null;
        try {
            HDCache cache = new HDCache();
            String json = cache.getString(CACHE_LOGINUSER_KEY);
            loginUser = new Gson().fromJson(json, new TypeToken<HDLoginUser>() {
            }.getType());
        } catch (Throwable e) {
            log.e("getCacheLoginUser [ERROR]: " + e.getMessage(), e);
        }
        log.e("getCacheLoginUser loginUser: " + loginUser);
        return loginUser;
    }

    public interface SignupCallback {
        void onSignup(boolean isSuccess, String error);
    }

    public interface ExchangeNewTokenCallback {
        void onExchange(boolean isSuccess, String error);
    }

    public interface ResetPasswordCallback {
        void onReset(boolean isSuccess, String error);
    }

    public interface LoginCallback {
        void onLogin(boolean isSuccess, HDUser user);
    }

    public interface LoginCallback2 {
        void onLogin(boolean isSuccess, int errorCode, HDUser user);
    }

    private final class HttpResult {
        boolean isSuccess;
        String error;
    }
}
