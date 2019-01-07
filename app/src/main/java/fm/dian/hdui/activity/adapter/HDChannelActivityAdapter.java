package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.ConfigService;
import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDChannelDetailActivity;
import fm.dian.hdui.activity.HDCreateUpdateLiveActivity;
import fm.dian.hdui.activity.HDHistoryActivity;
import fm.dian.hdui.activity.HDRoomPasswdActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.view.CommonDialog;


public class HDChannelActivityAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Object> dataList = new ArrayList<Object>();
    private final int TYPE_ONE = 0, TYPE_TWO = 1, TYPE_THREE = 2, TYPE_COUNT = 3;
    private boolean hasHistory = false;
    private boolean addRoomVisiable = false;
    AuthService authService = AuthService.getInstance();
    private ConfigService configService = ConfigService.getInstance();
    private int maxRoomCount= 6;//默认最多创建6个房间

    private LiveListElement selectedLive = null;

    public HDChannelActivityAdapter(Context mContext, List<Object> datas) {
        super();
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.dataList = datas;
        if(configService !=null && configService.getLiveNumber()!=null){
            maxRoomCount= configService.getLiveNumber();
        }
    }

    public void resetData(List<Object> dataList) {
        this.dataList = dataList;
        if (selectedLive != null) {
            for (int i = 1; i < dataList.size() - 1; i++) {
                if(dataList.get(i)!=null && (dataList.get(i) instanceof LiveListElement)){
                    LiveListElement ele = (LiveListElement) dataList.get(i);
                    if (ele.getLive().getId().equals(selectedLive.getLive().getId())) {
                        dataList.remove(ele);
                        dataList.add(1, ele);
                    }
                }
            }
        } else {
            Long currLiveId = authService.getLiveId();
            for (int i = 1; i < dataList.size() - 1; i++) {
                if(dataList.get(i)!=null && (dataList.get(i) instanceof LiveListElement)){
                    LiveListElement ele = (LiveListElement) dataList.get(i);
                    if (currLiveId !=null && currLiveId.equals(ele.getLive().getId())) {
                        dataList.remove(ele);
                        dataList.add(1, ele);
                        selectedLive = ele;
                    }
                }
            }
        }
    }

    public void setHistoryVisiable(boolean hasHistory) {
        this.hasHistory = hasHistory;
    }

    public void setAddRoomVisiable(boolean addRoomVisiable) {
        this.addRoomVisiable = addRoomVisiable;
    }


    @Override
    public int getCount() {
        return this.dataList.size();
    }

    /**
     * 该方法返回多少个不同的布局
     */
    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    /**
     * 根据position返回相应的Item
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        }
        if (dataList.get(position) instanceof LiveListElement) {//TYPE_TWO
            return TYPE_TWO;
        }
        return TYPE_THREE;
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder1 vh1 = null;
        ViewHolder2 vh2 = null;
        ViewHolder3 vh3 = null;
        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {
                case TYPE_ONE:
                    vh1 = new ViewHolder1();
                    convertView = mInflater.inflate(R.layout.item_channel_activity_type1, null);
                    vh1.iv_room_cover = (ImageView) convertView.findViewById(R.id.iv_room_cover);
                    vh1.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    vh1.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                    vh1.tv_user_des = (TextView) convertView.findViewById(R.id.tv_user_des);
                    convertView.setTag(vh1);
                    break;
                case TYPE_TWO:
                    vh2 = new ViewHolder2();
                    convertView = mInflater.inflate(R.layout.item_channel_activity_type2, null);
                    vh2.iv_live_icon = (ImageView) convertView.findViewById(R.id.iv_live_icon);
                    vh2.tv_live_name = (TextView) convertView.findViewById(R.id.tv_live_name);
                    vh2.tv_online_num = (TextView) convertView.findViewById(R.id.tv_online_num);
                    vh2.rl_close_live = (RelativeLayout) convertView.findViewById(R.id.rl_close_live);
                    vh2.ll_on_speaking = (LinearLayout) convertView.findViewById(R.id.ll_on_speaking);
                    convertView.setTag(vh2);
                    break;
                case TYPE_THREE:
                    vh3 = new ViewHolder3();
                    convertView = mInflater.inflate(R.layout.item_channel_activity_type3, null);
                    vh3.tv_seeding_bar_room_title = (TextView) convertView.findViewById(R.id.tv_seeding_bar_room_title);
                    vh3.rl_create_live = (RelativeLayout) convertView.findViewById(R.id.rl_create_live);
                    vh3.rl_history = (RelativeLayout) convertView.findViewById(R.id.rl_history);
                    convertView.setTag(vh3);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_ONE:
                    vh1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_TWO:
                    vh2 = (ViewHolder2) convertView.getTag();
                    break;
                case TYPE_THREE:
                    vh3 = (ViewHolder3) convertView.getTag();
                    break;
            }
        }

        switch (type) {
            case TYPE_ONE:
                final Room room = (Room) dataList.get(position);
                if (vh1.iv_room_cover.getTag() != "ok") {
                    final ViewHolder1 finalVh1 = vh1;
                    HDApp.getInstance().imageLoader.loadImage(room.getAvatar(), new ImageLoadingListener() {
                        public void onLoadingStarted(String s, View view) {
                        }

                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                        }

                        public void onLoadingCancelled(String s, View view) {
                        }

                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            if (finalVh1.iv_room_cover != null) {
//                                bitmap = blurImageAmeliorate(bitmap);
                                finalVh1.iv_room_cover.setImageBitmap(bitmap);
                                finalVh1.iv_room_cover.setTag("ok");
                            }
                        }
                    });
                }

                HDApp.getInstance().imageLoader.displayImage(room.getAvatar(), vh1.iv_image);
                vh1.tv_user_name.setText("频道号:" + room.getWebaddr());
                vh1.tv_user_des.setText(room.getDescription());

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("setOnClickListener", "setOnClickListener");
                        Intent channelActIntent = new Intent(mContext, HDChannelDetailActivity.class);
                        channelActIntent.putExtra(HDChannelDetailActivity.ROOM_ID, room.getRoomId());
                        channelActIntent.putExtra(HDChannelDetailActivity.IS_FOLLOW, false);
                        channelActIntent.putExtra(HDChannelDetailActivity.IS_FROM_CHAT_ACT, false);
                        ((Activity) mContext).startActivityForResult(channelActIntent, 2);
                    }
                });

                break;
            case TYPE_TWO:
                Object o = dataList.get(position);
                if (o instanceof LiveListElement) { //避免CoreService重复调用
                    final LiveListElement liveListElement = (LiveListElement) dataList.get(position);
                    final Live live = liveListElement.getLive();

                    vh2.tv_live_name.setText(live.getName());

                    Boolean locked = live.getLocked();
                    if (locked) {
                        Drawable right = mContext.getResources().getDrawable(R.drawable.create_live_act_lock);
                        int h = right.getIntrinsicHeight();
                        int w = right.getIntrinsicWidth();
                        right.setBounds(0, 0, w, h);
                        vh2.tv_live_name.setCompoundDrawables(null, null, right, null);
                    } else {
                        vh2.tv_live_name.setCompoundDrawables(null, null, null, null);
                    }
                    //麦上是否有人
                    Boolean isLive = liveListElement.getIsLive();
                    if (isLive != null && isLive) {
                        vh2.iv_live_icon.setImageResource(R.drawable.channel_act_voice_icon);
                        vh2.tv_live_name.setTextColor(mContext.getResources().getColor(R.color.color_red));
                        vh2.ll_on_speaking.findViewById(R.id.iv_rise).setVisibility(View.VISIBLE);
                        vh2.ll_on_speaking.findViewById(R.id.v_divider).setVisibility(View.VISIBLE);
                    } else {
                        vh2.iv_live_icon.setImageResource(R.drawable.channel_act_offline_room);
                        vh2.tv_live_name.setTextColor(mContext.getResources().getColor(R.color.color_grey));
                        vh2.ll_on_speaking.findViewById(R.id.iv_rise).setVisibility(View.GONE);
                        vh2.ll_on_speaking.findViewById(R.id.v_divider).setVisibility(View.GONE);
                    }
                    //当前频道
//                    long currentLiveId = HDApp.getInstance().getCurrLiveId();
                    if ((selectedLive != null && live.getId().equals(selectedLive.getLive().getId()))) {
                        vh2.ll_on_speaking.setVisibility(View.VISIBLE);
                        vh2.tv_online_num.setVisibility(View.GONE);
                    } else {
                        vh2.ll_on_speaking.setVisibility(View.GONE);
                        vh2.tv_online_num.setVisibility(View.VISIBLE);
                    }
                    //在线人数
                    Long num = liveListElement.getUserNumber();
                    vh2.tv_online_num.setText(num + "在线");

                    convertView.setLongClickable(true);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedLive = liveListElement;
                            Long currLiveId = authService.getLiveId();
                            if (currLiveId != null && currLiveId != selectedLive.getLive().getId()) {//进入新房间，把上一个房间密码抹掉
                                HDApp.getInstance().setCurrPwd("");
                            }

                            HDApp.getInstance().setCurrLiveName(live.getName());
                            Intent intent = new Intent(mContext, HDRoomPasswdActivity.class);
                            intent.putExtra("isLocked", live.getLocked());
                            intent.putExtra("fromSetting", false);
                            intent.putExtra("liveId", live.getId());
                            mContext.startActivity(intent);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    filterLives(position);
                                }
                            }, 500);
                        }
                    });

                    final ViewHolder2 finalVh = vh2;
                    vh2.rl_close_live.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
                            seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_leave_live);
                            mContext.sendBroadcast(seeding);

                            finalVh.ll_on_speaking.setVisibility(View.GONE);
                            finalVh.tv_online_num.setVisibility(View.VISIBLE);
                            selectedLive = null;
                        }
                    });

                } else {
                    Toast.makeText(mContext, getCount() + " position===" + position, Toast.LENGTH_SHORT).show();
                }

                break;
            case TYPE_THREE:
                final Room roomLocal = (Room) dataList.get(0);
                vh3.tv_seeding_bar_room_title.setText("节目");

                vh3.rl_create_live.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataList.size() >= maxRoomCount+2) {
                            new CommonDialog(mContext, CommonDialog.ButtonType.oneButton, null, "当前频道最多只能创建"+maxRoomCount+"个房间", "", "", "无法创建");
                            return;
                        }
                        final Intent intentCreateLive = new Intent(mContext, HDCreateUpdateLiveActivity.class);
                        intentCreateLive.putExtra("roomId", roomLocal.getRoomId());
                        ((Activity) mContext).startActivityForResult(intentCreateLive, 1);
                    }
                });
                vh3.rl_history.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intentHistory = new Intent(mContext, HDHistoryActivity.class);
                        intentHistory.putExtra("roomId", roomLocal.getRoomId());
                        mContext.startActivity(intentHistory);
                    }
                });

                if (addRoomVisiable) {
                    vh3.rl_create_live.setVisibility(View.VISIBLE);
                } else {
                    vh3.rl_create_live.setVisibility(View.GONE);
                }
                if (hasHistory) {
                    vh3.rl_history.setVisibility(View.VISIBLE);
                } else {
                    vh3.rl_history.setVisibility(View.GONE);
                }
                break;
        }


        return convertView;
    }

    static class ViewHolder1 {
        ImageView iv_image;
        ImageView iv_room_cover;
        TextView tv_user_name;
        TextView tv_user_des;
    }

    static class ViewHolder2 {
        TextView tv_live_name;
        ImageView iv_live_icon;
        TextView tv_online_num;
        RelativeLayout rl_close_live;
        LinearLayout ll_on_speaking;
    }

    static class ViewHolder3 {
        RelativeLayout rl_create_live;
        TextView tv_seeding_bar_room_title;
        RelativeLayout rl_history;
    }

    public LiveListElement getSelectedLive() {
        return selectedLive;
    }

    private void filterLives(int pos) {
        if(pos < dataList.size()){
            dataList.remove(pos);
            dataList.add(1, getSelectedLive());
            notifyDataSetChanged();
        }
    }

}
