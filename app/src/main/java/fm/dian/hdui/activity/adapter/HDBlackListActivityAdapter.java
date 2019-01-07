package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.RoomUserIgnore;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.HDApp;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;


/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2013-3-28 下午2:46:51
 */
@SuppressLint("InflateParams")
public class HDBlackListActivityAdapter extends BaseAdapter {

    private List<RoomUserIgnore> member_filter = new ArrayList<RoomUserIgnore>();
    private LayoutInflater inflater;
    private Context mContext;
    private CoreService coreService = CoreService.getInstance();

    public HDBlackListActivityAdapter(Context context, List<RoomUserIgnore> members) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.member_filter = members;
    }

    public void resetData(List<RoomUserIgnore> members) {
        this.member_filter = members;
    }

    @Override
    public int getCount() {
        return member_filter.size();
    }

    @Override
    public Object getItem(int position) {
        return member_filter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomUserIgnore member = (RoomUserIgnore) member_filter.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_blacklist_activity, null);
        }
        LinearLayout ll_user_name = (LinearLayout) convertView.findViewById(R.id.ll_user_name);
        final ImageView iv_user_icon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
        TextView tv_user_type = (TextView) convertView.findViewById(R.id.tv_user_type);
        final TextView tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
        final TextView tv_user_gender = (TextView) convertView.findViewById(R.id.tv_user_gender);
        final TextView tv_user_des = (TextView) convertView.findViewById(R.id.tv_user_des);
        TextView tv_last_item = (TextView) convertView.findViewById(R.id.tv_last_item);

        coreService.fetchUserInfo(member.getUserId(), new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    User user = (User) data.getSerializable("user");
                    inflateUserView(user, iv_user_icon, tv_user_gender, tv_user_name, tv_user_des);
                }
            }
        });
        return convertView;
    }


    private void inflateUserView(User user, ImageView iv_user_icon, TextView tv_user_gender, TextView tv_user_name, TextView tv_user_des) {
        Drawable drawable = null;
        if (GenderType.MALE == user.getGender()) {
            drawable = mContext.getResources().getDrawable(R.drawable.activity_online_gender_man);
        } else {
            drawable = mContext.getResources().getDrawable(R.drawable.activity_online_gender_woman);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_user_gender.setCompoundDrawables(null, null, drawable, null); //设置左图标


        HDApp.getInstance().imageLoader.displayImage(user.getAvatar() + HDApp.smallIcon, iv_user_icon);
        tv_user_name.setText(user.getNickname() + "");
        tv_user_des.setText(user.getSignature());
    }

}