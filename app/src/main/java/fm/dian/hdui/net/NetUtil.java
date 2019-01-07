package fm.dian.hdui.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;


public class NetUtil {

    public static final String TAG = "NetUtil";
    static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
    static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
    public static final String IMAGE_CAPTURE_NAME = "cameraTmp.jpeg"; // 照片名称


    public static Bitmap getBitmapCaptcha(String path) throws Exception {
        // String dest=path.substring(0, path.length()-6);//
        URL url = new URL(path);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        return BitmapFactory.decodeStream(is);
    }

//	@SuppressWarnings("deprecation")
//	public static String[] postRequest(String url, String imgPath,
//			HashMap<String, String> params, Context context) throws Exception {
//		String returnJson = "";
//		String[] result = new String[2];// 默认情况下网络不可用
//		result[0] = "000";
//		result[1] = "{\"returnCode\":\"000\",\"returnMess\":\"网络不给力\"}";
//		BasicHttpParams httpParams = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
//		HttpClient client = new DefaultHttpClient(httpParams);
//
//		//设置请求超时时间
//		client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, SO_TIMEOUT);
//		if(url==null){//排除程序异常时�?主机地址为空的异�?
////			EnumDeserializer.setData();
//			result[1] = "{\"returnCode\":\"000\",\"returnMess\":\"主机地址为空\"}";
//			return result;
//		}
//		
//		HttpPost post = new HttpPost(url);
//		MultipartEntity entity = new MultipartEntity(
//				
//		HttpMultipartMode.BROWSER_COMPATIBLE);
//		entity.addPart("myuserid", new StringBody(Long.toString(AppAplication.getCurrentUserId())));
//		entity.addPart("via", new StringBody(Constants.VIA));
//		entity.addPart("version", new StringBody(SysTool.getVersion(context)));
//		entity.addPart("uuid", new StringBody(SysTool.getIMEI(context)));
//		// 如果有图片的post请求，就创建�?��图片文件字段
//		if (imgPath != null) {
////			File temp = new File(imgPath);
//			entity.addPart("image", new FileBody(new File(imgPath)));
//			
//		}
//		if (params != null) {
//			for (Entry<String, String> en : params.entrySet()) {
//				entity.addPart(en.getKey(),new StringBody(URLEncoder.encode(en.getValue())));
//			}
//		}
//
//		BasicHttpParams httpParams2 = new BasicHttpParams();
//		httpParams.setParameter("charset", HTTP.UTF_8);
//		post.setParams(httpParams2);
//		post.setEntity(entity);
// 
//		try {
//			HttpResponse response = client.execute(post);
//			if (response != null) {
//				returnJson = EntityUtils.toString(response.getEntity());
//				JSONObject jsonObject = new JSONObject(returnJson);
//				int returnCode = jsonObject.getInt("returnCode");
//				if (returnCode == ErrorCodeList.SUCCESS) {
//					result[0] = Integer.toString(ErrorCodeList.SUCCESS);
//					result[1] = returnJson;
//				}else{
//					result[0] = Integer.toString(ErrorCodeList.ERROR);
//					result[1] = returnJson;
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			// // System.out.println("netUtuil post请求异常");
//		}
//		return result;
//	}


    public static String getJson(String path) throws Exception {
        String json = null;
        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(path);
        HttpResponse response = client.execute(get);
        if (response != null) {
            json = EntityUtils.toString(response.getEntity());

        }
        return json;
    }

    public static String getRequest(String path, Map<String, String> mParams, Context context) throws Exception {
        String returnJson = "";

        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParams);

        client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, SO_TIMEOUT);

        if (mParams != null) {
            int i = 0;
            for (Entry<String, String> en : mParams.entrySet()) {
                if (i == 0) {
                    path += "?" + en.getKey() + "=" + en.getValue();
                } else {
                    path += "&" + en.getKey() + "=" + en.getValue();
                }
                i++;
            }
        }

        System.out.println("请求的url===" + path);
        HttpGet get = new HttpGet(path);
        try {
            HttpResponse response = client.execute(get);
            if (response != null) {
                returnJson = EntityUtils.toString(response.getEntity());
                System.out.println("返回值===" + returnJson);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnJson;
    }

    /**
     * �?��网络连接是否可用
     *
     * @param ctx
     * @return true 可用; false 不可�?
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netinfo = cm.getAllNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        for (int i = 0; i < netinfo.length; i++) {
            if (netinfo[i].isConnected()) {
                return true;
            }
        }
        return false;
    }
}
