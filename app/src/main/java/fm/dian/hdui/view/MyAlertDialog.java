package fm.dian.hdui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.TextView;

import fm.dian.hdui.R;

/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2013-11-7 下午5:44:17
 */
public class MyAlertDialog extends AlertDialog implements OnCancelListener {
    Context mContext;
    TextView waitMsg;
    String msg;

    protected MyAlertDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public MyAlertDialog(Context context, boolean cancelable,
                         OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        getWindow().setBackgroundDrawableResource(R.color.color_transparency);
    }

    public MyAlertDialog(Context context, int theme, String msg) {
        super(context, theme);
        this.mContext = context;
        this.msg = msg;
    }

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dlg);
        waitMsg = (TextView) this.findViewById(R.id.tvWaitPromp);
        if (msg != null) {
            waitMsg.setText(msg);
        }
    }

    ;

    public void setPrompMsg(final String msg) {
        if (waitMsg != null) {
            new Thread() {
                public void run() {
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            waitMsg.setText(msg);

                        }
                    });
                }

                ;
            }.start();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
