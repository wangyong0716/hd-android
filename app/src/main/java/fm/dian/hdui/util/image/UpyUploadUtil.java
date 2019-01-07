package fm.dian.hdui.util.image;

import android.content.Context;

import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.util.SysTool;

/**
 * ******************
 * fileName  :UpyUploadUtil.java
 * author    : song
 * createTime:2015-1-21 下午4:55:42
 * fileDes   :
 */
public class UpyUploadUtil {
    // 本地文件路径
//		private String localFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.jpg";
    public static void uploadImage(Context context, File localFile, CompleteListener completeListener, ProgressListener progressListener) {
        String savePath = createRemoteImageName(context);

        try {
            UploaderManager uploaderManager = UploaderManager.getInstance(HongdianConstants.upy_host_upload);
            uploaderManager.setConnectTimeout(60);
            uploaderManager.setResponseTimeout(60);
            Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
            //			paramsMap.put("return_url", "");
            // signature & policy 建议从服务端获取
            String policyForInitial = UpYunUtils.getPolicy(paramsMap);
            String signatureForInitial = UpYunUtils.getSignature(paramsMap, HongdianConstants.upy_formApiSecret);
            uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String createRemoteImageName(Context context) {
//			http://image-storage.b0.upaiyun.com/hongdian-c/2015/1/995fe9e1bd789cd89c6083f15abbda63f755970c/16/1421380813.jpg"
        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH) + 1;
        int day = calender.get(Calendar.DATE);

        String remoteName = HongdianConstants.upy_android_dir + year + "/" + month + "/" + SysTool.getIMEI(context) + "/" + day + "/" + System.currentTimeMillis() + ".jpg";
        return remoteName;
    }

}
