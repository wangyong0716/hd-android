package fm.dian.hdui.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import fm.dian.hddata.HDData;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.view.face.FaceConversionUtil;
import hd.hdmedia.HDMediaModule;

public class HDApp extends Application {
    public static final String smallIcon = "!160";
    private static HDApp instance;
    private static File appCachePicDir;
    public ImageLoader imageLoader;
    public String threeAppEnterWebaddr = null;
    private User currUser;
    private long currRoomId = 0;
    private long currLiveId = 0;
    private String currLiveName;
    private String currPwd = "";
    public boolean currRoomDel = false;
    public boolean currUserPulledblack = false;

    public static HDApp getInstance() {
        return instance;
    }

    static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("JNIHDData");
        System.loadLibrary("openh264");
        System.loadLibrary("opustool");
        System.loadLibrary("hdcodec");

    }

    public void onCreate() {
//        开发中请关闭，发布时打开即可
        CrashHandler myCrashHandler = CrashHandler.getInstance(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(myCrashHandler);

        super.onCreate();
        instance = this;
        HDMediaModule.getInstance().setAppId("21d9ffe49d4802a5");


        new HDData().initContext(this);//老数据层，需要删掉

        //关闭日志
        Logger.disableLogging(false);
//        HDDataService hdDataService = HDDataService.getInstance();
//        hdDataService.start();

        initImageLoader();
        initChatFace();
    }

    //载入聊天页面表情图片
    private void initChatFace() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(instance);
            }
        }).start();
    }

    private void initImageLoader() {
        // 创建程序缓存目录
        appCachePicDir = new File(Environment.getExternalStorageDirectory()
                + "/" + HongdianConstants.APP_NAME + "/pic");
        if (!appCachePicDir.exists()) {
            try {
                appCachePicDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ImageLoaderConfiguration config = Config.getImageLoaderConfig(
                getApplicationContext(), appCachePicDir);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    public File getCacheDir() {
        return appCachePicDir;
    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User user) {
        this.currUser = user;
    }

    public long getCurrRoomId() {
        return currRoomId;
    }

    public void setCurrRoomId(long currRoomId) {
        this.currRoomId = currRoomId;
    }

    public String getCurrLiveName() {
        return currLiveName;
    }

    public void setCurrLiveName(String currLiveName) {
        this.currLiveName = currLiveName;
    }

    public String getCurrPwd() {
        return currPwd;
    }

    public void setCurrPwd(String currPwd) {
        this.currPwd = currPwd;
    }

    public Context getContext() {
        return this;
    }
}
