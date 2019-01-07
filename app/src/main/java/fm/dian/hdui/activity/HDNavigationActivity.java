package fm.dian.hdui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.util.image.ImageScaleUtil;
import fm.dian.hdui.wxapi.WXEntryActivity;

public class HDNavigationActivity extends HDBaseActivity {
    public static HDNavigationActivity self;
    Button btn_login;
    Button btn_register;

    ImageView iv_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_navigation);
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        initUI();
    }

    @Override
    public void initUI() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);


        Bitmap bitmap = readBitMap(R.drawable.navigation_activity_bg);
        double ratio = (bitmap.getWidth() * 1.0) / bitmap.getHeight();
        double h = metric.widthPixels / ratio;

        Bitmap zoomImg = ImageScaleUtil.zoomImg(bitmap, metric.widthPixels, (int) h);
        iv_bg.setImageBitmap(zoomImg);
    }

    public Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                HDUi_log.i("登录按钮");
                Intent intentLogin = new Intent(this, WXEntryActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.btn_register:
                Intent intentRegister = new Intent(this, HDRegister1Activity.class);
                startActivity(intentRegister);
                break;

            default:
                break;
        }
    }
}
