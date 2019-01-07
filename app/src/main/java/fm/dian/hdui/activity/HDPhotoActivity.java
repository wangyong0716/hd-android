package fm.dian.hdui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fm.dian.hddata.business.model.HDUser;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.LiveService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.RoomUserFollow;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.adapter.HDChannelActivityAdapter;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

public class HDPhotoActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 45;
    public static final int RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS = 3;
    public static final int RESULT_CODE_BLACKBOARD_WRITE_PHOTO_SUCCESS = 45;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        File mCurrentPhotoFile = new File(HDApp.getInstance().getCacheDir(), HongdianConstants.TEMP_TACK_PHOTO_NAME);//此处如果设置为程序缓存，那么容易获取不懂图片
        final Intent intent = getTakePickIntent(mCurrentPhotoFile);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            HDPhotoActivity.this.finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                File f = new File(HDApp.getInstance().getCacheDir(), HongdianConstants.TEMP_TACK_PHOTO_NAME);
                if (f.exists()) {
                    String path = f.getAbsolutePath();
                    Intent intent = new Intent();
                    intent.putExtra("path", path);
                    intent.putExtra("id", "path");
                    //String txt = "aiiiiiiiiii";
                    //intent.putExtra("txt", txt);
                    setResult(RESULT_CODE_BLACKBOARD_WRITE_PHOTO_SUCCESS, intent);
                    HDPhotoActivity.this.finish();
                } else {
                    Toast.makeText(this, "照片文件不存在！", Toast.LENGTH_SHORT).show();
                    return;
                }
            break;
        }
    }

    public static Intent getTakePickIntent(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }
}
