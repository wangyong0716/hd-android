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

import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.broadcast.HDRoomChangedBroadCastReceiver;

public class HDRoomEditWebaddrActivity extends HDBase2Activity {

    public static final String ROOM_ID = "ROOM_ID";
    public static final String ROOM_WEBADDR_TYPE = "ROOM_WEBADDR_TYPE";
    public static final String ROOM_WEBADDR_TYPE_ADDR = "ROOM_WEBADDR_TYPE_ADDR";
    public static final String ROOM_WEBADDR_TYPE_NUM = "ROOM_WEBADDR_TYPE_NUM";

    public static final int MAX = 20;
    public static final int MIN = 4;

    private long roomId = 0;

    private String type = ROOM_WEBADDR_TYPE_NUM;

    private int result = RESULT_CANCELED;
    private EditText roomWebaddr;
    private EditText roomWebaddr2;
    private TextView roomWebaddr3;

    Room roomLocal;
    private CoreService coreService = CoreService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_room_edit_webaddr);
        initUI();
        loadData();
    }

    public void initUI() {
        roomId = getIntent().getLongExtra(ROOM_ID, 0);

        type = getIntent().getStringExtra(ROOM_WEBADDR_TYPE);

        if (!(ROOM_WEBADDR_TYPE_NUM.equals(type) || ROOM_WEBADDR_TYPE_ADDR.equals(type))) {
            type = ROOM_WEBADDR_TYPE_NUM;
        }
        roomWebaddr = (EditText) findViewById(R.id.roomWebaddr);
        roomWebaddr2 = (EditText) findViewById(R.id.roomWebaddr2);
        roomWebaddr3 = (TextView) findViewById(R.id.roomWebaddr3);

        roomWebaddr.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int c) {
                if (roomWebaddr.isFocused()) {
                    roomWebaddr2.setText(roomWebaddr.getText());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        roomWebaddr2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int c) {
                if (roomWebaddr2.isFocused()) {
                    roomWebaddr.setText(roomWebaddr2.getText());
                }
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
        String webaddr = room.getWebaddr();
        if (webaddr.length() > MAX) {
            webaddr = webaddr.substring(0, MAX - 1);
        }

        String prefix = ConfigService.getInstance().getUtilsRoomNamePrefix();
        roomWebaddr3.setText(prefix);

        roomWebaddr.setText(webaddr);
        roomWebaddr2.setText(webaddr);

        if (ROOM_WEBADDR_TYPE_ADDR.equals(type)) {
            roomWebaddr2.requestFocus();
            Selection.setSelection((Spannable) roomWebaddr2.getText(), webaddr.length());
        } else {
            roomWebaddr.requestFocus();
            Selection.setSelection((Spannable) roomWebaddr.getText(), webaddr.length());
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

        String webaddr = roomWebaddr.getText().toString().trim();

        webaddr = webaddr.replaceAll(" ", "");
        webaddr = webaddr.replaceAll("\n", "");

        if (webaddr.length() > MAX || webaddr.length() < MIN) {
            Toast.makeText(getApplicationContext(), "只支持4-20个字母、数字", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < webaddr.length(); i++) {
            char c = webaddr.charAt(i);
            if ((c <= '9' && c >= '0') ||
                    (c <= 'z' && c >= 'a') ||
                    (c <= 'Z' && c >= 'A')) {

            } else {
                Toast.makeText(getApplicationContext(), "只支持4-20个字母、数字", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        back.setEnabled(false);
        editBtn.setEnabled(false);


        roomLocal.setWebaddr(webaddr);

        coreService.updateRoom(roomLocal, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    back.setEnabled(true);
                    editBtn.setEnabled(true);

                    Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                    result = RESULT_OK;
                    HDRoomChangedBroadCastReceiver.changeRoom(getApplicationContext(), roomId);
                    onFinish();
                } else {
                    back.setEnabled(true);
                    editBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "更新失败" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
