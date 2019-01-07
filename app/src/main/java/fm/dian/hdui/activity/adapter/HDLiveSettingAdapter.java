package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.model.Live;
import fm.dian.hdservice.model.LiveListElement;
import fm.dian.hdui.R;


public class HDLiveSettingAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<LiveListElement> dataList = new ArrayList<LiveListElement>();

    public HDLiveSettingAdapter(Context mContext, List<LiveListElement> datas) {
        super();
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.dataList = datas;
    }

    public void resetData(List<LiveListElement> dataList) {
        this.dataList = dataList;
    }


    @Override
    public int getCount() {
        return this.dataList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder2 vh2 = null;
        if (convertView == null) {
            vh2 = new ViewHolder2();
            convertView = mInflater.inflate(R.layout.item_live_setting_activity, null);
            vh2.iv_live_icon = (ImageView) convertView.findViewById(R.id.iv_live_icon);
            vh2.tv_live_name = (TextView) convertView.findViewById(R.id.tv_live_name);
            vh2.tv_online_num = (TextView) convertView.findViewById(R.id.tv_online_num);
            convertView.setTag(vh2);
        } else {
            vh2 = (ViewHolder2) convertView.getTag();
        }

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

        return convertView;
    }

    static class ViewHolder2 {
        TextView tv_live_name;
        ImageView iv_live_icon;
        TextView tv_online_num;
    }

}
