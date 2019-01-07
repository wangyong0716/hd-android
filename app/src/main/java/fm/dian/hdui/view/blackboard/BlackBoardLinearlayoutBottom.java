package fm.dian.hdui.view.blackboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdui.R;
import fm.dian.hdui.view.CommonDialog;

/**
 * ******************
 * fileName  :BlackBoardLinearlayoutBottom.java
 * author    : song
 * createTime:2015-3-6 下午2:10:57
 * fileDes   : 小黑板和录制面板
 */
public class BlackBoardLinearlayoutBottom extends LinearLayout implements OnClickListener {
    private Context mContext;
    private BlackBoardLinearlayoutListner listener;
    private View faceRootView;
    private TextView tv_item_recording;
    private HistoryService historyService = HistoryService.getInstance();
    private  View enter;
    public BlackBoardLinearlayoutBottom(Context context) {
        super(context);
        this.mContext = context;
    }

    public BlackBoardLinearlayoutBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public BlackBoardLinearlayoutBottom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public void setEnterButtonAndListener(View enter, BlackBoardLinearlayoutListner listener) {
        this.listener = listener;
        enter.setOnClickListener(this);
        this.enter=enter;
        initUI();
    }

    public void setFaceRootView(View v) {
        this.faceRootView = v;
    }

    public void setupRecordingState(boolean start){
        if(start){
            tv_item_recording.setTag("recording");
            tv_item_recording.setText("结束录制");
        }else{
            tv_item_recording.setTag("");
            tv_item_recording.setText("录制");
        }
    }

    public void disableFaceBtn(boolean silence) {
        if(silence){//禁言状态不可选择表情,不可发小黑板
            enter.setOnClickListener(null);
        }else{
            enter.setOnClickListener(this);
        }
    }

    private void initUI() {
        this.findViewById(R.id.tv_item_blackboard).setOnClickListener(this);
        boolean openBlackBoard = ConfigService.getInstance().getOpenBlackBoard();
        if( !openBlackBoard ){
            this.findViewById(R.id.tv_item_blackboard).setVisibility(GONE);
        }
        tv_item_recording = (TextView) this.findViewById(R.id.tv_item_recording);
        tv_item_recording.setOnClickListener(this);
    }


    public interface BlackBoardLinearlayoutListner {
        public void startRecording();

        public void stopRecording(long id);

        public void onBlackBoard();
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard() {
        // 收起软键盘
        InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null != ((Activity) mContext).getCurrentFocus()){
            im.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void hideBlackBoard() {
        this.setVisibility(View.GONE);
    }


    private boolean flag = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_blackboard:
                //展开小黑板模块
                if (flag) {
                    this.setVisibility(View.VISIBLE);
                } else {
                    this.setVisibility(View.GONE);
                }
                flag = !flag;
                hideKeyBoard();
                faceRootView.setVisibility(View.GONE);
                break;
            case R.id.tv_item_recording:
                if (listener != null) {
                    if("recording".equals(tv_item_recording.getTag())){
                        stopRecordUi();
                    }else{
                        tv_item_recording.setText("结束录制");
                        listener.startRecording();
                        setupRecordingState(true);
                    }
                }
                hideBlackBoard();
                break;
            case R.id.tv_item_blackboard:
                if(isVideoRecording()){
                    videoRecordingCannotPublishBlackboard();
                    this.setVisibility(GONE);
                    return;
                }
                if (listener != null) {
                    listener.onBlackBoard();
                }
                break;

            default:
                break;
        }
    }

    private boolean isVideoRecording(){
        boolean isVideoRecord = MediaService.getInstance().isVideoRecordOpen(); //视频直播
        return  isVideoRecord;
    }
    private void videoRecordingCannotPublishBlackboard(){
        new CommonDialog(mContext, CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
            public void doPositiveClick() {}

            public void doNegativeClick() {}
        }, "暂不支持视频直播中发布小黑板");
    }

    private void stopRecordUi(){
        new CommonDialog(mContext, CommonDialog.ButtonType.TowButton, new CommonDialog.DialogClickListener() {

            @Override
            public void doPositiveClick() {//结束录制
                historyService.stopRecord(new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (HistoryService.OK == e) {
                            long history_id = data.getLong("history_id");
                            listener.stopRecording(history_id);
                            setupRecordingState(false);
                            tv_item_recording.setText("录制");
                            setupRecordingState(false);
                        }
                    }
                });
            }

            @Override
            public void doNegativeClick() {
            }
        }, "确认结束录制？");
    }

}
