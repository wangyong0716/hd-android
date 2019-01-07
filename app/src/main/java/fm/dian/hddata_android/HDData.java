package fm.dian.hddata_android;

public class HDData {

    public static final int LOG_LEVEL_TRACE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARN = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public static final int LOG_LEVEL_FATAL = 5;


    public native static void setDataFilePath(String data_file_path);

    public native static boolean start(String config);

    public native static void stop();

    public native static boolean running();

    public native static boolean isGood(int service);

    public native static void batterySaving();

    public native static long loopIntervalMicroSeconds();

    public native static void setLoopIntervalMicroSeconds(long interval);

    /*
        kTrace = 0,
        kDebug = 1,
        kInfo  = 2,
        kWarn  = 3,
        kError = 4,
        kFatal = 5,
    */
    public native static void setLogLevel(int level);

    public native static void reloadConfig(String config);
}
