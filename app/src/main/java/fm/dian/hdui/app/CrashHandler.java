package fm.dian.hdui.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.WindowManager;

import java.lang.Thread.UncaughtExceptionHandler;

import fm.dian.hdservice.util.Logger;

public class CrashHandler implements UncaughtExceptionHandler {
    Logger log = Logger.getLogger(CrashHandler.class);
    SharedPreferences sp;
    WindowManager windowManager;
    LayoutInflater inflater;

    private static CrashHandler myCrashHandler;
    private static Context CrashContext;

    private CrashHandler() {
        inflater = LayoutInflater.from(CrashContext);
        windowManager = (WindowManager) CrashContext.getSystemService(Context.WINDOW_SERVICE);
        sp = CrashContext.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static synchronized CrashHandler getInstance(Context context) {
        CrashContext = context;
        if (myCrashHandler == null) {
            return new CrashHandler();
        }
        return myCrashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        log.error("thread crash", ex);
//        MediaControl.nativeForceStopRecord();
        ActivityManager.getInstance().popAllActivityExceptOne(this.getClass());//关闭所有activity
        Process.killProcess(Process.myPid());//杀死当前应用
        System.exit(0);
    }
//    private void restartApplication() {
//        Log.e("error_hd","start =");
//        final Intent intent = new Intent(CrashContext, HDSplashActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
//        CrashContext.startActivity(intent);
//    }

}
