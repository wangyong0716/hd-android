package fm.dian.hdui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDNavigationActivity;
import fm.dian.hdui.util.HDUiLog;
import fm.dian.hdui.view.MyAlertDialog;


public class HDBaseFragment extends Fragment implements OnClickListener {
    private final String mPageName = getClass().getSimpleName();

    public HDUiLog log = new HDUiLog();
    public ImageButton ib_chat_action_bar_right;
    private TextView tv_action_bar_title_sub;
    private View v_action_bar;
    public ImageButton ib_action_bar_left;
    private TextView tv_action_bar_title;
    public TextView tv_common_action_bar_right;
    protected MyAlertDialog myAlertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        myAlertDialog = new MyAlertDialog(getActivity(), true, new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    public void onClick(View v) {

    }


    public void checkBaseHandlerEnable() {
        if (HDBaseTabFragmentActivity.self == null) {
            Intent loginIntent = new Intent(getActivity(), HDNavigationActivity.class);
            startActivity(loginIntent);
            getActivity().finish();
        }
    }

    public void initChatActionBar(final Activity act) {
        ActionBar actionBar = act.getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflator = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v_action_bar = inflator.inflate(R.layout.layout_action_bar_chat, null);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            actionBar.setCustomView(v_action_bar, layout);

            tv_action_bar_title = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title);
            tv_action_bar_title_sub = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_title_sub);
            ib_chat_action_bar_right = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_right);
            ib_action_bar_left = (ImageButton) v_action_bar.findViewById(R.id.ib_action_bar_left);
            ib_action_bar_left.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((Activity) act).finish();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }
}