package fm.dian.hdui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hddata.util.HDLog;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.util.ActivityCheck;
import fm.dian.hdui.util.image.ImageScaleUtil;
import fm.dian.hdui.wximage.choose.ImageChooseActivity;
import fm.dian.hdui.wximage.choose.TakePictureActivity;

public class HDRoomCreateActivity extends HDBase2Activity {

    public static final int REQUEST_CODE_ROOM_EDIT_AVATAR = 5;

    private HDLog log = new HDLog(HDRoomCreateActivity.class);

//	private HDImageLoadUtil imageLoadUtil;

    private TextView roomName;
    private ImageView roomAvatar;
    private TextView roomDescription;
    private Button editBtn;

    private String avatar;
    private CoreService coreService = CoreService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_room_create);

//		imageLoadUtil = new HDImageLoadUtil();

        roomName = (TextView) findViewById(R.id.roomName);
        roomAvatar = (ImageView) findViewById(R.id.roomAvatar);
        roomDescription = (TextView) findViewById(R.id.roomDescription);
        editBtn = (Button) findViewById(R.id.editBtn);


        TextWatcher textWatcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkBtn();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };

        roomName.addTextChangedListener(textWatcher);
        roomDescription.addTextChangedListener(textWatcher);

        checkBtn();
    }

    public void back(View v) {
        finish();
    }

    public void roomAvatar(View v) {
        final Dialog builder = new Dialog(this, R.style.HDDialog);
        builder.setContentView(R.layout.activity_room_edit_avatar_dialog);
        builder.setCanceledOnTouchOutside(true);
        builder.show();

        builder.findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), TakePictureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_AVATAR);
            }
        });

        builder.findViewById(R.id.editWebaddr).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), ImageChooseActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_AVATAR);
            }
        });
    }

    private void checkBtn() {

        if (avatar != null && roomName.getText().length() > 0 && roomDescription.getText().length() > 0) {
            editBtn.setEnabled(true);
        } else {
            editBtn.setEnabled(false);
        }

    }

    public void save(View v) {

        if (avatar == null) {
            Toast.makeText(getApplicationContext(), "请上传头像", Toast.LENGTH_SHORT).show();
            return;
        }

        final ImageButton back = (ImageButton) findViewById(R.id.back);
        final Button editBtn = (Button) findViewById(R.id.editBtn);

        String name = roomName.getText().toString().trim();

        name = name.replaceAll(" ", "");
        name = name.replaceAll("\n", "");

        if (name.length() == 0) {
            Toast.makeText(getApplicationContext(), "频道名不允许为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > 30) {
            Toast.makeText(getApplicationContext(), "最多15个汉字（30字符）", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = roomDescription.getText().toString().trim();

        description = description.replaceAll(" ", "");

        if (description.length() == 0) {
            Toast.makeText(getApplicationContext(), "频道简介不允许为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.length() > 300) {
            Toast.makeText(getApplicationContext(), "最多150个汉字（300字符）", Toast.LENGTH_SHORT).show();
            return;
        }

        back.setEnabled(false);
        editBtn.setEnabled(false);

        Room room = new Room();

        room.setAvatar(avatar);
        room.setName(name);
        room.setDescription(description);

        coreService.createRoom(room, new CallBack() {
            @Override
            public void process(Bundle data) {
                back.setEnabled(true);
                editBtn.setEnabled(true);

                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Toast.makeText(getApplicationContext(), "创建频道成功", Toast.LENGTH_SHORT).show();
                    Room room = (Room) data.getSerializable("room");

                    Intent intent = new Intent(getApplicationContext(), HDRoomCreateShowActivity.class);
                    intent.putExtra(HDRoomCreateShowActivity.ROOM_ID, room.getRoomId());
                    startActivity(intent);

                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "创建频道失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ROOM_EDIT_AVATAR:
                if (HDBaseTabFragmentActivity.chooseBitmapPathLocal != null) {
                    Bitmap scaleBitmap = ImageScaleUtil.scaleBitmap(HDBaseTabFragmentActivity.chooseBitmapPathLocal);
                    HDBaseTabFragmentActivity.chooseBitmapPathLocal = null;
                    roomAvatar.setImageBitmap(scaleBitmap);
                }
                if (HDBaseTabFragmentActivity.chooseBitmapPathRemote != null) {
                    avatar = HDBaseTabFragmentActivity.chooseBitmapPathRemote;
                    HDBaseTabFragmentActivity.chooseBitmapPathRemote = null;

//				if (!imageLoadUtil.isLoadingComplete(roomAvatar, avatar)) {
//					HDApp.getInstance().imageLoader.displayImage(avatar, roomAvatar, null, new SimpleImageLoadingListener() {
//						public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
//							imageLoadUtil.setImageLoad(roomAvatar, avatar);
//						}
//					});
//				}
                }
                checkBtn();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
