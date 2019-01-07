package fm.dian.hdui.util;

public final class HDUiLog {

    private boolean isDebug = true;
    private boolean isDebugNetwork = true;

    private static final HDUiLog hdLog = new HDUiLog();
    private static final String TAG = "~~~~~~~~~~~[HDUi_log]";

    private static final HDUiLog getInstance() {
        return hdLog;
    }

    public boolean isDebugNetwork() {
        return getInstance().isDebugNetwork;
    }

    public void debugNetwork(boolean isDebugNetwork) {
        getInstance().isDebugNetwork = isDebugNetwork;
    }

    public boolean isDebugging() {
        return getInstance().isDebug;
    }

    public void debug(boolean debug) {
        getInstance().isDebug = debug;
    }

    public void i(String format, Object... args) {
        if (isBadLog(format)) {
            return;
        }
        if (getInstance().isDebug) {
            String msg = String.format(format, args);
            android.util.Log.i(TAG, TAG + msg);
        }
    }

    public void i(String msg) {
        if (isBadLog(msg)) {
            return;
        }
        if (getInstance().isDebug) {
            android.util.Log.i(TAG, TAG + msg);
        }
    }

    public void e(String msg) {
        if (isBadLog(msg)) {
            return;
        }
        if (getInstance().isDebug) {
            android.util.Log.e(TAG, TAG + msg);
        }
    }

    public void eNet(String format, Object... args) {
        if (format == null) {
            return;
        }
        String msg = String.format(format, args);
        if (getInstance().isDebugNetwork) {
            android.util.Log.e(TAG, TAG + msg);
        }
    }

    public void e(String msg, boolean isShowStackTrace) {
        if (isBadLog(msg)) {
            return;
        }
        showStackTrace(4, null);
        if (getInstance().isDebug) {
            android.util.Log.e(TAG, TAG + msg);
        }
    }

    public void e(String format, Object... args) {
        if (isBadLog(format)) {
            return;
        }
        if (getInstance().isDebug) {
            String msg = String.format(format, args);
            android.util.Log.e(TAG, TAG + msg);
        }
    }

    public void e(Throwable e) {
        if (isBadLog(e)) {
            return;
        }
        if (getInstance().isDebug) {
            android.util.Log.e(TAG, TAG, e);
        }
    }

    public void e(String msg, Throwable e) {
        if (isBadLog(e)) {
            return;
        }
        if (getInstance().isDebug) {
            android.util.Log.e(TAG, TAG + msg, e);
        }
    }

    private boolean isBadLog(Object log) {
        if (log == null) {
            showStackTrace(4, "Log Message is null");
            return true;
        }
        return false;
    }

    private void showStackTrace(int stackTraceLevel, String message) {
        if (getInstance().isDebug && stackTraceLevel >= 0) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append(TAG);
            sb.append("[Thread StackTrace]: ");
            if (message != null) {
                sb.append(message);
            }
            sb.append(" -> ");
            for (int i = stackTraceLevel; i < trace.length; i++) {
                sb.append('\n');
                sb.append(trace[i].toString());
            }
            android.util.Log.e(TAG, sb.toString());

            trace = null;
            sb = null;
        }
    }
}


