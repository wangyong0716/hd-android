package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HomePageRoom;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseActivity;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.HDUiLog;


public class HDTab1ListAdapter extends BaseAdapter {
    public HDUiLog log = new HDUiLog();

    private LayoutInflater mInflater;
    private List<HomePageRoom> rooms;
    private CoreService coreService;
    private HashMap<Long, Room> roomMap = new HashMap<Long, Room>();

    public HDTab1ListAdapter(Context mContext, List<HomePageRoom> datas) {
        super();
        coreService = CoreService.getInstance();
        mInflater = LayoutInflater.from(mContext);
        this.rooms = datas;
    }

    public void resetData(List<HomePageRoom> dataList) {
        if(dataList.size() == 20){ // 下拉刷新清空缓存
            roomMap.clear();
        }
        this.rooms = dataList;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 300);
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HomePageRoom homePageRoom = rooms.get(position);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_tab1_activity_rome, null);
            holder.iv_room_icon = (ImageView) convertView.findViewById(R.id.iv_room_icon);
            holder.tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
            holder.tv_room_describe = (TextView) convertView.findViewById(R.id.tv_room_describe);
            holder.iv_room_online_num = (TextView) convertView.findViewById(R.id.iv_room_online_num);
            holder.tv_room_state = (TextView) convertView.findViewById(R.id.tv_room_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (homePageRoom != null && homePageRoom.getIsLive()) {
            holder.tv_room_state.setVisibility(View.VISIBLE);
            holder.tv_room_state.setText("直播中");
        } else {
            holder.tv_room_state.setVisibility(View.GONE);
        }

        if(!roomMap.containsKey(homePageRoom.getRoomId())){
            coreService.fetchRoomByRoomId(homePageRoom.getRoomId(), new CallBack() {
                @Override
                public void process(Bundle data) {
                    int error_code = data.getInt("error_code");
                    if (CoreService.OK == error_code) {
                        Room room = (Room) data.getSerializable("room");
                        if (room != null) {
                            roomMap.put(homePageRoom.getRoomId(), room);//map相同key 自动去重


                        }
                        inflateItem(holder,homePageRoom,room);
                    }
                }
            });

        }else{
            inflateItem(holder,homePageRoom,roomMap.get(homePageRoom.getRoomId()));
        }
        return convertView;
    }

    private void inflateItem(ViewHolder holder,HomePageRoom homePageRoom ,Room room){
        if (null == room) {
            holder.tv_room_name.setText("");
            holder.tv_room_describe.setText("");
            HDApp.getInstance().imageLoader.displayImage("", holder.iv_room_icon, Config.getRoomConfig());
            holder.iv_room_online_num.setText("在线 " + 0);
        } else {
            holder.tv_room_name.setText(room.getName());
            holder.tv_room_describe.setText(room.getDescription());
            HDApp.getInstance().imageLoader.displayImage(room.getAvatar() + HDApp.smallIcon, holder.iv_room_icon, Config.getRoomConfig());
            holder.iv_room_online_num.setText("在线 " + homePageRoom.getOnlineUserNumber());
        }
    }



    private class ViewHolder {
        ImageView iv_room_icon;
        TextView tv_room_name;
        TextView tv_room_describe;
        TextView iv_room_online_num;
        TextView tv_room_state;
    }

    public Room getRoom(long roomId) {
        return this.roomMap.get(roomId);
    }


}
