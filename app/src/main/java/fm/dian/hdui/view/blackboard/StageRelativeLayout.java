package fm.dian.hdui.view.blackboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.util.Logger;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDUserActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.SysTool;

/**
 * ******************
 * fileName  :StageHorizontalScrollView.java
 * author    : song
 * createTime:2015-3-18 下午6:37:21
 * fileDes   :
 */
public class StageRelativeLayout extends RelativeLayout {
    private Context mContext;
    private LinearLayout ll_stage;
    private TextView tv_nobody;

    private Logger log = Logger.getLogger(StageRelativeLayout.class);

    Map<Long, View> stageViews = new HashMap<Long, View>();

    CoreService coreService = CoreService.getInstance();
    AuthService authService = AuthService.getInstance();

    public StageRelativeLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public StageRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public StageRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        initUI();
    }

    private void initUI() {
        ll_stage = (LinearLayout) this.findViewById(R.id.ll_stage);
        tv_nobody = (TextView) this.findViewById(R.id.tv_nobody);
    }

    public synchronized void updateUsers(List<Long> userIds) {

        Set<Long> add = new HashSet<>(userIds);
        add.removeAll(stageViews.keySet());
        Set<Long> remove = new HashSet<>(stageViews.keySet());
        remove.removeAll(userIds);

        log.debug("userIds={},add={},remove={},old={}", userIds.size(), add.size(), remove.size(), stageViews.size());

        for (Long id : remove) {
            View view = stageViews.get(id);
            ll_stage.removeView(view);
            stageViews.remove(id);
        }

        for (Long id : add) {
            coreService.fetchUserInfo(id, new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (CoreService.OK == e) {
                        final User currUser = (User) data.getSerializable("user");
                        if (currUser != null) {
                            View contentView = View.inflate(mContext, R.layout.item_chat_activity_stage_image_adapter, null);
                            ImageView imageView = (ImageView) contentView.findViewById(R.id.image);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, HDUserActivity.class);
                                    intent.putExtra(HDUserActivity.USER_ID, currUser.getUserId());
                                    intent.putExtra(HDUserActivity.ROOM_ID, authService.getLiveId());
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                                }
                            });

                            HDApp.getInstance().imageLoader.displayImage(currUser.getAvatar() + HDApp.smallIcon, imageView);

                            if (!stageViews.containsKey(currUser.getUserId())) {
                                tv_nobody.setVisibility(GONE);
                                ll_stage.addView(contentView);
                                stageViews.put(currUser.getUserId(), contentView);
                            }
                        }
                    }
                }
            });
        }

    }


    @SuppressWarnings("deprecation")
    public void changeHeight(boolean isHeight) {
        int screenWidth = SysTool.getScreenWidth(mContext);
        int height = (int) ((screenWidth * 1.0) / 4 * 3);//小黑板比例4:3

        int h = isHeight ? height : SysTool.convertDipToPx(mContext, 130);

        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.height = h;
        this.setLayoutParams(layoutParams);
    }

    public void nobody() {
        stageViews.clear();
        tv_nobody.setVisibility(VISIBLE);
        ll_stage.removeAllViews();
    }
}
