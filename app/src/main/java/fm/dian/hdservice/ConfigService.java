package fm.dian.hdservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fm.dian.hdservice.base.BaseService;
import fm.dian.hdservice.base.Config;
import fm.dian.hdservice.util.HttpClient;
import fm.dian.hdservice.util.HttpClient.RequestMethod;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.util.SysTool;


/**
 * Created by tinx on 4/3/15.
 */
public class ConfigService extends BaseService {

    private static final Logger log = Logger.getLogger(ConfigService.class, LOG_TAG);
    private static ConfigService configService;
    private final String dataFile = "hddata.db";
    private final String URL_CONFIG_DEBUG = "http://authtest.dian.fm:8888/configServer/config.htm";
    private final String URL_CONFIG = "http://config.dian.fm/config.htm";

    private Config config;
    private Context context;

    private ConfigService(int serviceType) {
        super(serviceType);
    }

    public static ConfigService getInstance() {
        if (configService == null) {
            configService = new ConfigService(0);
        }
        return configService;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public boolean isReady() {
        return null != config;
    }

    public void init(Context context, ConfigReadyCallBack callBack) {
        if (isReady()) {
            callBack.process(configService);
        } else {
            this.context = context;
            ConfigTask configTask = new ConfigTask(callBack);
            configTask.execute(URL_CONFIG);
        }
    }


    public String getDataFilePath() {
        String dbPath = null;

//        boolean isSdcardEnable = false;
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_MOUNTED.equals(state)){//SDCard是否插入
//            File f = new File(Environment.getExternalStorageDirectory().getPath());
//
//            if (f.canWrite()) {
//                isSdcardEnable = true;
//            }
//            else {
//
//                isSdcardEnable = false;
//            }
//        }
//        if(isSdcardEnable){
//
//            dbPath = Environment.getExternalStorageDirectory().getPath() + "/hddata.db";
//
//        }else{//未插入SDCard，建在内部储存中
//            dbPath = context.getFilesDir().getPath() + "/hddata.db";
//        }

//        String path = HDApp.getInstance().getExternalCacheDir().getPath() + File.separator + dataFile;
        dbPath = context.getFilesDir().getPath() + "/hddata.db";
        String path = dbPath;
        return path;
    }

    public String getLoginSignup() {
        if (config == null) {
            return null;
        }
        return config.getLogin().getSignup();
    }

    public String getLoginNewLogin() {
        if (config == null) {
            return null;
        }
        return config.getLogin().getNewLogin();
    }

    public String getLoginLogin() {
        if (config == null) {
            return null;
        }
        return config.getLogin().getLogin();
    }

    public String getLoginResetPassword() {
        if (config == null) {
            return null;
        }
        return config.getLogin().getResetPassword();
    }

    public boolean getUpdateNeedUpdate() {
        if (config == null) {
            return false;
        }
        return config.getUpdate().isNeedUpdate();
    }

    public boolean getUpdateForceUpdate() {
        if (config == null) {
            return false;
        }
        return config.getUpdate().isForceUpdate();
    }

    public boolean getNeedEarphone() {
        if (config == null) {
            return false;
        }
        return config.getUtils().isNeedEarphone();
    }

    public boolean getOpenBlackBoard() {
        if (config == null) {
            return false;
        }
        return config.getUtils().isOpenBlackBoard();
    }

    public String getUpdateUpdateUrl() {
        if (config == null) {
            return null;
        }
        return config.getUpdate().getUpdateURL();
    }

    public String getUpdateUpdateTitle() {
        if (config == null) {
            return null;
        }
        return config.getUpdate().getUpdateTitle();
    }

    public String getUpdateUpdateDescription() {
        if (config == null) {
            return null;
        }
        return config.getUpdate().getUpdateDescription();
    }

    public String getUtilsRoomNamePrefix() {
        if (config == null) {
            return null;
        }
        return config.getUtils().getRoomNamePrefix();
    }

    public Integer getLiveNumber() {
        if (config == null) {
            return null;
        }
        return config.getUtils().getLiveNumber();
    }


    public String getBufferTimePostAddress() {
        return config.getStatistics().getBufferTimePostAddress();
    }

    public String getHDDataConfig() {
        JSONObject hdConfig = new JSONObject();
        JSONArray mediaServer = new JSONArray();
        for (String mediaUrl : config.getMediaServer()) {
            mediaServer.put(mediaUrl);
        }
        JSONArray transferServer = new JSONArray();
        for (String transferUrl : config.getTransferServer()) {
            transferServer.put(transferUrl);
        }
        JSONArray videoServer = new JSONArray();
        for (String videoUrl : config.getVideoServer()) {
            videoServer.put(videoUrl);
        }

        JSONArray userCenter = new JSONArray();
        for (String userUrl : config.getUserCenter()) {
            userCenter.put(userUrl);
        }
        try {
            hdConfig.put("MediaServer", mediaServer);
            hdConfig.put("TransferServer", transferServer);
            hdConfig.put("VideoServer", videoServer);
            hdConfig.put("UserCenter", userCenter);
        } catch (JSONException e) {
            log.error("getHDDataConfig error", e);
        }
        Log.i("VideoConfig", hdConfig.toString());
        return hdConfig.toString();
    }

    public interface ConfigReadyCallBack {
        void process(ConfigService serivce);
    }

    private class ConfigTask extends AsyncTask<String, Integer, Config> {

        private ConfigReadyCallBack callBack;

        public ConfigTask(ConfigReadyCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Config doInBackground(String... strings) {
            if (isReady()) {
                return config;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("HD_CONFIG", context.MODE_PRIVATE);
            Config config = null;
            HttpClient httpClient = new HttpClient(strings[0], RequestMethod.POST);
            httpClient.addParam("platform", "ANDROID");
            httpClient.addParam("version", SysTool.getAppVersion(context));
            httpClient.addParam("deviceID", SysTool.getDeviceUDID(context));
            httpClient.addParam("model", android.os.Build.MODEL);
            try {
                String configJson = httpClient.request();

                log.debug("fetch config from server success! config:{}", configJson);
                Editor editor = sharedPreferences.edit();
                editor.putString("CONFIG_JSON", configJson);
                editor.commit();
                config = new Gson().fromJson(configJson, Config.class);
            } catch (Exception e) {
                log.error("fetch config from server error", e);
                String configJson = sharedPreferences.getString("CONFIG_JSON", null);
                if (null != configJson) {
                    try {
                        config = new Gson().fromJson(configJson, Config.class);
                    } catch (Exception ex) {
                        return null;
                    }
                }
                return config;
            }

            return config;
        }

        @Override
        protected void onPostExecute(Config config) {
            super.onPostExecute(config);
            setConfig(config);
            callBack.process(configService);
        }
    }
}
