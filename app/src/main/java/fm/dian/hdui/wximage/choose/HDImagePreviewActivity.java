package fm.dian.hdui.wximage.choose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.wximage.clip.utils.ScaleImageView;

public class HDImagePreviewActivity extends HDBaseActivity {
    public static final int result_code_selected = 15;
    ScaleImageView iv_image;
    TextView tv_selected;
    String image_path = "";
    String id = "";
    int count = 0;
    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_image_preview);
        initUI();
    }

    @Override
    public void initUI() {
        image_path = getIntent().getStringExtra("path");
        id = getIntent().getStringExtra("id");
        count = getIntent().getIntExtra("count", 0);
        isSelected = getIntent().getBooleanExtra("isSelected", false);

        super.initActionBar(this);
        ib_action_bar_left.setOnClickListener(this);
        tv_common_action_bar_right.setVisibility(View.VISIBLE);
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(5, 0, 5, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setOnClickListener(this);

        tv_selected = (TextView) findViewById(R.id.tv_selected);
        tv_selected.setOnClickListener(this);


        iv_image = (ScaleImageView) findViewById(R.id.iv_image);

        String url = "file://" + image_path;
        HDApp.getInstance().imageLoader.displayImage(url, iv_image);


        if (isSelected) {
            tv_common_action_bar_right.setText("发送(" + (count) + "/9)");
            tv_selected.setBackgroundResource(R.drawable.choose_image_act_check);
            tv_selected.setText(count + "");
            tv_selected.setTag("checked");
        } else {
            if (count > 0) {
                tv_common_action_bar_right.setText("发送(" + (count) + "/9)");
            } else {
                tv_common_action_bar_right.setText("发送");
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                complete();
                break;
            case R.id.ib_action_bar_left:
                setResult(false);
                break;
            case R.id.tv_selected:
                chooseImage();
                break;
            default:
                break;
        }
    }

    private void chooseImage() {
        if ("checked".equals(tv_selected.getTag())) {
            tv_selected.setBackgroundResource(R.drawable.choose_image_act_uncheck_selector);
            if (isSelected) {
                tv_common_action_bar_right.setText("发送(" + (count - 1) + "/9)");
            } else {
                tv_common_action_bar_right.setText("发送(" + (count) + "/9)");
            }
            tv_selected.setTag("");
            tv_selected.setText("");
        } else {
            tv_selected.setBackgroundResource(R.drawable.choose_image_act_check);
            int _count = count;
            if (isSelected) {
                tv_common_action_bar_right.setText("发送(" + (count) + "/9)");
            } else {
                tv_common_action_bar_right.setText("发送(" + (count + 1) + "/9)");
                _count += 1;
            }
            tv_selected.setTag("checked");
            tv_selected.setText((_count) + "");
        }
    }

    private void setResult(boolean isComplete) {
        //判断取消选择还是选中
        if (isSelected) { //原来是选中状态
            if (!"checked".equals(tv_selected.getTag())) {//取消选中
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("image_path", image_path);
                intent.putExtra("isComplete", isComplete);
                intent.putExtra("isUnselected", true);

                setResult(result_code_selected, intent);
            }
        } else {
            if ("checked".equals(tv_selected.getTag())) {//选中
                Intent intent = new Intent();
                intent.putExtra("id", id);
                intent.putExtra("image_path", image_path);
                intent.putExtra("isComplete", isComplete);
                intent.putExtra("isUnselected", false);

                setResult(result_code_selected, intent);
            }
        }

        HDImagePreviewActivity.this.finish();
    }

    private void complete() {
        Intent intent = new Intent();
        intent.putExtra("isComplete", true);
        if (count == 0 || (!isSelected && "checked".equals(tv_selected.getTag()))) {//一个都没有选中或者当前这个图片被选中就带回去
            intent.putExtra("id", id);
            intent.putExtra("image_path", image_path);
            intent.putExtra("isUnselected", false);
        }else{

        }
        setResult(result_code_selected, intent);
        HDImagePreviewActivity.this.finish();
    }
}
