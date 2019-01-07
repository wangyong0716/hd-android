package fm.dian.hdui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hddata.util.HDLog;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.broadcast.HDRoomChangedBroadCastReceiver;

public class HDRoomEditDescriptionActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";

    public static final int MAX = 150;

    private HDLog log = new HDLog(HDRoomEditDescriptionActivity.class);

    private long roomId = 0;

    private int result = RESULT_CANCELED;

    private EditText roomDescription;
    private TextView count;

    Room roomLocal;
    private CoreService coreService = CoreService.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_room_edit_description);

        initUI();
        loadData();
    }

    private void initUI() {
        roomId = getIntent().getLongExtra(ROOM_ID, 0);

        roomDescription = (EditText) findViewById(R.id.roomDescription);
        count = (TextView) findViewById(R.id.count);

        roomDescription.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int c) {
                count.setText((MAX - s.length()) + "");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });
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

            String description = room.getDescription();

            if (description.length() > MAX) {
                description = description.substring(0, MAX - 1);
            }

            roomDescription.setText(description);

            count.setText((MAX - description.length()) + "");

            roomDescription.requestFocus();

            Selection.setSelection((Spannable) roomDescription.getText(), description.length());

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

        String description = roomDescription.getText().toString().trim();

        description = description.replaceAll(" ", "");

        if (description.length() == 0) {
            Toast.makeText(getApplicationContext(), "频道简介不允许为空", Toast.LENGTH_SHORT).show();
            return;
        }

//		if (description.length() > MAX) {
//			Toast.makeText(getApplicationContext(), "最多150个汉字（300字符）", Toast.LENGTH_SHORT).show();
//			return ;
//		}

        back.setEnabled(false);
        editBtn.setEnabled(false);

        roomLocal.setDescription(description);
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
