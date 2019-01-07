package fm.dian.hdui.activity;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import fm.dian.hddata.util.HDLog;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.broadcast.HDRoomChangedBroadCastReceiver;

public class HDRoomEditNameActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";

    private HDLog log = new HDLog(HDRoomEditNameActivity.class);

    private long roomId = 0;

    private int result = RESULT_CANCELED;
    private EditText roomName;

    Room roomLocal;
    private CoreService coreService = CoreService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_room_edit_name);

        initUI();
        loadData();
    }

    private void initUI() {
        roomId = getIntent().getLongExtra(ROOM_ID, 0);
        roomName = (EditText) findViewById(R.id.roomName);
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

    private void inflateView(Room room) {

        if (room != null) {
            String name = room.getName();

            if (name.length() > 30) {
                name = name.substring(0, 29);
            }

            roomName.setText(name);

            roomName.requestFocus();

            Selection.setSelection((Spannable) roomName.getText(), name.length());

        } else {
            Toast.makeText(getApplicationContext(), "频道信息有误！", Toast.LENGTH_SHORT).show();
            onFinish();
        }

    }

    private void onFinish() {
        setResult(result);
        finish();
    }

    public void back(View v) {
        onFinish();
    }

    public void edit(View v) {
        final ImageButton back = (ImageButton) findViewById(R.id.back);
        final Button editBtn = (Button) findViewById(R.id.editBtn);

        String name = roomName.getText().toString().trim();

        name = name.replaceAll(" ", "");
        name = name.replaceAll("\n", "");

        if (name.length() == 0) {
            Toast.makeText(getApplicationContext(), "名称不允许为空", Toast.LENGTH_SHORT).show();
            return;
        }

//		if (name.length() > 30) {
//			Toast.makeText(getApplicationContext(), "最多15个汉字", Toast.LENGTH_SHORT).show();
//			return ;
//		}

        back.setEnabled(false);
        editBtn.setEnabled(false);
        roomLocal.setName(name);

        coreService.updateRoom(roomLocal, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    back.setEnabled(true);
                    editBtn.setEnabled(true);

                    result = RESULT_OK;
                    HDRoomChangedBroadCastReceiver.changeRoom(getApplicationContext(), roomId);
                    onFinish();
                    Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                } else {
                    back.setEnabled(true);
                    editBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
