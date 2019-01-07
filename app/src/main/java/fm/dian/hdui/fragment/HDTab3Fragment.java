package fm.dian.hdui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fm.dian.hddata.HDData;
import fm.dian.hddata.business.model.HDUser;
import fm.dian.hddata.business.service.core.user.HDUserCache;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity.BaseViewPagerListener;
import fm.dian.hdui.activity.HDSettingActivity;
import fm.dian.hdui.activity.HDUserEditActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.fragment.HDTab2Fragment.StateChageType;
import fm.dian.hdui.view.RoundAngleImageView;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

@SuppressLint("InflateParams")
public class HDTab3Fragment extends HDBaseFragment implements BaseViewPagerListener {
    PullRefreshLayout layout;
    private RoundAngleImageView iv_user_icon;
    private TextView tv_user_name;
    private TextView tv_user_des;

    private CoreService service = CoreService.getInstance();
    private User userLocal;
    private long userId;

    private static final int REQUEST_CODE_EDIT_USER = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab3, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupView();
        checkBaseHandlerEnable();
    }

    private void setupView() {
        userId = new HDUserCache().getLoginUser().userId;
        getView().findViewById(R.id.rl_user).setOnClickListener(this);
        getView().findViewById(R.id.rl_item1).setOnClickListener(this);
        iv_user_icon = (RoundAngleImageView) getView().findViewById(R.id.iv_user_icon);
        tv_user_name = (TextView) getView().findViewById(R.id.tv_user_name);
        tv_user_des = (TextView) getView().findViewById(R.id.tv_user_des);

        layout = (PullRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUi();
            }
        });
        updateUi();
    }

    private void cancelRefrash() {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_user:
                Intent intentEditUser = new Intent(getActivity(), HDUserEditActivity.class);
                //startActivity(intentEditUser);
                startActivityForResult(intentEditUser, REQUEST_CODE_EDIT_USER);
                break;
            case R.id.rl_item1:
                Intent intentSetting = new Intent(getActivity(), HDSettingActivity.class);
                startActivity(intentSetting);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == REQUEST_CODE_EDIT_USER && resultCode == Activity.RESULT_OK) {
            updateUi();
        }
    }

    private void updateUi() {
//        service.fetchUserInfo(userId, new CallBack() {
//            @Override
//            public void process(Bundle data) {
//                int error = data.getInt("error_code");
//                if (error == CoreService.OK) {
//                    userLocal = (User) data.getSerializable("user");
//                    Log.i("AAA","HDTab3Fragment->setCurrUser");
//                    HDApp.getInstance().setCurrUser(userLocal);
//                    if (null != userLocal) {
//                        HDApp.getInstance().imageLoader.displayImage(userLocal.getAvatar() + HDApp.smallIcon, iv_user_icon);
//                        tv_user_name.setText(userLocal.getNickname());
//                        if (!"NONE".equals(userLocal.getSignature())) {
//                            tv_user_des.setText(userLocal.getSignature());
//                        }
//                    }
//                }
//            }
//        });

        userLocal = HDApp.getInstance().getCurrUser();
        if (null != userLocal) {
            HDApp.getInstance().imageLoader.displayImage(userLocal.getAvatar() + HDApp.smallIcon, iv_user_icon);
            tv_user_name.setText(userLocal.getNickname());
            if (!"NONE".equals(userLocal.getSignature())) {
                tv_user_des.setText(userLocal.getSignature());
            }
        }

        cancelRefrash();
    }

    public void uploadUserMsg(User newUser) {
        service.updateUserInfo(newUser, new CallBack() {
            @Override
            public void process(Bundle data) {
                updateUi();
            }
        });

    }

    @Override
    public void onCurrPage() {
        HDUser user = new HDData().getLoginUser();
        if (user != null) {
            updateUi();
        }
    }


    public void onUserChage(StateChageType type, long userId) {
    }
}
