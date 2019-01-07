package fm.dian.hdui.wximage.choose;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.wximage.clip.ClipActivity;

public class TakePictureActivity extends HDBaseActivity {
    private static final int CAMERA_WITH_DATA = 3023; // 用来标识请求照相功能的activity
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    public void initUI() {
        try {
            mCurrentPhotoFile = new File(HDApp.getInstance().getCacheDir(), HongdianConstants.TEMP_TACK_PHOTO_NAME);//此处如果设置为程序缓存，那么容易获取不懂图片

            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "相机不可用", Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            TakePictureActivity.this.finish();
            return;
        }

        switch (requestCode) {
            case CAMERA_WITH_DATA:
                startClipAct();
                break;
        }
    }

    private void startClipAct() {
        File f = new File(HDApp.getInstance().getCacheDir(), HongdianConstants.TEMP_TACK_PHOTO_NAME);
        if (f.exists()) {
            String path = f.getAbsolutePath();
            Intent clipIntent = new Intent(TakePictureActivity.this, ClipActivity.class);
            clipIntent.putExtra("path", path);
            TakePictureActivity.this.startActivity(clipIntent);
            TakePictureActivity.this.finish();
        } else {
            Toast.makeText(this, "照片文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
