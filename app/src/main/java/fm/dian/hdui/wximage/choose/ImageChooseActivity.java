package fm.dian.hdui.wximage.choose;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.activity.HDBlackboardTxt;
import fm.dian.hdui.activity.HDPhotoActivity;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.wximage.choose.album.AlbumAdapter;
import fm.dian.hdui.wximage.choose.album.AlbumHelper;
import fm.dian.hdui.wximage.choose.album.AlbumModel;
import fm.dian.hdui.wximage.choose.utils.ImageGridAdapter;
import fm.dian.hdui.wximage.choose.utils.ImageItem;

/**
 * 用法：用Intent 调用本activity，需要Intent传递intent.putExtra("type", "many");
 *
 * @author song
 */

@SuppressLint("HandlerLeak")
@SuppressWarnings("unchecked")
public class ImageChooseActivity extends HDBaseActivity {
    public static final int result_code_selected_images = 11;

    DisplayMetrics displayMetrics = new DisplayMetrics();
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;
    //	----------------Album----------------------
    List<AlbumModel> albumDataList;
    ListView lv_content;
    AlbumAdapter albumAdapter;// 自定义的适配器
    AlbumHelper albumHelper;
    //	----------------destDir--------------------
    List<ImageItem> destDataList;
    GridView gridView;
    ImageGridAdapter destAdapter;

    //	----------------myLogic--------------------
    TextView tv_selected;
    TextView tv_listview_bg;
    RelativeLayout rl_list;
    boolean is2Close = true;
    boolean isAniming = false;
    int type = 1;//默认只选择一张图片
    private boolean isFromChatAct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_choose_image_main);

        type = getIntent().getIntExtra("type", 1);
        isFromChatAct = getIntent().getBooleanExtra("isFromChatAct", false);

        albumHelper = AlbumHelper.getHelper();
        albumHelper.init(getApplicationContext());

        initData();
        initUI();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        albumDataList = albumHelper.getImagesBucketList(true);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//		---------------distDir----------------
        destDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                EXTRA_IMAGE_LIST);
    }

    private void toggleListView() {
        final Animation alphaAnim;
        alphaAnim = AnimationUtils.loadAnimation(ImageChooseActivity.this, R.anim.alpha_in);
        final TranslateAnimation animation;
        if (is2Close) {
            animation = (TranslateAnimation) AnimationUtils.loadAnimation(ImageChooseActivity.this, R.anim.translate_bottom_out);
        } else {
            animation = (TranslateAnimation) AnimationUtils.loadAnimation(ImageChooseActivity.this, R.anim.translate_bottom_in);
        }
        animation.setDuration(300);
        animation.setFillAfter(true);
        lv_content.setAnimationCacheEnabled(true);
        lv_content.setAnimation(animation);
        lv_content.startAnimation(animation);

        alphaAnim.setFillAfter(true);
        alphaAnim.setDuration(200);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;
                if (!is2Close) {//准备打开
                    rl_list.setVisibility(View.VISIBLE);
                    lv_content.setVisibility(View.VISIBLE);
                    lv_content.setEnabled(true);
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (is2Close) {//关闭动画完成
                    rl_list.setVisibility(View.GONE);
                    lv_content.setVisibility(View.GONE);
                    lv_content.setEnabled(false);

                    tv_listview_bg.startAnimation(alphaAnim);
                }
                is2Close = !is2Close;
                isAniming = false;
            }
        });
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        if (type == 1) {
            tv_common_action_bar_right.setVisibility(View.GONE);
        } else {
            tv_common_action_bar_right.setVisibility(View.VISIBLE);
        }
        tv_common_action_bar_right.setText("发送");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(5, 0, 5, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        tv_common_action_bar_right.setEnabled(false);

        tv_common_action_bar_right.setOnClickListener(this);
        ib_action_bar_left.setOnClickListener(this);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rl_list = (RelativeLayout) findViewById(R.id.rl_list);
        rl_list.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        tv_listview_bg = (TextView) findViewById(R.id.tv_listview_bg);
        tv_listview_bg.setOnClickListener(this);
        tv_selected = (TextView) findViewById(R.id.tv_selected);
        tv_selected.setOnClickListener(this);

//		-----------------------------------------
        lv_content = (ListView) findViewById(R.id.lv_content);
        albumAdapter = new AlbumAdapter(ImageChooseActivity.this, albumDataList);
        lv_content.setAdapter(albumAdapter);

        lv_content.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (albumDataList != null && albumDataList.size() > 0) {
                    destAdapter.clearSelectedList();
                    AlbumModel albumModel = albumDataList.get(position);
                    destDataList = albumModel.imageList;
                    destAdapter.resetDataList(destDataList);
                    destAdapter.notifyDataSetChanged();

                    int visiblePosition = lv_content.getFirstVisiblePosition();
                    int originPosition = albumAdapter.getSelectItem();
                    if (originPosition >= visiblePosition && originPosition <= lv_content.getLastVisiblePosition()) {
                        View originView = lv_content.getChildAt(originPosition - visiblePosition);
                        if (null != originView) {
                            originView.findViewById(R.id.isselected).setVisibility(View.GONE);
                        }
                    }
                    View currentView = lv_content.getChildAt(position - visiblePosition);
                    if (null != currentView) {
                        currentView.findViewById(R.id.isselected).setVisibility(View.VISIBLE);
                    }

                    albumAdapter.setSelectItem(position);
                    tv_selected.setText(albumModel.bucketName);
                    toggleListView();
                }
            }

        });
//		-----------------------destDir--------------------------

        gridView = (GridView) findViewById(R.id.gridview);
        destAdapter = new ImageGridAdapter(ImageChooseActivity.this, destDataList);
        destAdapter.setChoseType(type);
        destAdapter.setSendButton(tv_common_action_bar_right);
        gridView.setAdapter(destAdapter);

        if (albumDataList != null && albumDataList.size() > 0) {
            AlbumModel albumModel = albumDataList.get(0);

            if (albumModel != null && albumModel.imageList != null) {
                tv_selected.setText(albumModel.bucketName);

                destDataList = albumModel.imageList;
                destAdapter.resetDataList(destDataList);
                destAdapter.notifyDataSetChanged();
            }
        }
        toggleListView();
    }

    private void setResult() {
        ArrayList<ImageItem> imagesList = (ArrayList<ImageItem>) destAdapter.getSelectedList();
//                Toast.makeText(ImageChooseActivity.this, "选图片" + imagesList.get(0).imagePath, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putSerializable("imageList", imagesList);

        intent.putExtra("resultBundle", bundle);
        setResult(result_code_selected_images, intent);
        ImageChooseActivity.this.finish();
    }

    private void setEmptyResult() {
        ArrayList<ImageItem> imagesList = new ArrayList<ImageItem>();

        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putSerializable("imageList", imagesList);

        intent.putExtra("resultBundle", bundle);
        setResult(result_code_selected_images, intent);
        ImageChooseActivity.this.finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                setResult();
                break;
            case R.id.tv_listview_bg:
                rl_list.setVisibility(View.VISIBLE);
                if (!isAniming) {
                    toggleListView();
                }
                break;
            case R.id.tv_selected:
                rl_list.setVisibility(View.VISIBLE);
                if (!isAniming) {
                    toggleListView();
                }
                break;
            case R.id.ib_action_bar_left:
                //setEmptyResult();
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HDBlackboardTxt.RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS) {
            String txt = data.getStringExtra("txt");

            Intent intentResult = new Intent();
            intentResult.putExtra("txt", txt);
            setResult(HDBlackboardTxt.RESULT_CODE_BLACKBOARD_WRITE_TXT_SUCCESS, intentResult);
            finish();
        } else if (resultCode == HDImagePreviewActivity.result_code_selected) {
            ImageItem item = new ImageItem();
            String id = data.getStringExtra("id");
            String path = data.getStringExtra("image_path");
            boolean isComplete = data.getBooleanExtra("isComplete", false);
            boolean isUnselected = data.getBooleanExtra("isUnselected", false);

            if (null != id && path != null) {
                item.setImageId(id);
                item.setImagePath(path);
                item.isSelected = true;
                if (isUnselected) {
                    destAdapter.addOrDelSelected(item, false);
                } else {
                    destAdapter.addOrDelSelected(item, true);
                }

                if (destAdapter.getSelectedList().size() > 0) {
                    tv_common_action_bar_right.setText("发送(" + destAdapter.getSelectedList().size() + "/9)");
                    tv_common_action_bar_right.setEnabled(true);
                } else {
                    tv_common_action_bar_right.setText("发送");
                    tv_common_action_bar_right.setEnabled(false);
                }
            }

            if (isComplete) {//关闭
                setResult();
            }
        } else if (resultCode == HDPhotoActivity.RESULT_CODE_BLACKBOARD_WRITE_PHOTO_SUCCESS) {
            ImageItem item = new ImageItem();
            String id = data.getStringExtra("id");
            String path = data.getStringExtra("path");
            item.setImageId(id);
            item.setImagePath(path);

            ArrayList<ImageItem> imagesList = new ArrayList<ImageItem>();
            imagesList.add(item);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("imageList", imagesList);

            intent.putExtra("resultBundle", bundle);
            setResult(result_code_selected_images, intent);
            ImageChooseActivity.this.finish();
        }
    }

}

