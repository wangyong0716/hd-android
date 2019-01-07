package fm.dian.hdui.net;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import fm.dian.hdui.view.MyAlertDialog;

public class HDAsycTask extends AsyncTask<Void, Integer, String> {
    private MyAlertDialog myAlertDialog;
    private HDAsyncHandler mHandler;

    public HDAsycTask(Context mCtx, HDAsyncHandler mHandler) {
        super();
        this.mHandler = mHandler;
        myAlertDialog = new MyAlertDialog(mCtx, true, new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myAlertDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        mHandler.doInBackground(params);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mHandler.onPostExecute(result);
        myAlertDialog.dismiss();
    }

    public interface HDAsyncHandler {
        public String doInBackground(Void... params);

        public String onPostExecute(String result);
    }

}
