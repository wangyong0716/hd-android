package fm.dian.hdservice;

import android.content.Context;

import fm.dian.hddata_android.HDData;
import fm.dian.hddata_android.auth.AuthRequest;
import fm.dian.hdservice.ConfigService.ConfigReadyCallBack;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.SysTool;

/**
 * Created by tinx on 5/11/15.
 */
public class HDDataService {
    private static final Logger log = Logger.getLogger(HDDataService.class);
    private static HDDataService hdDataService;

    private ConfigService configService;

    private final Context context;

    private HDDataService(Context context) {
        this.context = context;
    }

    public static HDDataService getInstance() {
        if (hdDataService == null) {
            hdDataService = new HDDataService(HDApp.getInstance().getContext());
        }
        return hdDataService;
    }

    public void start() {
        HDData.setLogLevel(HDData.LOG_LEVEL_TRACE);
        //设置中间层的日志级别
        configService = ConfigService.getInstance();
        configService.init(context, new ConfigReadyCallBack() {
            @Override
            public void process(ConfigService serivce) {
                if (isRunning()) {
                    log.debug("reload hddata");
                    log.debug(configService.getHDDataConfig());
                    HDData.reloadConfig(configService.getHDDataConfig());
                    log.debug("reload finish");
                } else {
                    log.debug("start hddata");
                    HDData.setDataFilePath(configService.getDataFilePath());
                    HDData.start(configService.getHDDataConfig());
                    log.debug(configService.getHDDataConfig());
                }
                AuthRequest.setDeviceIdentifier(SysTool.getDeviceUDID(context));
            }
        });
    }

    public boolean isRunning() {
        return HDData.running();
    }

    public void restart() {
        HDData.stop();
        start();
    }

    public void stop() {
        HDData.stop();
    }
}
