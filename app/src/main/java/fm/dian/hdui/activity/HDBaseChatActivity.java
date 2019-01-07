package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.dian.hdservice.BlackBoardService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Card;
import fm.dian.hdui.R;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.util.image.UpyUploadUtil;
import fm.dian.hdui.view.blackboard.BlackboardRelativeLayoutTop;
import fm.dian.hdui.wximage.choose.utils.ImageItem;
import fm.dian.service.blackboard.HDBlackboardCard;

/**
 * ******************
 * fileName :HDBaseChatActivity.java author : song createTime:2015-1-19
 * 上午11:15:30 fileDes :
 */
@SuppressLint("InflateParams")
public class HDBaseChatActivity extends HDBaseActivity {
    private PopupWindow mVoicePopupWindow;
    private ImageView iv_pop_left_icon, iv_pop_right_icon;
    private ProgressBar pb_pop_loading;
    protected TextView tv_pop_msg;
    protected PopupWindow mPopupWindow;

    BlackBoardService blackBoardService = BlackBoardService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //标题栏悬浮并透明 （问题是需要控制当小黑板关闭的时候设置回来颜色）
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_transparent_half)));
        setContentView(R.layout.activity_chat);
        //初始化完成页面，收起软键盘
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//		WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View menu_content = LayoutInflater.from(this).inflate(R.layout.layout_chat_activity_voice_dialog, null);
        tv_pop_msg = (TextView) menu_content.findViewById(R.id.tv_pop_msg);
        iv_pop_left_icon = (ImageView) menu_content.findViewById(R.id.iv_pop_left_icon);
        iv_pop_right_icon = (ImageView) menu_content.findViewById(R.id.iv_pop_right_icon);
        pb_pop_loading = (ProgressBar) menu_content.findViewById(R.id.pb_pop_loading);

        mVoicePopupWindow = new PopupWindow(menu_content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
//		mVoicePopupWindow.setBackgroundDrawable(new BitmapDrawable());  
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initUI() {

    }

    public void dismissVoicePopupWindow() {
        if (mVoicePopupWindow.isShowing()) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVoicePopupWindow.dismiss();
                }
            }, 2000);
        }
    }

    /**
     * @param parent
     * @param type   0:连接中  1：手指上滑，锁住上麦  2：松开手指，锁住上麦  3：上麦失败，请重试  4：已取消上麦
     */
    public void showVoicePopupWindow(View parent, int type) {
        switch (type) {
            case 0:
                showPop(parent, false, true, "", R.drawable.dialog_mic_bg, 0);
                break;
            case 1:
                showPop(parent, false, false, "手指上滑，锁住上麦", R.drawable.dialog_mic_bg,
                        R.drawable.dialog_voice_height);
                break;
            case 2:
                showPop(parent, true, false, "松开手指，锁住上麦", R.drawable.dialog_mic_lock_bg,
                        R.drawable.dialog_voice_height);
                break;
            case 3:
                showPop(parent, false, false, "上麦失败，请重试", R.drawable.dialog_mic_bg,
                        R.drawable.dialog_cancel_bg);
                break;
            case 4:
                showPop(parent, false, false, "已取消上麦", R.drawable.dialog_tan_bg, 0);
                break;
            case 100:
                showPop(parent, false, false, "录音测试", R.drawable.dialog_mic_bg,
                        R.drawable.amp1);
                break;

            default:
                break;
        }
    }

    private void showPop(View parent, boolean isRedMsg, boolean isLoading, String msg,
                         int leftIcon, int rightIcon) {

        if (!mVoicePopupWindow.isShowing()) {
            mVoicePopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
        if (isRedMsg) {
            tv_pop_msg.setBackgroundResource(android.R.color.holo_red_dark);
        } else {
            tv_pop_msg.setBackgroundResource(android.R.color.transparent);
        }

        if (isLoading) {
            pb_pop_loading.setVisibility(View.VISIBLE);
        } else {
            pb_pop_loading.setVisibility(View.GONE);
        }

        if (leftIcon != 0) {
            iv_pop_left_icon.setVisibility(View.VISIBLE);
            iv_pop_left_icon.setBackgroundResource(leftIcon);
        } else {
            iv_pop_left_icon.setVisibility(View.GONE);
        }

        if (rightIcon != 0) {
            iv_pop_right_icon.setVisibility(View.VISIBLE);
            iv_pop_right_icon.setBackgroundResource(rightIcon);
        } else {
            iv_pop_right_icon.setVisibility(View.GONE);
        }
        if (msg != null) {
            tv_pop_msg.setText(msg);
        }
    }


    public void uploadBlackboardImage(final ArrayList<ImageItem> imagesList, final BlackboardRelativeLayoutTop blackboardRelativeLayoutTop) {
        final Map<String,String> blackBoardImageMap = new HashMap<>();

        blackboardRelativeLayoutTop.setVisibility(View.VISIBLE);
        blackboardRelativeLayoutTop.showProgressBar();

        File file = null;
        for (final ImageItem item : imagesList) {

            file = new File(item.imagePath);

        final File finalFile = file;
        UpyUploadUtil.uploadImage(this, file, new CompleteListener() {
                @Override
                public void result(boolean isComplete, String result, String error) {
                    if (isComplete) {
                        try {

                            JSONObject jsonObject = new JSONObject(result);
                            String path = HongdianConstants.upy_host_download + jsonObject.getString("path");
                            if (null != path) {
                                blackBoardImageMap.put(finalFile.getAbsolutePath(), path);
                                blackboardRelativeLayoutTop.updateProgressBar(100);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "图片上传失败:" + error, Toast.LENGTH_SHORT).show();
                        blackBoardImageMap.put(finalFile.getAbsolutePath(), "error");
                    }

                    //所有图片上传又拍云完成以后，发送到红点服务器
                    if(blackBoardImageMap.size() == imagesList.size()){
                        final Card currCard = blackboardRelativeLayoutTop.getCurrCard();
                        uploadBlackboardHDServer(currCard, blackBoardImageMap, imagesList,blackboardRelativeLayoutTop);

                        blackboardRelativeLayoutTop.updateProgressBar(100);
                        blackboardRelativeLayoutTop.postDelayed(new Runnable() {
                        public void run() {
                            blackboardRelativeLayoutTop.dismissProgressBar();
                            blackboardRelativeLayoutTop.updateProgressBar(0);
                        }
                    }, 100);
                    }
                }
            }, new ProgressListener() {
                @Override
                public void transferred(long transferedBytes, long totalBytes) {
                    double uploadedProgress = blackBoardImageMap.size() * (100.0 / imagesList.size());

                    double progress = (transferedBytes * 1.0) / totalBytes;
                    double currPicProgress = progress * (100.0 / imagesList.size());
                    double setProgress = currPicProgress + uploadedProgress;

                    if(blackboardRelativeLayoutTop.getProgress() < (int)setProgress){

                        blackboardRelativeLayoutTop.updateProgressBar((int)setProgress );
                    }
                }
            });
        }
    }


    private void uploadBlackboardHDServer(Card currCard,Map<String,String> blackBoardImageMap,ArrayList<ImageItem> imagesList, final BlackboardRelativeLayoutTop blackboardRelativeLayoutTop) {
        final List<Card> cards = new ArrayList<>();

        if (null != blackBoardImageMap && null != imagesList) {
            for(ImageItem imageItem : imagesList){
                String remotePath = blackBoardImageMap.get(imageItem.getImagePath());
                if("error".equals(remotePath) || "".equals(remotePath)){ continue; } //忽略掉上传又拍云失败的图片
                Card card = new Card();
                card.setCardType(HDBlackboardCard.CardType.IMAGE);
                card.setData(HDBlackboardCard.CardType.IMAGE, remotePath);
                cards.add(card);
            }

            final long lastCardId = currCard == null ? 0 : currCard.getCardId();
            blackBoardService.sendBlackBoard(lastCardId, cards, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (BlackBoardService.OK == e) {
                        long[] ids = (long[]) data.getSerializable("card_id_list");
                        for(int i =0;i<ids.length;i++){
                            cards.get(i).setCardId(ids[i]);
                        }
                        blackboardRelativeLayoutTop.updateBlackboardUI(cards,lastCardId); //先用本地数据更新ui

                    }else{
                        Toast.makeText(getApplicationContext(), "发布失败" + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void uploadBlackboardTxt(String txt, final BlackboardRelativeLayoutTop blackboardRelativeLayoutTop) {
        final List<Card> cards = new ArrayList<Card>();
        blackboardRelativeLayoutTop.setVisibility(View.VISIBLE);
        Long insertCardId = 0l;
        final Card currCard = blackboardRelativeLayoutTop.getCurrCard();
        if (currCard != null) {
            insertCardId = currCard.getCardId();
        }

        Card card = new Card();
        card.setData(HDBlackboardCard.CardType.TEXT, txt);
        cards.add(card);

        blackBoardService.sendBlackBoard(insertCardId, cards, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (BlackBoardService.OK != e) {
                    Toast.makeText(getApplicationContext(), "发布失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void createPopupWindow(int level) {
        View menu_content = LayoutInflater.from(this).inflate(R.layout.popmenu_live, null);
        menu_content.findViewById(R.id.ll_item1).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_item2).setOnClickListener(this);
        menu_content.findViewById(R.id.ll_item3).setOnClickListener(this);

        if (level == 1 || level == 2) {//管理员或者频道主 共有属性
            menu_content.findViewById(R.id.ll_item3).setVisibility(View.VISIBLE);
//            menu_content.findViewById(R.id.ll_item2).setVisibility(View.GONE);
        }

        mPopupWindow = new PopupWindow(menu_content, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）

//        mPopupWindow.showAsDropDown(parent, 10, 0);//popupwindow 显示在相对父容器x轴0像素，y轴0像素的位置下方

        mPopupWindow.setFocusable(true);// 使其聚集
        mPopupWindow.setOutsideTouchable(true);// 设置允许在外点击消失
        mPopupWindow.update();//刷新状态
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVoicePopupWindow != null) {
            mVoicePopupWindow.dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    protected void transparentTitlebar(boolean trans) {
        if (trans) {
            getWindow().setFeatureInt(Window.FEATURE_ACTION_BAR_OVERLAY, R.layout.layout_action_bar_chat);
            getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_transparent_half)));
        } else {
            getWindow().setFeatureInt(Window.FEATURE_ACTION_BAR, R.layout.layout_action_bar_chat);
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303537")));
        }
    }

    //是否有耳机
    public boolean isHeadset() {
        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

}
