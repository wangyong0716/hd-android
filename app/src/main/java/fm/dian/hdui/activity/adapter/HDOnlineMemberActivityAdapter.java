package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.model.HDUIOnlineMemberModel;
import fm.dian.hdui.view.indexlistview.StickyListHeadersBaseAdapter;
import fm.dian.hdui.view.indexlistview.StickyViewHolder;
import fm.dian.hdui.view.indexlistview.StringMatcher;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;


/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2013-3-28 下午2:46:51
 */
@SuppressLint("InflateParams")
public class HDOnlineMemberActivityAdapter extends StickyListHeadersBaseAdapter implements SectionIndexer {

    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<HDUIOnlineMemberModel> member_filter = new ArrayList<HDUIOnlineMemberModel>();
    private LayoutInflater inflater;
    private Context mContext;

    private CoreService coreService = CoreService.getInstance();

    public HDOnlineMemberActivityAdapter(Context context, List<HDUIOnlineMemberModel> members) {
        super(context);
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.member_filter = members;
    }

    public void resetData(List<HDUIOnlineMemberModel> members) {
        this.member_filter = members;
    }

    @Override
    public int getPositionForSection(int section) {
        // If there is no item for current section, previous section will be selected
        for (int i = section; i >= 0; i--) {
            for (int j = 0; j < getCount(); j++) {
                HDUIOnlineMemberModel member = member_filter.get(j);
                char index = member.nickname.toUpperCase(Locale.CHINA).charAt(0);
                if (i == 0) {
                    // For numeric section此时选中字母为#
                    for (int k = 0; k <= 9; k++) {
                        if (StringMatcher.match(String.valueOf(index), String.valueOf(k)))
                            return j;
                    }
                } else {//匹配相应字母对应的item 的position
                    if (StringMatcher.match(String.valueOf(index), String.valueOf(mSections.charAt(i))))
                        return j;
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sections = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++)
            sections[i] = String.valueOf(mSections.charAt(i));
        return sections;
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
    public View getHeaderView(int position, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_sticky_listview_header, null);
        }
        TextView text = StickyViewHolder.get(convertView, R.id.text);
        HDUIOnlineMemberModel model = member_filter.get(position);
        //set header text as first char in name
        String header_char = model.getUiType().toUpperCase(Locale.CHINA).charAt(0) + "";
        if (header_char.equals("A") || header_char.equals("B")) {
            text.setText("管理员");
        } else if (header_char.equals("C")) {
            text.setText("观众");
        } else if (header_char.equals("X")) {
            text.setText("");
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return ((HDUIOnlineMemberModel) getItem(position)).getUiType().toUpperCase(Locale.CHINA).charAt(0);
    }


    @Override
    protected View getView(int position, View convertView) {
        HDUIOnlineMemberModel member = (HDUIOnlineMemberModel) member_filter.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_online_member_activity, null);
        }
        LinearLayout ll_user_name = (LinearLayout) convertView.findViewById(R.id.ll_user_name);
        final ImageView iv_user_icon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
        TextView tv_user_type = (TextView) convertView.findViewById(R.id.tv_user_type);
        final TextView tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
        final TextView tv_user_gender = (TextView) convertView.findViewById(R.id.tv_user_gender);
        final TextView tv_user_des = (TextView) convertView.findViewById(R.id.tv_user_des);
        TextView tv_last_item = (TextView) convertView.findViewById(R.id.tv_last_item);

        if (member.getUiType().endsWith("last")) {//最后一条
            ll_user_name.setVisibility(View.GONE);
            iv_user_icon.setVisibility(View.GONE);
            tv_user_des.setVisibility(View.GONE);
            tv_last_item.setVisibility(View.VISIBLE);
            tv_last_item.setText("- 还有" + member.getUnLoginNum() + "人未登录 -");
            return convertView;
        } else {
            ll_user_name.setVisibility(View.VISIBLE);
            iv_user_icon.setVisibility(View.VISIBLE);
            tv_user_des.setVisibility(View.VISIBLE);
            tv_last_item.setVisibility(View.GONE);
        }

        //正常数据
        tv_user_name.setText(member.nickname + "");

        String header_char = member.getUiType().toUpperCase(Locale.CHINA).charAt(0) + "";
        if (header_char.equals("A")) {
            tv_user_type.setText("频道主");
            tv_user_type.setVisibility(View.VISIBLE);
            tv_user_type.setBackgroundResource(R.drawable.drawable_color_red);
        } else if (header_char.equals("B")) {
            if (member.isOwner()) {
                tv_user_type.setText("频道主");
            } else {
                tv_user_type.setText("管理员");
            }
            tv_user_type.setVisibility(View.VISIBLE);
            tv_user_type.setBackgroundResource(R.drawable.drawable_color_yellow);
        } else if (header_char.equals("C")) {
            tv_user_type.setVisibility(View.GONE);
            tv_user_type.setBackgroundResource(R.color.color_transparency);
        }
        coreService.fetchUserInfo(member.userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (CoreService.OK == e) {
                    User user = (User) data.getSerializable("user");
                    if (user != null) {
                        inflateUserView(user, iv_user_icon, tv_user_gender, tv_user_name, tv_user_des);
                    }
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
        if (!"NONE".equals(user.getSignature())) {
            tv_user_des.setText(user.getSignature());
        }
    }

}