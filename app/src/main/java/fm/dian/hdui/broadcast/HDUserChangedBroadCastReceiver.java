package fm.dian.hdui.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.view.CommonDialog;

public class HDUserChangedBroadCastReceiver extends BroadcastReceiver {

    public static final String INTENT_FILTER_ACTION = "fm.dian.user.loginRepeat";

    private static final String USER_ID = "userId";
    private boolean isShowing=false;
    CommonDialog dialog;

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(INTENT_FILTER_ACTION)) {
//            long userId = intent.getLongExtra(USER_ID, 0);
//            Toast.makeText(context,userId+"",Toast.LENGTH_SHORT).show();
            if(!isShowing){
                isShowing=true;
                dialog =new CommonDialog(context.getApplicationContext(), CommonDialog.ButtonType.oneButton, new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {
                        HDBaseTabFragmentActivity.self.appLogout();
                        ((Activity)context).finish();
                        isShowing=false;
                    }

                    public void doNegativeClick() {
                        isShowing=false;
                    }
                }, "你的账号在其他设备上登录，请重新登录");
                dialog.setTouchOutsideAndCalceled(false,false);
                dialog.setAlertType();
            }
        }
    }

    public void dismissDialog(){
        if(isShowing){
            dialog.dismissDialog();
        }
    }

}
