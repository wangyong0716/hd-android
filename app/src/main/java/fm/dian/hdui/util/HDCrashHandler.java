package fm.dian.hdui.util;

import java.lang.Thread.UncaughtExceptionHandler;

import fm.dian.hddata.util.HDLog;

public class HDCrashHandler implements UncaughtExceptionHandler {

    private HDLog log = new HDLog(HDCrashHandler.class);

    private UncaughtExceptionHandler mDefaultCrashHandler;

    public void init() {
        log.i("init.");

        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private static HDCrashHandler crashHandler = new HDCrashHandler();

    public static HDCrashHandler getInstance() {
        return crashHandler;
    }

    public void uncaughtException(Thread thread, Throwable ex) {

        ex.printStackTrace();

        new ActivityCheck().goIndex();

        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        }

    }

}
