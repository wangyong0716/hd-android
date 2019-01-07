package fm.dian.hdui.view.video;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.MediaService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDChatActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.util.SysTool;
import hd.hdmedia.HDMediaModule;

/**
 * Created by lishaokai on 5/14/15.
 */

public class VideoRelativeLayout extends RelativeLayout {
    MediaService mediaService = MediaService.getInstance();
    CoreService coreService = CoreService.getInstance();
    private long room_id;
    private long user_id;
    private long live_id;

    boolean isOpen = true;
    boolean isStopPicture=false;
    boolean isBig=true;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private Context mContext;

    public GLSurfaceView player;

    public ImageView mButton;
//--------------------------------ui 控件
    private RelativeLayout rl_video_container;
    private RelativeLayout rl_video_loading;
    private OnVideoToggleListener onVideoToggleListener;
    private OnClickListener playerOnClickListener;
    private ProgressBar mProgressBar;
    private ImageView iv_user;
    private RelativeLayout localViewTool;
    private RelativeLayout remoteVideoTool;

    public VideoRelativeLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public VideoRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addOnVideoToggleListener(OnVideoToggleListener l){
        this.onVideoToggleListener = l ;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initUI();
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public void setLive_id(long live_id) {
        this.live_id = live_id;
    }

    public void initUI() {
        //if (true) return;
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = (int) ((displayMetrics.widthPixels * 1.0) / 4 * 3);
        layoutParams.width = displayMetrics.widthPixels;
        this.setLayoutParams(layoutParams);

        rl_video_container = (RelativeLayout) findViewById(R.id.rl_video_container);// 小黑板区域的麦上区域
        rl_video_loading = (RelativeLayout) findViewById(R.id.rl_video_loading);// 小黑板区域的麦上区域
        iv_user = (ImageView) rl_video_loading.findViewById(R.id.image);
        mProgressBar = (ProgressBar) rl_video_loading.findViewById(R.id.mProgressBar);

        initLocalVideoTool();

        HDMediaModule.getInstance().setRoomStatusListener(new HDMediaModule.HDRoomStatusListener() {
            @Override
            public void roomStatusChanged(String s, String s1, boolean b, boolean b1, HDMediaModule.HDEvent hdEvent) {
                if (hdEvent == HDMediaModule.HDEvent.HDEvent_FirstVideoFrame) {
                    Log.d(HongdianConstants.DEBUG_LIEYUNYE, "HDEvent_FirstVideoFrame");
                    final RelativeLayout waitingVideoProgressBar = ((RelativeLayout) findViewById(R.id.waitingVideoProgressBar));
                    waitingVideoProgressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            waitingVideoProgressBar.setVisibility(INVISIBLE);
                        }
                    });
                }
            }
        });

        playerOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBig){
                    ActionBar actionBar = ((HDChatActivity) (mContext)).getActionBar();
                    if (MediaService.getInstance().isVideoRecordOpen()){
                        if(actionBar.isShowing()){
                            actionBar.hide();
                            localViewTool.setVisibility(GONE);

                        }else{
                            actionBar.show();
                            localViewTool.setVisibility(VISIBLE);
                        }
                    }else {
                        if(actionBar.isShowing()){
                            actionBar.hide();
                            remoteVideoTool.setVisibility(GONE);

                        }else{
                            actionBar.show();
                            remoteVideoTool.setVisibility(VISIBLE);
                        }
                    }

                }else{
                    toBig(true);
                }

                Log.d(HongdianConstants.DEBUG_LIEYUNYE, "mButton.getVisibility()." + mButton.getVisibility());
            }
        };

        initRemoteVideoTool();

    }

    private void initLocalVideoTool(){
        localViewTool = (RelativeLayout)findViewById(R.id.localVideoTool);
        findViewById(R.id.flash).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HDMediaModule.getInstance().changeTorchStatus();
            }
        });

        findViewById(R.id.changeCamera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HDMediaModule.getInstance().changeCameraPosition();
            }
        });

    }

    protected void initRemoteVideoTool(){
        remoteVideoTool = (RelativeLayout)findViewById(R.id.remoteVideoTool);
        mButton = (ImageView) findViewById(R.id.iv_video_open);
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStopPicture) {
                    mButton.setImageResource(R.drawable.video_icon_stop_picture);
                    mediaService.resumePictureOfVideo();
                    isStopPicture=false;
                    rl_video_loading.setVisibility(GONE);
                }else {
                    mButton.setImageResource(R.drawable.video_icon_resume_picture);
                    mediaService.stopPictureOfVideo();
                    isStopPicture=true;
                    rl_video_loading.setVisibility(VISIBLE);
                    ((RelativeLayout)findViewById(R.id.rl_video_container)).bringChildToFront(rl_video_loading);
                    ((RelativeLayout)findViewById(R.id.rl_video_container)).bringChildToFront(remoteVideoTool);

                    if(mediaService.getVideoSpeaker() !=null){
                        coreService.fetchUserInfo(mediaService.getVideoSpeaker(), new CallBack() {
                            @Override
                            public void process(Bundle data) {
                                int e = data.getInt("error_code");
                                if (CoreService.OK == e) {
                                    User currUser = (User) data.getSerializable("user");
                                    if(currUser!=null){
                                        mProgressBar.setVisibility(GONE);
                                        HDApp.getInstance().imageLoader.displayImage(currUser.getAvatar(),iv_user);
                                    }
                                }else{
                                    Toast.makeText(mContext,"video user icon load error" ,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void stopPlayer() {

        isOpen = false;
        MediaService.getInstance().stopVideoPlay();
        if (player != null){
            ((RelativeLayout)findViewById(R.id.rl_video_container)).removeView(player);
        }
    }

    public void startPlayer() {
        Log.i(HongdianConstants.DEBUG_LIEYUNYE, "startPlayer" + live_id + " " + user_id);
        isOpen = true;

        if (player != null){
            ((RelativeLayout)findViewById(R.id.rl_video_container)).removeView(player);
        }

        player = new GLSurfaceView(mContext);
        player.setOnClickListener(playerOnClickListener);
        player.setLayoutParams(this.getLayoutParams());


        ((RelativeLayout)findViewById(R.id.rl_video_container)).addView(player);
        ((RelativeLayout)findViewById(R.id.rl_video_container)).bringChildToFront(remoteVideoTool);

        localViewTool.setVisibility(INVISIBLE);
        remoteVideoTool.setVisibility(INVISIBLE);

        if (("" + user_id).equals(HDApp.getInstance().getCurrUser().getUserId().toString())){
            Log.d(HongdianConstants.DEBUG_LIEYUNYE, "绑定本地视频View");
//            HDVideoRecorder.getInstance().startVideoRecorder("" + live_id);
            HDMediaModule.getInstance().startVideoRecord(String.valueOf(live_id),String.valueOf(user_id));
            HDMediaModule.getInstance().bindPreview((SurfaceView) findViewById(R.id.preview), player, 4 / 3.0f);
            ((RelativeLayout)findViewById(R.id.rl_video_container)).bringChildToFront(localViewTool);
            localViewTool.setVisibility(VISIBLE);
        } else {
            Log.d(HongdianConstants.DEBUG_LIEYUNYE, "绑定远程视频View");
//            HDVideoPlayer.getInstance().stopVideoPlay();
//            HDVideoPlayer.getInstance().startVideoPlay("" + live_id, "" + user_id);
            mediaService.startVideoPlay(user_id);
            HDMediaModule.getInstance().bindViewToUserId("" + user_id, player, 4 / 3.0f);
            remoteVideoTool.setVisibility(VISIBLE);
        }

        RelativeLayout waitingVideoProgressBar = ((RelativeLayout)findViewById(R.id.waitingVideoProgressBar));
        ((RelativeLayout) findViewById(R.id.rl_video_container)).bringChildToFront(waitingVideoProgressBar);
        waitingVideoProgressBar.setVisibility(VISIBLE);
    }


    public void toBig(boolean open){
        isBig = open;

//        RelativeLayout.LayoutParams lp = (LayoutParams) rl_video_container.getLayoutParams();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(open){
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height= (int) ((displayMetrics.widthPixels * 1.0) / 4 * 3);
            lp.setMargins(0, 0, 0, 0);

            if(player != null){
                LayoutParams lp3 =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((displayMetrics.widthPixels * 1.0) / 4 * 3));
                player.setLayoutParams(lp3);
            }

            LayoutParams lp2 =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((displayMetrics.widthPixels * 1.0) / 4 * 3));
            findViewById(R.id.rl_video).setLayoutParams(lp2);

//            Toast.makeText(mContext,"video open" ,Toast.LENGTH_SHORT).show();
            rl_video_container.setOnClickListener(null);
            findViewById(R.id.flash).setVisibility(View.VISIBLE);
            findViewById(R.id.changeCamera).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_video_open).setVisibility(View.VISIBLE);
        }else{
            int w =SysTool.convertDipToPx(mContext, 80);
            lp.height= (int)((w *1.0)*3/4);
            lp.width = w;
            lp.setMargins(0,0,20,20);

            LayoutParams lp2 =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rl_video_container.getHeight());
            findViewById(R.id.rl_video).setLayoutParams(lp2);

            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

            rl_video_container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toBig(true);
                }
            });
            findViewById(R.id.flash).setVisibility(View.GONE);
            findViewById(R.id.changeCamera).setVisibility(View.GONE);
            findViewById(R.id.iv_video_open).setVisibility(View.GONE);
        }

        rl_video_container.setLayoutParams(lp);

        if(open){
            onVideoToggleListener.toBig();
        }else{
            onVideoToggleListener.toSmall();
        }
    }

    public interface OnVideoToggleListener{
        public void toBig();
        public void toSmall();
    }
}
