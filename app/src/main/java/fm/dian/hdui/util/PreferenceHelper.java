package fm.dian.hdui.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.tauth.Tencent;

import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.model.HDUIUserInfo;
import fm.dian.hdui.sdk.qq.QQAccessToken;

/**
 * Created by mac on 14-1-22.
 */
public class PreferenceHelper {
    private static SharedPreferences spAppSettings = HDApp.getInstance().getSharedPreferences(HongdianConstants.APP_NAME, 0);
    private static SharedPreferences.Editor localEditor = spAppSettings.edit();

    /**
     * 0 unlogin, 1 weixin, 2 qq
     * *
     */
    public static void setLogin(int type) {
        localEditor.putInt("isLogin", type);
        localEditor.commit();
    }

    public static int getLogin() {
        return spAppSettings.getInt("isLogin", 0);
    }

    /**
     * userInfo
     * *
     */
    private static final String USER_PREFERENCES_NAME = "user_info";

    public static void writeUserInfo(Context context, HDUIUserInfo userInfo) {
        if (null == context || null == userInfo) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HDUIUserInfo.NAME, userInfo.getName());
        editor.putString(HDUIUserInfo.GENDER, userInfo.getGender());
        editor.putString(HDUIUserInfo.AVATAR_URL, userInfo.getAvatarUrl());
        editor.putString(HDUIUserInfo.LOCATION, userInfo.getLocation());
        editor.putString(HDUIUserInfo.DESCRIPTION, userInfo.getDescription());
        editor.putString(HDUIUserInfo.SNS_ID, userInfo.getSnsId());
        editor.putString(HDUIUserInfo.SNS_DOMAIN, userInfo.getSnsDomain());
        editor.commit();
    }

    public static HDUIUserInfo readUserInfo(Context context) {
        if (null == context) {
            return null;
        }
        HDUIUserInfo userInfo = new HDUIUserInfo();
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        userInfo.setName(pref.getString(HDUIUserInfo.NAME, ""));
        userInfo.setGender(pref.getString(HDUIUserInfo.GENDER, ""));
        userInfo.setAvatarUrl(pref.getString(HDUIUserInfo.AVATAR_URL, ""));
        userInfo.setLocation(pref.getString(HDUIUserInfo.LOCATION, ""));
        userInfo.setDescription(pref.getString(HDUIUserInfo.DESCRIPTION, ""));
        userInfo.setSnsId(pref.getString(HDUIUserInfo.SNS_ID, ""));
        userInfo.setSnsDomain(pref.getString(HDUIUserInfo.SNS_DOMAIN, ""));
        return userInfo;
    }

    public static void setUserId(Context context, long id) {
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("user_id", id);
        editor.commit();
    }

    public static long getUserId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        return pref.getLong("user_id", -1);
    }

    public static void setLastRoom(Context context, long roomId) {
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("last_room_id", roomId);
        editor.commit();
    }

    public static long getLastRoom(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        return pref.getLong("last_room_id", -1);
    }

    public static void clearUserInfo(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * qq
     * *
     */
    private static final String QQ_PREFERENCES_NAME = "sns_qq";

    public static void writeQQAccessToken(Context context, Tencent tencent) {
        if (null == context || null == tencent) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(QQ_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HongdianConstants.QQ_OPENID, tencent.getOpenId());
        editor.putString(HongdianConstants.QQ_ACCESS_TOKEN, tencent.getAccessToken());
        editor.putLong(HongdianConstants.QQ_EXPIRES_IN, tencent.getExpiresIn());
        editor.commit();
    }

    public static QQAccessToken readQQAccessToken(Context context) {
        if (null == context) {
            return null;
        }

        QQAccessToken token = new QQAccessToken();
        SharedPreferences pref = context.getSharedPreferences(QQ_PREFERENCES_NAME, Context.MODE_APPEND);
        token.setOpenId(pref.getString(HongdianConstants.QQ_OPENID, ""));
        token.setAccessToken(pref.getString(HongdianConstants.QQ_ACCESS_TOKEN, ""));
        token.setExpiresIn(pref.getLong(HongdianConstants.QQ_EXPIRES_IN, 0));
        return token;
    }

    public static void clearQQAccessToken(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(QQ_PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
