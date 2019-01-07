package fm.dian.hdui.app;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by song on 2015/5/11.
 */
public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;
    private  ActivityManager(){
    }
    public static ActivityManager getInstance(){
        if(instance==null){
            instance=new ActivityManager();
        }
        return instance;
    }
    //退出栈顶Activity
    public void popActivity(Activity activity){
        if(activity!=null){
            activity.finish();
            activityStack.remove(activity);
            activity=null;
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    //退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls){
        while(true){
            Activity activity=currentActivity();
            if(activity==null){
                break;
            }
            if(activity.getClass().equals(cls) ){
                break;
            }
            popActivity(activity);
        }
    }
}