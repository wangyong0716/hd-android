package fm.dian.hdui.view.blackboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdui.R;
import fm.dian.hdui.util.DateUtil;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.CommonDialog.ButtonType;
import fm.dian.hdui.view.CommonDialog.DialogClickListener;
import fm.dian.hdui.view.CommonInputDialog;

/**
 * ******************
 * fileName  :BlackboardRelativeLayoutTop.java
 * author    : song
 * createTime:2015-3-19 下午2:10:57
 * fileDes   : 录制展示区域
 */
public class RecordingRelativeLayout extends RelativeLayout {
    private Context mContext;
    private TextView tv_time;

    private long startTime;
    @SuppressWarnings("unused")
    private long stopTime;
    HistoryService historyService = HistoryService.getInstance();

    public RecordingRelativeLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public RecordingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public RecordingRelativeLayout(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initUI();
    }

    private void initUI() {
        tv_time = (TextView) findViewById(R.id.tv_time);// 小黑板区域的麦上区域


    }

    public void stop(final long history_id) {
        RecordingRelativeLayout.this.setVisibility(View.GONE);
        stopTime = System.currentTimeMillis();
        new CommonDialog(mContext, ButtonType.TowButton, new DialogClickListener() {//发布选择
            @Override
            public void doPositiveClick() {
                new CommonInputDialog(mContext, CommonInputDialog.ButtonType.TowButton, new CommonInputDialog.DialogClickListener() {//输入名字

                    @Override
                    public void doPositiveClick(String input) {//以 input 为名字发布
                        historyService.updateHistory(history_id, input, true, false, new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int e = data.getInt("error_code");
                                if (HistoryService.OK == e) {
                                    Toast.makeText(mContext, "发布成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void doNegativeClick() {
                    }
                }, "");
            }

            @Override
            public void doNegativeClick() {
            }

        }, "已保存至“历史”中", "稍后发布", "立即发布", "录制完成");
    }

    public void start(long timestamp) {
        this.setVisibility(View.VISIBLE);
        startTime = System.currentTimeMillis();
        if(timestamp!=0){
            startTime=startTime-timestamp;
        }
        updateTime();
    }

    private void updateTime() {
        String during = DateUtil.formatDuring(new Date(startTime), new Date(System.currentTimeMillis()));
        tv_time.setText(during);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                updateTime();
            }
        }, 1000);

    }

    public interface BlackBoardLinearlayoutListner {
        public void startRecording();

        public void onBlackBoard();
    }

}
