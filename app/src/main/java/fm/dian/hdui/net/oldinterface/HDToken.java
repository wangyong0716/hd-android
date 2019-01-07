package fm.dian.hdui.net.oldinterface;

import org.json.JSONObject;

import fm.dian.hddata.cache.HDCache;
import fm.dian.hddata.util.HDLog;

public class HDToken {

    private static final String HDDATA_TOKEN = "HDDATA_TOKEN";
    private static final String TOKEN_KEY = "TOKEN_KEY";
    private static final String TOKEN_TYPE = "TOKEN_TYPE";
    private static final String TOKEN_TYPE_VALUE = "TOKEN_TYPE_VALUE";

    private static final String TOKEN_TYPE_PHONENUMBER = "TOKEN_TYPE_PHONENUMBER";
    private static final String TOKEN_TYPE_ACCOUNTID = "TOKEN_TYPE_ACCOUNTID";

    private static final HDToken TOKEN = new HDToken();

    private HDToken() {
    }

    public static final HDToken getInstance() {
        return TOKEN;
    }

    public boolean setTokenByAccountId(String accountId, String token) {
        boolean isOK = false;
        if (accountId == null || token == null) {
            new HDLog(HDToken.class).e("setTokenByAccountId [ERROR]: accountId or token is null.");
            return false;
        }
        try {
            TokenValue tokenValue = new TokenValue(token, TOKEN_TYPE_ACCOUNTID, accountId);
            HDCache cache = new HDCache();
            cache.setString(tokenValue.toString(), HDDATA_TOKEN);
        } catch (Throwable e) {
            new HDLog(HDToken.class).e(" setTokenByAccountId [ERROR]: " + e.getMessage(), e);
        }
        return isOK;
    }

    public boolean setTokenByPhoneNumber(String phoneNumber, String token) {
        boolean isOK = false;
        if (phoneNumber == null || token == null) {
            new HDLog(HDToken.class).e("setTokenByPhoneNumber [ERROR]: phoneNumber or token is null.");
            return false;
        }
        try {
            TokenValue tokenValue = new TokenValue(token, TOKEN_TYPE_PHONENUMBER, phoneNumber);
            HDCache cache = new HDCache();
            cache.setString(tokenValue.toString(), HDDATA_TOKEN);
        } catch (Throwable e) {
            new HDLog(HDToken.class).e(" setTokenByPhoneNumber [ERROR]: " + e.getMessage(), e);
        }
        return isOK;
    }

    public TokenValue getToken() {
        TokenValue tokenValue = null;
        try {
            tokenValue = new TokenValue(new HDCache().getString(HDDATA_TOKEN));
        } catch (Throwable e) {
            new HDLog(HDToken.class).e(" getToken [ERROR]: " + e.getMessage(), e);
        }
        return tokenValue;
    }

    public final class TokenValue {
        private String token;
        private String type;
        private String typeValue;

        public String getToken() {
            return token;
        }

        public boolean isTokenByAccountId() {
            return TOKEN_TYPE_ACCOUNTID.equals(type);
        }

        public boolean isTokenByPhoneNumber() {
            return TOKEN_TYPE_PHONENUMBER.equals(type);
        }

        private TokenValue(String token, String type, String typeValue) {
            super();
            this.token = token;
            this.type = type;
            this.typeValue = typeValue;
        }

        private TokenValue(String json) {
            super();
            try {
                if (json == null) {
                    new HDLog(HDToken.class).i(" toJson [ERROR]: json is null.");
                } else {
//					new HDLog().i(" toJson json: "+json);
                    JSONObject jo = new JSONObject(json);
                    type = jo.getString(TOKEN_TYPE);
                    token = jo.getString(TOKEN_KEY);
                    typeValue = jo.getString(TOKEN_TYPE_VALUE);
                }
            } catch (Throwable e) {
                new HDLog(HDToken.class).e(" toJson [ERROR]: " + e.getMessage(), e);
            }
        }

        public String toString() {
            JSONObject jo = new JSONObject();
            try {
                jo.put(TOKEN_TYPE, type);
                jo.put(TOKEN_KEY, token);
                jo.put(TOKEN_TYPE_VALUE, typeValue);
            } catch (Throwable e) {
                new HDLog(HDToken.class).e(" toString [ERROR]: " + e.getMessage(), e);
            }
            return jo.toString();
        }
    }
}
