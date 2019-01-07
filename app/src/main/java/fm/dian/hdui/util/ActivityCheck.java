package fm.dian.hdui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import fm.dian.hddata.util.HDContext;
import fm.dian.hddata.util.HDLog;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDNavigationActivity;
import fm.dian.hdui.activity.HDSplashActivity;

public class ActivityCheck {

    private HDLog log = new HDLog(ActivityCheck.class);

    @SuppressWarnings("rawtypes")
    public boolean checkAndGoIndex(Activity activity) {
        if (activity == null) {
            return false;
        }
        Class[] filter = new Class[]{
                HDBaseTabFragmentActivity.class,
                HDNavigationActivity.class,
                HDSplashActivity.class
        };

        if (HDBaseTabFragmentActivity.self == null) {
            System.gc();
            for (Class c : filter) {
                if (c == activity.getClass()) {
                    return false;
                }
            }

            log.e("HDBaseTabFragmentActivity.self == null");

            Intent intent = new Intent(activity, HDSplashActivity.class);
            activity.startActivity(intent);
            activity.finish();

            return true;
        }
        return false;
    }

    public void goIndex() {
        Context context = HDContext.getInstance().getContext();
        if (context != null) {
            Intent intent = new Intent(context, HDSplashActivity.class);
            context.startActivity(intent);
        }
    }
}
