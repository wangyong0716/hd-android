package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.SubscribeListRoom;
import fm.dian.hdui.R;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.HDUiLog;


public class HDTab2ListAdapter extends BaseAdapter {
    public HDUiLog log = new HDUiLog();
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SubscribeListRoom> rooms;
    private CoreService coreService;
    private HashMap<Long, Room> roomMap = new HashMap<Long, Room>();

    public HDTab2ListAdapter(Context mContext, List<SubscribeListRoom> datas) {
        super();
        coreService = CoreService.getInstance();
        mInflater = LayoutInflater.from(mContext);
        this.rooms = datas;
        this.mContext = mContext;
    }

    public void resetData(List<SubscribeListRoom> dataList) {
        this.rooms = dataList;
        roomMap.clear();
    }


    @Override
    public int getCount() {
        if (null != rooms) {
            return rooms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SubscribeListRoom hdSubscribeRoom = rooms.get(position);

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

        if (hdSubscribeRoom != null && hdSubscribeRoom.getIsLive()) {
            holder.tv_room_state.setVisibility(View.VISIBLE);
            holder.tv_room_state.setText("直播中");
        } else {
            holder.tv_room_state.setVisibility(View.GONE);
        }
        coreService.fetchRoomByRoomId(hdSubscribeRoom.getRoomId(), new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    Room room = (Room) data.getSerializable("room");
                    if (room != null) {
                        roomMap.put(hdSubscribeRoom.getRoomId(), room);//map相同key 自动去重
                    }
                    //设置房间属性
                    if (room != null && room.getIsCanceled()) {
                        holder.tv_room_state.setVisibility(View.VISIBLE);
                        holder.tv_room_state.setText("已关闭");
                    }
                    if (null == room) {
                        holder.tv_room_name.setText("未知");
                        holder.tv_room_describe.setText("");
                        holder.iv_room_online_num.setText("");
                    } else {
                        holder.tv_room_name.setText(room.getName());
                        holder.tv_room_describe.setText(room.getDescription());
                        HDApp.getInstance().imageLoader.displayImage(room.getAvatar() + HDApp.smallIcon, holder.iv_room_icon, Config.getRoomConfig());
                        holder.iv_room_online_num.setText("在线 " + hdSubscribeRoom.getOnlineUserNumber());
                    }
                }
            }
        });
        return convertView;
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

    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> cache;

        public BitmapCache() {
            cache = new LruCache<String, Bitmap>(30 * 1024 * 1024) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }
    }

}
