package fm.dian.hdui.view.blackboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fm.dian.hdservice.BlackBoardService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Card;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDChatBlackboardImageAdapter;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.view.CommonDialog.DialogClickListener;
import fm.dian.hdui.view.CommonMenuDialog;
import fm.dian.hdui.view.DotsIndicator;
import fm.dian.hdui.view.MyGallery;
import fm.dian.hdui.wximage.clip.utils.ScaleImageView;
import fm.dian.hdui.wximage.clip.utils.ScaleImageView.onClickCallBackListener;
import fm.dian.service.blackboard.HDBlackboardCard.CardType;

/**
 * ******************
 * fileName  :BlackboardRelativeLayoutTop.java
 * author    : song
 * createTime:2015-3-19 下午2:10:57
 * fileDes   : 小黑板展示区域
 */
@SuppressLint("ClickableViewAccessibility")
@SuppressWarnings("deprecation")
public class BlackboardRelativeLayoutTop extends RelativeLayout {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private MediaService mediaService = MediaService.getInstance();
    public boolean isBig = false;
    private Context mContext;
    private MyGallery mGallery;
    private DotsIndicator dotsIndicator;
    private ImageView iv_dot_left_arrow;
    private ImageView iv_dot_right_arrow;
    private HDChatBlackboardImageAdapter blackboardAdapter;
    private RelativeLayout rl_blackboard_mic;
    private ImageView iv_blackboard_zhezhao;
    private TextView tv_prompt;
    private ProgressBar progressBar;
    private boolean isCanSliding = false;
    // 舞台区
    private StageRelativeLayout hs;
    OnItemClickListener galleryItemClickListener;
    OnClickListener zhezhaoOnclickListener;
    // 键盘监听(监听表情区域，小黑板和录制区域，还有键盘)
    private KeyboardListener keyboardListener;
    OnBlackboardToggleListener onBlackboardToggleListener;
    //分页圆点箭头
    int lastPage = 0;
    int lastAllSize = 0;
    int mExitCounter = 0;//文字小黑板双击退出
//    ---------------ui 控件
    private RelativeLayout rl_blackboard_container;

    BlackBoardService blackBoardService = BlackBoardService.getInstance();

    public BlackboardRelativeLayoutTop(Context context) {
        super(context);
        this.mContext = context;
    }

    public BlackboardRelativeLayoutTop(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public BlackboardRelativeLayoutTop(Context context, AttributeSet attrs,
                                       int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public void setStageBar(StageRelativeLayout hs) {
        this.hs = hs;
    }

    public void addKeyboardListener(KeyboardListener l) {
        this.keyboardListener = l;
    }
    public void addOnBlackboardToggleListener(OnBlackboardToggleListener l) {
        this.onBlackboardToggleListener = l;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initUI();
    }

//    public void refresh() {
//        List<Card> cards = blackboardAdapter.getData();
//        int tt = dotsIndicator.getVisibility();
//
//        dotsIndicator.setVisibility(View.VISIBLE);
//        iv_blackboard_zhezhao.setVisibility(View.GONE);
//
//        if (cards.size() > 0) updateBlackboardUI(cards, cards.get(0).getCardId(), true);
//
//    }

    private void setDotsAndArrowVisiable(int visiable) {
        findViewById(R.id.rl_dots_and_arrow).setVisibility(visiable);
    }

    private void slidingGallery(int position) {
        slidingGallery(position, false);
    }

    private void slidingGallery(int position, boolean force) {
        int allSize = blackboardAdapter.getData().size();

        int currPage = (position) / DotsIndicator.pageMaxCount;
        if (dotsIndicator.getDotsCount() == 0 || allSize != lastAllSize || force) {//避开首次没有设置圆点数量 或者 小黑板数量减少了
            if (currPage < (allSize / DotsIndicator.pageMaxCount)) {
                dotsIndicator.setupDots(DotsIndicator.pageMaxCount);
            } else {
                int cPageCount = allSize % DotsIndicator.pageMaxCount;
                dotsIndicator.setupDots(cPageCount);
            }
        }

        if (currPage == lastPage) {
            int cIndex = (position) % DotsIndicator.pageMaxCount;

            dotsIndicator.setFocuseByIndex(cIndex);

        } else {//翻页
            int currIndex = (position) % DotsIndicator.pageMaxCount;
            int currPageCount = allSize % DotsIndicator.pageMaxCount;

            if ((allSize / DotsIndicator.pageMaxCount) > currPage) {//非第一页和最后一页

                dotsIndicator.setupDots(DotsIndicator.pageMaxCount, currIndex);
            } else {//最后一页/第一页
                dotsIndicator.setupDots(currPageCount, currIndex);
            }
            lastPage = currPage;
        }

        //设置箭头
        if (allSize <= DotsIndicator.pageMaxCount) {//只有一页
            setArrow(false, false);
        } else {//大于1页
            if (currPage == 0) {//第一页
                setArrow(false, true);
            } else {//非第一页
                iv_dot_left_arrow.setVisibility(View.VISIBLE);

                if (currPage == allSize / DotsIndicator.pageMaxCount - 1 + (allSize % DotsIndicator.pageMaxCount > 0 ? 1 : 0)) {//最后一页
                    setArrow(iv_dot_left_arrow.getVisibility() == View.VISIBLE, false);
                } else {//中间页
                    setArrow(iv_dot_left_arrow.getVisibility() == View.VISIBLE, true);
                }
            }
        }
        lastAllSize = allSize;
    }

    private void initUI() {
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = (int) ((displayMetrics.widthPixels * 1.0) / 4 * 3);
        layoutParams.width = displayMetrics.widthPixels;
        this.setLayoutParams(layoutParams);

        rl_blackboard_container = (RelativeLayout) findViewById(R.id.rl_blackboard_container);// 小黑板区域的麦上区域


        rl_blackboard_mic = (RelativeLayout) findViewById(R.id.rl_blackboard_mic);// 小黑板区域的麦上区域
        rl_blackboard_mic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toBig(false);
                hs.changeHeight(true);
            }
        });
        mGallery = (MyGallery) findViewById(R.id.mGallery);
        blackboardAdapter = new HDChatBlackboardImageAdapter(mContext);

        dotsIndicator = (DotsIndicator) findViewById(R.id.dotsIndicator);
        iv_dot_left_arrow = (ImageView) findViewById(R.id.iv_dot_left_arrow);
        iv_dot_right_arrow = (ImageView) findViewById(R.id.iv_dot_right_arrow);
        mGallery.setAdapter(blackboardAdapter);
        mGallery.addOnFlipingListener(new MyGallery.OnFlipingListener() {
            @Override
            public void onFliping() {
                //延迟0.5s执行的目的是取得正确的position,如果有更好的办法请改之
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int position = getCurrIndex();
                        if (dotsIndicator != null) {
                            slidBlackBoard();//上传滑动数据
                            slidingGallery(position);//更新ui
                        } else {
                            dotsIndicator.setupDots(blackboardAdapter.getData().size(),
                                    position);
                        }
                    }
                }, 500);

            }
        });


        galleryItemClickListener =  new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (keyboardListener != null && keyboardListener.isKeyboardShowed2Closed()) {
                    //收起软键盘以后的逻辑
                } else if (keyboardListener != null && keyboardListener.isFaceShowed2Closed()) {
                    //收起表情以后的逻辑
                } else {
                    new CommonMenuDialog(mContext, "小黑板操作", new CommonMenuDialog.DialogClickListener() {
                        @Override
                        public void onItemClick(int functionIndex) {
                            if (functionIndex == 0) {//菜单功能1
                                showBlackboardDialog(mContext, mGallery);
                            } else if (functionIndex == 1) {
                                Card card = (Card) blackboardAdapter.getItem(getCurrIndex());
                                delBlackBoardData(card.getCardId());
                            } else if (functionIndex == 2) {
                                closeBlackBoardData();
                            }
                        }
                    }, "全屏", "删除", "关闭小黑板");
                }
            }
        };
        mGallery.setOnItemClickListener(galleryItemClickListener);

        iv_blackboard_zhezhao = (ImageView) findViewById(R.id.iv_blackboard_zhezhao);
        tv_prompt = (TextView) findViewById(R.id.tv_prompt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        zhezhaoOnclickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = mGallery.getSelectedItemPosition();
                if(index >= 0){
                    Card card = blackboardAdapter.getData().get(index);
                    showBlackboardDialog(mContext, mGallery);
                }
            }
        };

    }

    public void nobodyOnMic() {
        if (getCurrCards() != null && getCurrCards().size() > 0) {// 麦上没人的时候小黑板在前边最大化
//            blackboardFront();
            toBig(true);
        }
    }

    private void setArrow(boolean leftArrow, boolean rightArrow) {

        iv_dot_left_arrow. setVisibility((leftArrow == true) ? View.VISIBLE : View.GONE);
        iv_dot_right_arrow.setVisibility((rightArrow == true)? View.VISIBLE: View.GONE);
    }

    private void showMicIcon(boolean show){
        if(show){
            rl_blackboard_mic.setVisibility(VISIBLE);
        }else{
            rl_blackboard_mic.setVisibility(GONE);
        }
    }


    public void toBig(boolean open){
        isBig = open;

        RelativeLayout.LayoutParams lp = (LayoutParams) rl_blackboard_container.getLayoutParams();
        if(open){
            lp.height= LayoutParams.MATCH_PARENT;
            lp.width = LayoutParams.MATCH_PARENT;
            lp.setMargins(0,0,0,0);

            setCanSliding(getCanSliding());
            iv_blackboard_zhezhao.setOnClickListener(zhezhaoOnclickListener);
            mGallery.setOnItemClickListener(galleryItemClickListener);
            blackboardAdapter.blackboardChangeSmall(false);
            showMicIcon(true);
            if(getCanSliding()){
                setDotsAndArrowVisiable(VISIBLE);
            }

        }else{
            int w =SysTool.convertDipToPx(mContext,80);
            lp.height= (int)((w *1.0)*3/4);
            lp.width = w;
            lp.setMargins(0, 0, 20, 20);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

//            tempEnableSlidingToSmall(false);
            iv_blackboard_zhezhao.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    toBig(true);
                }
            });
            mGallery.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    toBig(true);
                }
            });
            showMicIcon(false);
            blackboardAdapter.blackboardChangeSmall(true);
            setDotsAndArrowVisiable(View.GONE);

        }

        rl_blackboard_container.setLayoutParams(lp);

        if(open){
            onBlackboardToggleListener.toBig();
        }else{
            onBlackboardToggleListener.toSmall();
        }
    }

    public List<Card> getCurrCards() {
        return blackboardAdapter.getData();
    }

    public void updateBlackboardUI(List<Card> cards, long currCardId) {
        updateBlackboardUI(cards, currCardId, false);
    }

    private void updateBlackboardUI(List<Card> cards, long currCardId, boolean force) {

        List<Card> currList = getCurrCards();
        if (cards == null || currList == null) {
            return;
        }
        // 判断数据有误变化
        boolean isChange = false;
        for (int i = 0; i < currList.size(); i++) {
            Card curr = currList.get(i);
            boolean isHave = false;
            for (Card card : cards) {
                if (curr.getCardId() == card.getCardId()) {
                    isHave = true;
                }
            }
            if (!isHave) {
                isChange = true;
            }
        }
        if ((cards.size() != currList.size()) || isChange) {
            blackboardAdapter.resetData(cards);
            blackboardAdapter.notifyDataSetChanged();
        }

        // 切换当前小黑板图片
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i)!=null && null != cards.get(i).getCardId() && cards.get(i).getCardId() == currCardId) {
//				mGallery.setSelection(i);
//				dotsIndicator.setupDots(blackboardAdapter.getData().size());
//				dotsIndicator.setFocuseByIndex(i);
                slidingGallery(i, force);
                mGallery.setSelection(i);
            }
        }
    }

    // 小黑板 大图展示 单击关闭 ,双击放大 ,缩放 ,长按保存
    @SuppressLint("InflateParams")
    public void showBlackboardDialog(final Context mContext, View parrentView) {
        if(! isBig){//小窗口时 直接切换而不是显示菜单
            toBig(true);
            return ;
        }

        final Card card = blackboardAdapter.getData().get(getCurrIndex());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View alertContentView = inflater.inflate(R.layout.item_blackboard_image_adapter_fullscreen, null);
        final PopupWindow pop = new PopupWindow(alertContentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, false);
        final ScaleImageView iv = (ScaleImageView) alertContentView.findViewById(R.id.iv);
        final TextView tv = (TextView) alertContentView.findViewById(R.id.tv);
        final ScrollView sv_txt = (ScrollView) alertContentView.findViewById(R.id.sv_txt);

        if (card.getCardType() == CardType.IMAGE) {// 图片
            String imageUrl = card.getData();
            if (imageUrl != null) {
                try {
                    HDApp.getInstance().imageLoader.displayImage(imageUrl, iv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            iv.addOnclickListener(new onClickCallBackListener() {

                @Override
                public void onClick() {
                    pop.dismiss();
                }

                @Override
                public void longClick() {
                    new CommonDialog(mContext, ButtonType.oneButton, new DialogClickListener() {
                        public void doPositiveClick() {

                            String imageUrl = card.getData();
                            if (imageUrl != null) {
                                HDApp.getInstance().imageLoader.loadImage(imageUrl, new ImageLoadingListener() {
                                    public void onLoadingCancelled(String arg0, View arg1) {
                                    }

                                    @Override
                                    public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
                                        final File file = new File(mContext.getCacheDir(), System.currentTimeMillis() + ".png");
                                        boolean saveBmpToFile = SysTool.saveBmpToFile(bitmap, file);
                                        if (saveBmpToFile) {
                                            Toast.makeText(mContext, "保存成功 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mContext, "保存图片失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                    }

                                    public void onLoadingStarted(String arg0, View arg1) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void doNegativeClick() {
                        }
                    }, null, null, "保存图片", null);
                }
            });
            iv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            sv_txt.setVisibility(View.GONE);
        } else if (card.getCardType() == CardType.TEXT) {// 文本
            String txt = card.getData();
            txt = SysTool.ToDBC(txt);
            txt = SysTool.stringFilter(txt);
            tv.setText(txt);
            if (txt.length() < 20) {
                tv.setGravity(Gravity.CENTER);
            }

            mExitCounter=0;
            sv_txt.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            mExitCounter++;

                            if (mExitCounter == 1) {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mExitCounter = 0;
                                    }
                                }, 300);
                                return true;
                            }

                            pop.dismiss();
                            break;
                    }
                    return false;
                }
            });
            iv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            sv_txt.setVisibility(View.VISIBLE);
        }

        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.popwin_anim_style);
        pop.showAtLocation(parrentView, Gravity.CENTER, 0, 0);
    }

    // 更新小黑板
    public void loadBlackBoardData(List<Long> cardIds, final Long currCardId) {
        if (cardIds !=null && cardIds.size() != blackboardAdapter.getCount()) {//避免每次滑动都下载所有小黑板图片
            long[] ids = new long[cardIds.size()];
            for (int i = 0; i < cardIds.size(); i++) {
                if(null != cardIds.get(i)){
                    ids[i]=cardIds.get(i);
                }
            }

            blackBoardService.fetchBlackBoardList(ids, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (BlackBoardService.OK == e) {
                        ArrayList<Card> cards = (ArrayList<Card>) data.getSerializable("cards");

                        updateBlackboardUI(cards, currCardId);//获取数据，更新ui
                    }
                }
            });
        } else { // 切换当前小黑板图片
            for (int i = 0; i < cardIds.size(); i++) {
                if (currCardId.equals(cardIds.get(i))) {
                    slidingGallery(i);
                    mGallery.setSelection(i);
                }
            }
        }

    }

    private void delBlackBoardData(long cardId) {
        long[] cards = new long[1];
        cards[0] = cardId;
        blackBoardService.deleteBlackBoardList(cards, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (BlackBoardService.OK == e) {
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "删除失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeBlackBoardData() {
        blackBoardService.closeBlackBoardList(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (BlackBoardService.OK == e) {
                    blackboardAdapter.resetData(new ArrayList<Card>());
                    blackboardAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "关闭失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void slidBlackBoard() {
        Card currCard = getCurrCard();
        blackBoardService.changeBlackBoardList(currCard.getCardId(), new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (BlackBoardService.OK != e) {
//                    Toast.makeText(mContext, "操作失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setCanSliding(boolean isCanSliding) {
        this.isCanSliding = isCanSliding;
        if (isCanSliding) {
            iv_blackboard_zhezhao.setOnClickListener(null);
            iv_blackboard_zhezhao.setVisibility(View.GONE);
            if(isBig){
                setDotsAndArrowVisiable(VISIBLE);
            }
        } else {
            iv_blackboard_zhezhao.setVisibility(VISIBLE);
            iv_blackboard_zhezhao.setOnClickListener(zhezhaoOnclickListener);
            setDotsAndArrowVisiable(GONE);
        }
    }

    public boolean getCanSliding() {
        return this.isCanSliding;
    }

    public int getCurrIndex() {
        return mGallery.getSelectedItemPosition();
    }

    public Card getCurrCard() {
        if (blackboardAdapter.getData().size() == 0) {
            return null;
        }
        return blackboardAdapter.getData().get(getCurrIndex());
    }

    //显示小黑板上传进度条
    public void showProgressBar() {
        findViewById(R.id.rl_blackboard_progressbar).setBackgroundResource(R.color.chat_room_gl_stage_bg_color);
        tv_prompt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    //隐藏小黑板上传进度条
    public void dismissProgressBar() {
        findViewById(R.id.rl_blackboard_progressbar).setBackgroundResource(R.color.color_transparency);
        tv_prompt.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    //更新小黑板上传进度条
    public void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
    }
    public int getProgress(){
        return  progressBar.getProgress();
    }

    // 回调接口(监听表情，软键盘，小黑板和录制面板，是否在展开状态)
    public interface KeyboardListener {
        public boolean isKeyboardShowed2Closed();//如果键盘是显示状态就收起键盘并返回true

        public boolean isFaceShowed2Closed();//如果表情区域是显示状态就收起表情区域并返回true

        public boolean isBlackboardPanelShowed2Closed();//如果小黑板和录制区域在显示状态就收起并返回true
    }

    public interface OnBlackboardToggleListener{
        public void toBig();
        public void toSmall();
    }
}
