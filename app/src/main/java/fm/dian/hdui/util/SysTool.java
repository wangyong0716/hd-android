package fm.dian.hdui.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("deprecation")
public class SysTool {

    public static final String SDCARD_ROOT_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();// 路径
    private static final String TAG = "SysTool";

    /**
     * 检查存储卡是否插入
     *
     * @return
     */
    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取手机的imei号码
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String IMEI = null;
        // 获取IMEI码
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();
        return IMEI;
    }

    /* 获取当前应用的版本号 */
    public static String getVersion(Context context) {
        String version = null;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息(eg：1.1)
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取sdcard上剩余空间
     *
     * @return 返回值单位为k
     */

    public static float getFreeSpaceOnSD() {
        // 检测sdcard是否已经挂载了
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            long blockSize = statfs.getBlockSize();// sdcard存储单位
            long availaBlock = statfs.getAvailableBlocks();// 可用空间块
            return blockSize * availaBlock / 1024;
        } else {
            return -1;
        }
    }

    public static boolean saveBmpToFile(Bitmap bmp, File file) {
//		int MAXFREDSPACE = 1 * 1024 * 1024;// sdcard最大剩余空间1m
//		if (getFreeSpaceOnSD() < MAXFREDSPACE) {
//			Log.w(TAG, "Low free space onsd, do not cache");
//			return false;
//		}
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);// 手动构建临时文件路径
            bmp.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "没有找到文件");
        } catch (IOException e) {
            Log.w(TAG, "IOException io异常");
        }
        return false;
    }


    // 将手机中图片的uri转换成绝对路径
    public static String turnUri2AbsolutePath(Activity context, Uri uri) {
        // 获取图片在sdcard上的路径
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.managedQuery(uri, proj, null, null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        return cursor.getString(column_index);
    }

    // gzip压缩
    public static byte[] gZip(File srcFile) throws IOException {
        byte[] data = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            @SuppressWarnings("resource")
            FileInputStream fis = new FileInputStream(srcFile);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fis.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte[] bContent = out.toByteArray();
            out.reset();// 清空byte数组

            GZIPOutputStream gOut = new GZIPOutputStream(out, bContent.length); // 压缩级别,缺省为1级
            DataOutputStream objOut = new DataOutputStream(gOut);
            objOut.write(bContent);
            objOut.flush();
            gOut.close();
            data = out.toByteArray();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return data;
    }

    // 压缩图片时候获取压缩属性
    public static Options getOptions(Context context, int sampleSize) {
        if (sampleSize == 0) {
            sampleSize = 2;
        }

        Options opt = new Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;

        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;

        // 获取屏的宽度和高度
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2，图片即为原图1/4
        opt.inSampleSize = sampleSize;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        } else {
            if (picHeight > screenHeight)
                opt.inSampleSize = picHeight / screenHeight;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;

        return opt;
    }

    // bitmap图片转换圆角（一般情况下参数二填30像素就行了）
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap = null;
        return output;
    }

    // 判断当前网络是否可用
    public static boolean isHaveNetwork(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("check network exception");
        }
        return false;

    }

    // 获取listview的条目高度（注：listview的每一个item的父容器必须是linearlayout，只有linearlayout才重写了onMeasure（））
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);//计算
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    // 获取当前屏幕宽度
    public static int getScreenWidth(Context context) {
        // 获取屏的宽度和高度
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        return screenWidth;
    }

    // 通过uri获取图片bitmap
    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取手机系统版本号
    public static String getAndroidSDKVersion() {
        String version = null;
        try {
            version = android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 判断是否是不兼容的版本
     *
     * @return true是非兼容版本 false是兼容版本
     */
    public static boolean IsErrorSDKVersion() {
        // String androidSDKVersion="4.0.4";
        String androidSDKVersion = getAndroidSDKVersion();
        if (androidSDKVersion != null) {
            String[] split = androidSDKVersion.split("\\.");
            if (split.length == 3) {
                if (Integer.parseInt(split[0]) > 4) {

                    return true;
                } else if (Integer.parseInt(split[0]) == 4) {
                    if (Integer.parseInt(split[1]) > 0) {
                        return true;
                    } else {
                        if (Integer.parseInt(split[2]) > 3) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }

            } else {
                return true;
            }
        }

        return false;
    }

    // bitmapt缩放，并且压缩成方形图片

    /**
     * @param sourceFile
     * @param context
     * @param targetWidth 目标宽度
     */
    public static Bitmap scaleBitmap(File sourceFile, Context context,
                                     Float targetWidth) {
        try {
            Options opt = SysTool.getOptions(context, 2);
            Bitmap decodeFile = BitmapFactory.decodeFile(
                    sourceFile.getAbsolutePath(), opt);
            // // //
            // System.out.println("拍照图片宽度"+decodeFile.getWidth()+"高"+decodeFile.getHeight());
            int imgWidth = decodeFile.getWidth();
            int imgHeight = decodeFile.getHeight();
            int length = imgWidth < imgHeight ? imgWidth : imgHeight;
            int x = 0;
            int y = 0;
            if (imgWidth > imgHeight) {// 横屏拍摄
                x = (imgWidth - imgHeight) / 2;
                y = 0;
            } else {
                x = 0;
                y = (imgHeight - imgWidth) / 2;
            }
            // 按照600宽缩放
            float scaleFactor = targetWidth / length;
            // // // System.out.println("缩放比："+scaleFactor);
            Matrix m = new Matrix();
            m.postScale(scaleFactor, scaleFactor);
            // m.postRotate(90);//旋转90度（）
            Bitmap scaledImage = Bitmap.createBitmap(decodeFile, x, y, length,
                    length, m, false);
            FileOutputStream outStream = new FileOutputStream(sourceFile);
            CompressFormat format = CompressFormat.JPEG;
            int quality = 90;
            scaledImage.compress(format, quality, outStream);// 把bitmap用format格式，quality质量，输出到outStream流
            decodeFile.recycle();
            return scaledImage;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    // 中文符号替换为英文符号，滤掉特殊符号
    public static String stringFilter(String str) {
        str = str.replaceAll("\\[", "【").replaceAll("\\]", "】")
                .replaceAll("!", "！").replaceAll("：", ":").replaceAll(",", "，");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    // 获取本地（asserts）文件，转化为utf-8
    public static String getFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF_8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean notEmptyOrNull(String str) {
        if (str != null && !"".equals(str))
            return true;
        return false;
    }

    // 检查是否是手机号码
    public static boolean telCheck(String phonenumber) {
        String regEx = "^0?(13[0-9]|15[012356789]|18[0236789]|14[57])[0-9]{8}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(phonenumber);
        return m.matches();
    }

    // 检查是否是座机号码
    public static boolean fixedTelCheck(String phonenumber) {
        String regEx = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{5,8})(-(\\d{3}))?$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(phonenumber);
        return m.matches();
    }

    public static int getSystemVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }


    public static int convertPxToDip(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f * (px >= 0 ? 1 : -1));
    }

    public static int convertDipToPx(Context context, int dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dip + 0.5f);
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(Context context) {
        // 收起软键盘
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppVersion(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            String vn = pi.versionName;
            return vn;
        } catch (Throwable e) {
            Log.e("getAppVersion: ", e.getMessage());
        }

        return "";
    }

    public static String getDeviceUDID(Context context) {
        String udid = null;
        try {
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            udid = telephony.getDeviceId();
            if (udid == null || udid.length() == 0) {
                udid = getCPUSerial();
            }
        } catch (Throwable e) {
            Log.e("getDeviceUDID [ERROR]: ", e.getMessage());
        }
        return udid;
    }

    private static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    if (str.indexOf("Serial") > -1) {
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Throwable e) {
            Log.e("getCPUSerial [ERROR]: ", e.getMessage());
        }
        return cpuAddress;
    }
}
