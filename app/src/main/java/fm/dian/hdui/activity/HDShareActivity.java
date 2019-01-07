package fm.dian.hdui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

import java.io.InvalidObjectException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.sdk.qq.QQBaseUIListener;
import fm.dian.hdui.util.image.ImageScaleUtil;

public class HDShareActivity extends HDBaseActivity implements IWXAPIEventHandler, IWeiboHandler.Response {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String LIVE_ID = "LIVE_ID";
    private IWXAPI wxApi;
    private Tencent mTencent;
    IWeiboShareAPI mWeiboShareAPI;

    private CoreService coreService = CoreService.getInstance();

    private String roomNamePrefix;
    private Bitmap thumb = null;
    long roomId;
    long liveId;
    String shareUrl = "";
    Room room;
    private int shareType;

    @Override
    public String toString() {
        return "HDShareActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStateBar();
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_share);
        initUI();
        loadData();
    }

    //Android5.0 状态栏透明
    private void hideStateBar(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void initUI() {
        //实例化
        wxApi = WXAPIFactory.createWXAPI(this, HongdianConstants.WX_ID, true);
        wxApi.handleIntent(getIntent(), this);
        wxApi.registerApp(HongdianConstants.WX_ID);

        mTencent = Tencent.createInstance(HongdianConstants.QQ_APPID, getApplicationContext());

        roomNamePrefix = "http://" + ConfigService.getInstance().getUtilsRoomNamePrefix();
        roomId = getIntent().getLongExtra(ROOM_ID, 0);
        liveId = getIntent().getLongExtra(LIVE_ID, 0);
        shareUrl = getIntent().getStringExtra("shareUrl");
        shareType = getIntent().getIntExtra("shareType",0);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (null != mWeiboShareAPI) {
            mWeiboShareAPI.handleWeiboResponse(intent, HDShareActivity.this); //当前应用唤起微博分享后,返回当前应用
        }
    }

    private void loadData() {
        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    room = (Room) data.getSerializable("room");

                    if (room != null) {
                        HDApp.getInstance().imageLoader.loadImage(room.getAvatar(), new ImageLoadingListener() {
                            public void onLoadingStarted(String arg0, View arg1) {
                            }

                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            }

                            public void onLoadingCancelled(String arg0, View arg1) {
                            }

                            public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
                                thumb = bitmap;
                            }
                        });
                    }
                }
            }
        });
    }

    //浏览器打开
    public void shareBrowser(View v) {
        String url = "";
        if (null != shareUrl) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            url = shareUrl;
        } else {
            checkRoomLoaded();

            url = roomNamePrefix + room.getWebaddr();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            if (liveId != 0) {
                url += "?live_id=" + liveId;
            }
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

        shareCancel(v);
    }

    //复制链接
    public void shareCopy(View v) {
        String url = "";
        if (null !=  shareUrl) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            url = shareUrl;
        }
        else {
            checkRoomLoaded();

            url = roomNamePrefix + room.getWebaddr();

            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            if(liveId!=0){
                url+="?live_id="+liveId;
            }
        }
        ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(url);
        Toast.makeText(HDShareActivity.this, "链接复制成功", Toast.LENGTH_SHORT).show();
        shareCancel(v);
    }

    //分享微信好友
    public void shareWeixin(View v) {
        wechatShare(0);
        shareCancel(v);
    }

    //分享微信朋友圈
    public void shareWeixin2(View v) {
        wechatShare(1);
        shareCancel(v);
    }

    //分享qq
    public void shareQQ(View v) {
        checkRoomLoaded();

        Bundle bundle = new Bundle();
        bundle.putString("title", room.getName());
        if (null != room.getAvatar() && !"".equals(room.getAvatar())) {
            bundle.putString("imageUrl", roomNamePrefix + room.getAvatar());

        }
        String url = "";
        if (null !=  shareUrl) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            url = shareUrl;
        }
        else {
            url = roomNamePrefix + room.getWebaddr();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            if(liveId!=0){
                url+="?live_id="+liveId;
            }
        }
        bundle.putString("targetUrl", url);

        bundle.putString("summary", "我加入了【" + room.getName() + "】，频道号" + room.getWebaddr() + "，你也来吧！");
        bundle.putString("appName", "红点");
        QQBaseUIListener uiListener = new QQBaseUIListener(HDShareActivity.this, new QQBaseUIListener.AuthCompleteListener() {
            @Override
            public void authComplete() {
                Toast.makeText(HDShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }
        });
        mTencent.shareToQQ(HDShareActivity.this, bundle, uiListener);

        shareCancel(v);
    }

    //分享新浪微博
    public void shareWeibo(View v) {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, HongdianConstants.WB_ID);
        boolean reg = mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端
        if (!reg) {
            Toast.makeText(HDShareActivity.this, "尚未安装微博客户端", Toast.LENGTH_SHORT).show();
            shareCancel(v);
            return;
        }
        checkRoomLoaded();

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        String url = "";
        if (null !=  shareUrl) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            url = shareUrl;
        }
        else {
            url = roomNamePrefix + room.getWebaddr();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            if(liveId!=0){
                url+="?live_id="+liveId;
            }
        }

        textObject.text = "我加入了【" + room.getName() + "】，频道号" + room.getWebaddr() + "，你也来吧！" + url;
        weiboMessage.textObject = textObject;
//        ImageObject imageObject = new ImageObject();
//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
//        if(thumb == null){
//            imageObject.setThumbImage(bitmap);
//        }else{
//            imageObject.setThumbImage(thumb);
//        }
//        weiboMessage.imageObject = imageObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(request); //发送请求消息到微博,唤起微博分享界面

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HDShareActivity.this.finish();
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
    }

    public void closeAct(View v) {
        finish();
    }

    public void shareCancel(View v) {
        finish();
    }


    //    --------------工具方法-----------------
    private void wechatShare(final int flag) {
        if (room == null){
            Toast.makeText(this, R.string.net_get_data_fail, Toast.LENGTH_SHORT).show();
            finish();
        }
        checkRoomLoaded();

        WXWebpageObject webpage = new WXWebpageObject();
        String url = "";
        if (null !=  shareUrl) {
            if (!shareUrl.startsWith("http://")) {
                shareUrl = "http://" + shareUrl;
            }
            url = shareUrl;
        }
        else {
            url = roomNamePrefix + room.getWebaddr();
            if (!url.startsWith("http://")) {
                url = "http://" + url;
            }
            if(liveId!=0){
                url+="?live_id="+liveId;
            }
        }
        webpage.webpageUrl = url;

        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = room.getName();
        msg.description = room.getDescription();
        //这里替换一张自己工程里的图片资源
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        if (thumb == null) {
            msg.setThumbImage(bitmap);
        } else {
            Bitmap zoomImg = ImageScaleUtil.zoomImg(thumb, 200, 200);
            msg.setThumbImage(zoomImg);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);

    }

    //微信 sdk 回调
    public void onReq(BaseReq arg0) {
    }

    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                Toast.makeText(this, "分享成功 ", Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                Toast.makeText(this, "分享取消 ", Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                Toast.makeText(this, "分享拒绝", Toast.LENGTH_SHORT).show();
                break;
        }
        umengStatistic(2);
    }

    //微博 sdk 回调
    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                break;
        }
        Toast.makeText(HDShareActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
        HDShareActivity.this.finish();
        umengStatistic(4);
    }

    //qq sdk 回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mTencent) {
            mTencent.onActivityResult(requestCode, resultCode, data);
            umengStatistic(3);
        }
    }

    //分享统计
    private void umengStatistic(int type) {
        UMPlatformData.UMedia uType = UMPlatformData.UMedia.SINA_WEIBO;
        switch (type) {
            case 1://微信好友
                uType = UMPlatformData.UMedia.WEIXIN_FRIENDS;
                break;
            case 2://微信朋友圈
                uType = UMPlatformData.UMedia.WEIXIN_CIRCLE;
                break;
            case 3://qq
                uType = UMPlatformData.UMedia.TENCENT_QQ;
                break;
            case 4://微博
                uType = UMPlatformData.UMedia.SINA_WEIBO;
                break;
            default:
                uType = UMPlatformData.UMedia.SINA_WEIBO;
                break;
        }
        HDUser user = new HDUserCache().getLoginUser();
        UMPlatformData platform = new UMPlatformData(uType, user.userId + "");
        platform.setName(user.nickname);
        MobclickAgent.onSocialEvent(this, platform);
    }

    // 确保room数据已经加载好，否则退出Activity
    private void checkRoomLoaded()
    {
        if (room == null) {
            Toast.makeText(this,
                    getResources().getString(R.string.act_share_get_data_fail),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}