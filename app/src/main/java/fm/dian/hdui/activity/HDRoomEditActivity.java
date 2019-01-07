package fm.dian.hdui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import fm.dian.hddata.util.HDLog;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.broadcast.HDRoomChangedBroadCastReceiver;
import fm.dian.hdui.util.HDImageLoadUtil;
import fm.dian.hdui.util.image.ImageScaleUtil;
import fm.dian.hdui.wximage.choose.ImageChooseActivity;
import fm.dian.hdui.wximage.choose.TakePictureActivity;

public class HDRoomEditActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";

    public static final int REQUEST_CODE_ROOM_EDIT_LOGO = 1;
    public static final int REQUEST_CODE_ROOM_EDIT_NAME = 2;
    public static final int REQUEST_CODE_ROOM_EDIT_DESCRIPTION = 3;
    public static final int REQUEST_CODE_ROOM_EDIT_WEBADDR = 4;
    public static final int REQUEST_CODE_ROOM_EDIT_AVATAR = 5;

    private HDLog log = new HDLog(HDRoomEditActivity.class);

    private long roomId = 0;

    private int result = RESULT_CANCELED;

    private HDImageLoadUtil imageLoadUtil;

    private TextView roomName;
    private ImageView roomAvatar;
    private TextView description;
    private TextView roomWebaddr;

    private String avatar;

    Room roomLocal;
    private CoreService coreService = CoreService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_room_edit);


        initUI();
        loadData();
    }

    private void initUI() {
        roomId = getIntent().getLongExtra(ROOM_ID, 0);

        imageLoadUtil = new HDImageLoadUtil();
        roomName = (TextView) findViewById(R.id.roomName);
        roomWebaddr = (TextView) findViewById(R.id.roomWebaddr);
        roomAvatar = (ImageView) findViewById(R.id.roomAvatar);
        description = (TextView) findViewById(R.id.description);
    }

    private void loadData() {
        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    roomLocal = (Room) data.getSerializable("room");

                    if (roomLocal != null) {
                        inflateView(roomLocal);
                    }
                }
            }
        });
    }

    private void inflateView(final Room room) {
        try {
            if (room == null) {
                log.e("room is null.");
                return;
            }
            roomName.setText(room.getName());
            description.setText(room.getDescription());
            roomWebaddr.setText(room.getWebaddr());

            if (!imageLoadUtil.isLoadingComplete(roomAvatar, room.getAvatar())) {
                HDApp.getInstance().imageLoader.displayImage(room.getAvatar(), roomAvatar, Config.getRoomConfig(), new SimpleImageLoadingListener() {
                    public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
                        imageLoadUtil.setImageLoad(roomAvatar, room.getAvatar());
                    }
                });
            }

        } catch (Throwable e) {
            log.e("setRoom [ERROR]: " + e.getMessage(), e);
        }
    }

    private void onFinish() {
        setResult(result);
        finish();
    }

    public void back(View v) {
        onFinish();
    }

    public void roomName(View v) {
        Intent intent = new Intent(getApplicationContext(), HDRoomEditNameActivity.class);
        intent.putExtra(HDRoomEditNameActivity.ROOM_ID, roomId);
        startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_NAME);
    }

    public void description(View v) {
        Intent intent = new Intent(getApplicationContext(), HDRoomEditDescriptionActivity.class);
        intent.putExtra(HDRoomEditDescriptionActivity.ROOM_ID, roomId);
        startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_DESCRIPTION);
    }

    public void webaddr(View v) {
        Intent intent = new Intent(getApplicationContext(), HDRoomEditWebaddrActivity.class);
        intent.putExtra(HDRoomEditWebaddrActivity.ROOM_ID, roomId);
        intent.putExtra(HDRoomEditWebaddrActivity.ROOM_WEBADDR_TYPE, HDRoomEditWebaddrActivity.ROOM_WEBADDR_TYPE_NUM);
        startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_WEBADDR);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ROOM_EDIT_NAME:
            case REQUEST_CODE_ROOM_EDIT_DESCRIPTION:
            case REQUEST_CODE_ROOM_EDIT_WEBADDR:
                if (resultCode == RESULT_OK) {
                    result = RESULT_OK;
                    loadData();
                }
                break;
            case REQUEST_CODE_ROOM_EDIT_AVATAR:
                if (HDBaseTabFragmentActivity.chooseBitmapPathLocal != null) {
                    Bitmap scaleBitmap = ImageScaleUtil.scaleBitmap(HDBaseTabFragmentActivity.chooseBitmapPathLocal);
                    HDBaseTabFragmentActivity.chooseBitmapPathLocal = null;
                    roomAvatar.setImageBitmap(scaleBitmap);
                }
                if (HDBaseTabFragmentActivity.chooseBitmapPathRemote != null) {
                    avatar = HDBaseTabFragmentActivity.chooseBitmapPathRemote;
                    HDBaseTabFragmentActivity.chooseBitmapPathRemote = null;

                    roomLocal.setAvatar(avatar);

//				if (!imageLoadUtil.isLoadingComplete(roomAvatar, room.avatar)) {
//					HDApp.getInstance().imageLoader.displayImage(room.avatar, roomAvatar, Config.getRoomConfig(), new SimpleImageLoadingListener() {
//						public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
//							imageLoadUtil.setImageLoad(roomAvatar, room.avatar);
//						}
//					});
//				}

                    coreService.updateRoom(roomLocal, new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            int e = data.getInt("error_code");
                            if (CoreService.OK == e) {
                                Room room = (Room) data.getSerializable("room");
                                result = RESULT_OK;
                                HDRoomChangedBroadCastReceiver.changeRoom(getApplicationContext(), roomId);

                                Toast.makeText(getApplicationContext(), "更新封面成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "更新封面失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void onBackPressed() {
        onFinish();
    }

}
