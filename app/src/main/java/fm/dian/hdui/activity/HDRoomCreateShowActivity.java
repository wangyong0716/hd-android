package fm.dian.hdui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.ActivityCheck;
import fm.dian.hdui.util.HDImageLoadUtil;

public class HDRoomCreateShowActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";

    public static final int REQUEST_CODE_ROOM_EDIT_WEBADDR = 4;

    private long roomId = 0;
    private HDImageLoadUtil imageLoadUtil;

    private TextView roomName;
    private ImageView roomAvatar;
    private TextView roomWebaddr;
    private TextView description;

    private CoreService coreService = CoreService.getInstance();
    private Room roomLocal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_room_create_show);

        roomId = getIntent().getLongExtra(ROOM_ID, 0);

        imageLoadUtil = new HDImageLoadUtil();

        roomName = (TextView) findViewById(R.id.roomName);
        roomWebaddr = (TextView) findViewById(R.id.roomWebaddr);
        roomAvatar = (ImageView) findViewById(R.id.roomAvatar);
        description = (TextView) findViewById(R.id.description);

        initRoom();
    }

    private void initRoom() {
        coreService.fetchRoomByRoomId(roomId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Room room = (Room) data.getSerializable("room");
                    if (room != null) {
                        roomLocal = room;
                        setRoom(room);
                    }
                }
            }
        });

//        coreService.fetchFollowByRoomId(roomId, new CallBack() {
//            @Override
//            public void process(Bundle data) {
//                int e = data.getInt("error_code");
//                if (CoreService.OK == e) {
//                    Room room = (Room) data.getSerializable("room");
//                    if (room != null) {
//                        setRoom(room);
//                    }
//                }
//            }
//        });
    }

    private void setRoom(final Room room) {
        try {
            if (room == null) {
                return;
            }
            roomName.setText(room.getName());
            roomWebaddr.setText("频道号: " + room.getWebaddr());

            description.setText(room.getDescription());

            if (!imageLoadUtil.isLoadingComplete(roomAvatar, room.getAvatar())) {
                HDApp.getInstance().imageLoader.displayImage(room.getAvatar(), roomAvatar, Config.getRoomConfig(), new SimpleImageLoadingListener() {
                    public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
                        imageLoadUtil.setImageLoad(roomAvatar, room.getAvatar());
                    }
                });
            }
        } catch (Throwable e) {
        }
    }

    public void back(View v) {
        finish();
    }

    public void go(View v) {
        AuthService.getInstance().joinRoom(roomLocal.getRoomId(), "", new CallBack() {
            @Override
            public void process(Bundle data) {
                int error_code = data.getInt("error_code");
                if (AuthService.OK == error_code) {
                    HDApp.getInstance().setCurrRoomId(roomLocal.getRoomId());

                    Intent channelIntent = new Intent(HDRoomCreateShowActivity.this, HDChannelActivity.class);
                    channelIntent.putExtra("roomId", roomLocal.getRoomId());
                    startActivity(channelIntent);
                    HDRoomCreateShowActivity.this.finish();
                } else {
                    Toast.makeText(HDRoomCreateShowActivity.this, "进入新房间错误" + error_code, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void editWebAddr(View v) {
        Intent intent = new Intent(getApplicationContext(), HDRoomEditWebaddrActivity.class);
        intent.putExtra(HDRoomEditWebaddrActivity.ROOM_ID, roomId);
        intent.putExtra(HDRoomEditWebaddrActivity.ROOM_WEBADDR_TYPE, HDRoomEditWebaddrActivity.ROOM_WEBADDR_TYPE_NUM);
        startActivityForResult(intent, REQUEST_CODE_ROOM_EDIT_WEBADDR);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ROOM_EDIT_WEBADDR:
                if (resultCode == RESULT_OK) {
                    initRoom();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
