package fm.dian.hdui.net.oldinterface;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Locale;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata.cache.HDCache;
import fm.dian.hddata.util.HDLog;

public final class HDUserCache {

    private HDLog log = new HDLog(HDUserCache.class);

    private HDCache cache = new HDCache();

    private String cacheKey(long userId) {
        return String.format(Locale.CHINA, "KEY_HDUser_%d", userId);
    }

    private String cacheLoginKey() {
        return String.format(Locale.CHINA, "KEY_HDUser_Login");
    }

    public boolean cacheUser(HDUser user) {
        if (user == null || user.userId < 1) {
            log.e(" cacheUser [ERROR]: user is null or userId < 1.");
            return false;
        }
        try {
            String json = new Gson().toJson(user);
            cache.setString(json, cacheKey(user.userId));
            return true;
        } catch (Throwable e) {
            log.e(" cacheUser [ERROR]: ", e);
        }
        return false;
    }

    public boolean cacheLoginUser(HDUser user) {
        if (user == null || user.userId < 1) {
            log.e(" cacheLoginUser [ERROR]: user is null or user_id < 1.");
            return false;
        }
        try {
            String json = new Gson().toJson(user);
            cache.setString(json, cacheKey(user.userId));
            cache.setString(String.valueOf(user.userId), cacheLoginKey());
            return true;
        } catch (Throwable e) {
            log.e(" cacheLoginUser [ERROR]: ", e);
        }
        return false;
    }

    public HDUser getUser(long userId) {
        if (userId < 1) {
            log.e(" getUser [ERROR]: userId < 1.");
            return null;
        }
        try {
            String json = cache.getString(cacheKey(userId));
            HDUser user = new Gson().fromJson(json, new TypeToken<HDUser>() {
            }.getType());
            return user;
        } catch (Throwable e) {
            log.e(" getUser [ERROR]: " + userId, e);
        }
        return null;
    }

    public HDUser getLoginUser() {
        try {
            HDCache cache = new HDCache();
            String uid = cache.getString(cacheLoginKey());
            if (uid == null) {
                log.e(" getLoginUser [ERROR]: not login user.");
                return null;
            }
            long userId = Long.parseLong(uid);
            if (userId < 1) {
                log.e(" getLoginUser [ERROR]: userId < 1.");
                return null;
            }
            String json = cache.getString(cacheKey(userId));
            HDUser user = new Gson().fromJson(json, new TypeToken<HDUser>() {
            }.getType());
            return user;
        } catch (Throwable e) {
            log.e(" getLoginUser [ERROR]: ", e);
        }
        return null;
    }

    public boolean removeUser(long userId) {
        if (userId < 1) {
            log.e(" removeUser [ERROR]: userId < 1.");
            return false;
        }
        try {
            new HDCache().remove(cacheKey(userId));
            return true;
        } catch (Throwable e) {
            log.e(" removeUser [ERROR]: ", e);
        }
        return false;
    }

    public boolean removeLoginUser() {
        try {
            new HDCache().remove(cacheLoginKey());
            return true;
        } catch (Throwable e) {
            log.e(" removeLoginUser [ERROR]: ", e);
        }
        return false;
    }

}
