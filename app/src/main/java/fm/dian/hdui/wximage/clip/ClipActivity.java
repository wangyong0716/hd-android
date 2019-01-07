package fm.dian.hdui.wximage.clip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;

import org.json.JSONObject;

import java.io.File;

import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.app.HongdianConstants;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.util.image.UpyUploadUtil;
import fm.dian.hdui.view.MyAlertDialog;
import fm.dian.hdui.wximage.clip.utils.ClipImageLayout;

public class ClipActivity extends HDBaseActivity {
    private ClipImageLayout mClipImageLayout;
    private MyAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        initUI();
    }

    private void complete() {
        dialog.show();
        new Thread() {
            public void run() {
                Bitmap bitmap = mClipImageLayout.clip();
                if (bitmap == null) {
                    dialog.dismiss();
                    finish();
                    return;
                }
                HDBaseTabFragmentActivity.chooseBitmapPathLocal = mClipImageLayout.getSelectImagePath();

                final File file = new File(getCacheDir(), HongdianConstants.TEMP_UPLOAD_PHOTO_NAME);
                boolean saveBmpToFile = SysTool.saveBmpToFile(bitmap, file);
                if (!saveBmpToFile) {
                    dialog.dismiss();
                    finish();
                }

//                String savePath = UpyUploadUtil.createRemoteImageName(ClipActivity.this);
                System.out.printf("开始上传");
                UpyUploadUtil.uploadImage(ClipActivity.this, file, new CompleteListener() {
                    @Override
                    public void result(boolean isComplete, String result, String error) {
                        if (isComplete) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String path = HongdianConstants.upy_host_download + jsonObject.getString("path");
                                if (null != path) {
                                    HDBaseTabFragmentActivity.chooseBitmapPathRemote = path;
                                    System.out.println("upy url=" + path);
//								    //上传完成删除图片
                                    deleteTempFile();

                                    Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "图片上传失败" + result + " " + error, Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                        finish();
                    }
                }, new ProgressListener() {

                    @Override
                    public void transferred(long transferedBytes, long totalBytes) {
                        System.out.println(transferedBytes + "  " + totalBytes);
//						long part=totalBytes/5;
//						int comp=0;
//						
//						if(transferedBytes>5*part){
//							comp=5;
//						}else if(transferedBytes>4*part){
//							comp=4;
//						}else if(transferedBytes>3*part){
//							comp=3;
//						}else if(transferedBytes>2*part){
//							comp=2;
//						}else if(transferedBytes>part){
//							comp=1;
//						}
//						dialog.setPrompMsg("上传中 "+comp+"/"+5);
                    }
                });

            }

            ;
        }.start();

    }


    private void deleteTempFile() {
        File tackFile = new File(getCacheDir(), HongdianConstants.TEMP_TACK_PHOTO_NAME);
        File uploadFile = new File(getCacheDir(), HongdianConstants.TEMP_UPLOAD_PHOTO_NAME);
        if (tackFile != null && tackFile.exists()) {
            tackFile.delete();
        }
        if (uploadFile != null && uploadFile.exists()) {
            uploadFile.delete();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                complete();
                break;

            default:
                break;
        }
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setText("完成");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 10, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setPadding(15, 0, 15, 0);

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        dialog = new MyAlertDialog(this, false, null);

        String path = getIntent().getStringExtra("path");
        mClipImageLayout.setImageDrawable(path);
    }
}
