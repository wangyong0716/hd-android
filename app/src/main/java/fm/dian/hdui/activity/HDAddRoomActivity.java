package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.HDApp;

@SuppressLint("InflateParams")
public class HDAddRoomActivity extends HDBaseActivity {
    public static final int msg_what_no_room = 100;
    public static final int msg_what_add_success = 101;
    ListView mListView;
    EditText et_search;
    TextView tv_action_bar_right;
    CoreService coreService = CoreService.getInstance();
    AuthService authService = AuthService.getInstance();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case msg_what_no_room:
                    Toast.makeText(HDAddRoomActivity.this, "没有此频道", Toast.LENGTH_SHORT).show();
                    break;
                case msg_what_add_success:
                    Toast.makeText(HDAddRoomActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    final Room room = (Room) msg.obj;
                    authService.joinRoom(room.getRoomId(), "", new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            int e = data.getInt("error_code");
                            if (AuthService.OK == e) {
                                HDApp.getInstance().setCurrRoomId(room.getRoomId());
                                Intent channelIntent = new Intent(HDAddRoomActivity.this, HDChannelActivity.class);
                                channelIntent.putExtra("roomId", room.getRoomId());
                                startActivity(channelIntent);
                                HDAddRoomActivity.this.finish();
                            }
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_add_room);
        initUI();
    }

    @Override
    public void initUI() {
        initActionBar();

        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HDAddRoomActivity.this.finish();
                return false;
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            getActionBar().setBackgroundDrawable(new ColorDrawable(R.color.actionbar_transparent_half));
            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v_action_bar = inflator.inflate(R.layout.layout_action_bar_search, null);
            LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            actionBar.setCustomView(v_action_bar, layout);
            tv_action_bar_right = (TextView) v_action_bar.findViewById(R.id.tv_action_bar_right);
            tv_action_bar_right.setOnClickListener(this);
            tv_action_bar_right.setEnabled(false);

            v_action_bar.findViewById(R.id.iv_action_bar_left).setOnClickListener(this);

            et_search = (EditText) v_action_bar.findViewById(R.id.et_action_bar_search);
            et_search.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (null != s.toString().trim()) {
                        tv_action_bar_right.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_action_bar_left:
                HDAddRoomActivity.this.finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;
            case R.id.tv_action_bar_right:
                String roomId = et_search.getText().toString().trim();
                if (null != roomId) {
//				        mHandler.search(roomId);
                    coreService.searchRoom(roomId, new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            int e = data.getInt("error_code");
                            if (CoreService.OK == e) {
                                final Room hdRoom = (Room) data.getSerializable("room");
                                if (hdRoom == null) {
                                    Toast.makeText(HDAddRoomActivity.this, "没有该频道", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                User hdUser = HDApp.getInstance().getCurrUser();

                                coreService.addFollow(hdRoom.getRoomId(), hdUser.getUserId(), new CallBack() {
                                    @Override
                                    public void process(Bundle data) {
                                        int e = data.getInt("error_code");
                                        if (CoreService.OK == e) {
                                            Message handlerMsg = new Message();
                                            handlerMsg.what = msg_what_add_success;
                                            handlerMsg.obj = hdRoom;
                                            mHandler.sendMessage(handlerMsg);
                                        } else {
                                            Toast.makeText(HDAddRoomActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(this,
                            getString(R.string.act_add_room_no_roomid),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
