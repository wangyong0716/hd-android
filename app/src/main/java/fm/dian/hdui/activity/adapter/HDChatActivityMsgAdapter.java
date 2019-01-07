package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fm.dian.hddata_android.auth.AuthActionRequest;
import fm.dian.hdservice.ActionAuthService;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.GroupChatService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.GroupChat;
import fm.dian.hdservice.model.User;
import fm.dian.hdservice.model.UserOfRole;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDUserActivity;
import fm.dian.hdui.activity.HDWebActivity;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.util.TimeUtil;
import fm.dian.hdui.view.CommonMenuDialog;
import fm.dian.hdui.view.face.FaceConversionUtil;

@SuppressLint("InflateParams")
public class HDChatActivityMsgAdapter extends BaseAdapter {
    ClipboardManager clip;
    Picasso picasso;

    static Boolean initialized = false;
    static String menuTitle = "菜单";

    // use variables to store stings to avoid findViewById
    static String userTxt1 = null;
    static String userTxt2 = null;
    static String adminTxt = null;
    static String channelMasterTxt = null;
    static String nullMsgTxt = null;

    CoreService coreService = CoreService.getInstance();
    ActionAuthService actionAuthService = ActionAuthService.getInstance();
    AuthService authService = AuthService.getInstance();
    GroupChatService groupChatService = GroupChatService.getInstance();
    long loginUserId = new HDUserCache().getLoginUser().userId;

    private final int TYPE_ONE = 0, TYPE_TWO = 1, TYPE_COUNT = 2;
    private List<GroupChat> dataList;
    private LayoutInflater mInflater;
    private Context mContext;
    Map<Long,UserOfRole> keyMap=new HashMap<Long,UserOfRole>();

    public HDChatActivityMsgAdapter(Context context, List<GroupChat> dataList) {
        this.dataList = dataList;
        picasso = Picasso.with(context);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        if (!initialized) {
            menuTitle =  context.getResources().getString(R.string.act_chat_room_menu);
            userTxt1 = context.getResources().getString(R.string.act_chat_room_hd_user1);
            userTxt2 = context.getResources().getString(R.string.act_chat_room_hd_user2);
            adminTxt = context.getResources().getString(R.string.act_chat_room_admin);
            channelMasterTxt = context.getResources().getString(R.string.act_chat_room_channel_master);
            nullMsgTxt = context.getResources().getString(R.string.null_msg);

            initialized = true;
        }
    }
    public void setMenuTitle(String title){
        this.menuTitle =title;
    }


    public void resetData(List<GroupChat> dataList) {
        if(dataList.size() == 20){ // 第一页清空缓存数据
            keyMap.clear();
        }
        this.dataList = dataList;
    }

    public int getCount() {
        return dataList.size();
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


    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        GroupChat g = dataList.get(position);

        if (g != null && g.getUserId()!=null && g.getUserId() == loginUserId) {//自己的消息item
            return TYPE_TWO;
        } else {
            return TYPE_ONE;
        }

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final GroupChat entity = dataList.get(position);
        long lastTimestamp = 0;
        if (position > 0) {
            if (null != dataList.get(position - 1).getTimestamp()) {
                lastTimestamp = dataList.get(position - 1).getTimestamp();
            }
        }
        final int type = getItemViewType(position);

        ViewHolder1 vh1 = null;
        ViewHolder2 vh2 = null;
        if (convertView == null) {
            switch (type) {
                case TYPE_ONE:
                    vh1 = new ViewHolder1();
                    convertView = mInflater.inflate(R.layout.item_chat_activity_msg_other, null);
                    vh1.iv_userhead = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    vh1.tv_user_type = (TextView) convertView.findViewById(R.id.tv_user_type);
//                    vh1.tv_avail = (TextView) convertView.findViewById(R.id.tv_avail);
                    vh1.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                    vh1.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    vh1.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                    convertView.setTag(vh1);
                    break;
                case TYPE_TWO:
                    vh2 = new ViewHolder2();
                    convertView = mInflater.inflate(R.layout.item_chat_activity_msg_self, null);
                    vh2.iv_userhead_me = (ImageView) convertView.findViewById(R.id.iv_userhead_me);
                    vh2.tv_chatcontent_me = (TextView) convertView.findViewById(R.id.tv_chatcontent_me);
                    vh2.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    convertView.setTag(vh2);
                    break;
            }

        } else {
            switch (type) {
                case TYPE_ONE:
                    vh1 = (ViewHolder1) convertView.getTag();
                    vh1.iv_userhead.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            userIconOnClick(entity);
                        }
                    });
                    vh1.tv_chatcontent.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            showMenu(menuTitle, ((TextView) v).getText().toString());
                            return true;
                        }
                    });
                    break;
                case TYPE_TWO:
                    vh2 = (ViewHolder2) convertView.getTag();
                    vh2.iv_userhead_me.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userIconOnClick(entity);
                        }
                    });
                    vh2.tv_chatcontent_me.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showMenu(menuTitle, ((TextView) v).getText().toString());
                            return true;
                        }
                    });
                    break;
            }
        }
        if(entity.getUserId() ==null){
            return convertView;
        }
        String textMsg =entity.getText();
        switch (type) {//设置属性
            case TYPE_ONE:
                if (textMsg == null || "".equals(textMsg)) {
                    vh1.tv_chatcontent.setText(nullMsgTxt);
                } else {
                    if(!urlTextMsgOnClick(entity.getText(),vh1.tv_chatcontent)){
                        FaceConversionUtil.getInstace().getExpressionString(mContext, vh1.tv_chatcontent, entity.getText());
                    }
                }

                if (null != entity.getTimestamp() && entity.getTimestamp() != 0 && (entity.getTimestamp() - lastTimestamp > 300)) {//消息间隔5分钟
                    long timestamp = entity.getTimestamp();
                    String chatTime = TimeUtil.getTime((int) timestamp);
                    vh1.tv_time.setVisibility(View.VISIBLE);
                    vh1.tv_time.setText(chatTime);
                    lastTimestamp = timestamp;
                } else {
                    vh1.tv_time.setVisibility(View.GONE);
                }
                break;
            case TYPE_TWO:
                if (textMsg == null || "".equals(textMsg)) {
                    vh2.tv_chatcontent_me.setText(mContext.getString(R.string.null_msg));
                } else {
                    if(!urlTextMsgOnClick(entity.getText(),vh2.tv_chatcontent_me)){
                        FaceConversionUtil.getInstace().getExpressionString(mContext, vh2.tv_chatcontent_me, entity.getText());
                    }
                }
                if (null != entity.getTimestamp() && entity.getTimestamp() != 0 && (entity.getTimestamp() - lastTimestamp > 300)) {//消息间隔5分钟
                    long timestamp = entity.getTimestamp();
                    String chatTime = TimeUtil.getTime((int) timestamp);
                    vh2.tv_time.setVisibility(View.VISIBLE);
                    vh2.tv_time.setText(chatTime);
                    lastTimestamp = timestamp;
                } else {
                    vh2.tv_time.setVisibility(View.GONE);
                }
                break;
        }

        if (keyMap.keySet().contains(entity.getUserId()) && null != keyMap.get(entity.getUserId())) {
            User currUser = keyMap.get(entity.getUserId()).getUser();
            boolean admin = keyMap.get(entity.getUserId()).isAdmin();
            boolean owner = keyMap.get(entity.getUserId()).isOwner();
            switch (type) {//设置属性
                case TYPE_ONE:
                    if (admin) {//管理员
                        vh1.tv_user_type.setText(" 管理员 ");
                        vh1.tv_user_type.setBackgroundResource(R.drawable.drawable_color_yellow);
                    } else if (owner) {//频道主
                        vh1.tv_user_type.setText(" 频道主 ");
                        vh1.tv_user_type.setBackgroundResource(R.drawable.drawable_color_red);
                    } else {//普通用户
                        vh1.tv_user_type.setText("");
                        vh1.tv_user_type.setBackgroundResource(0);
                    }
                    loadAvatar(currUser.getAvatar(), vh1.iv_userhead);
                    if ("".equals(currUser.getNickname()) || "NONE".equals(currUser.getNickname()) || null == currUser.getNickname()) {
                        if(!admin && !owner){
                            vh1.tv_user_name.setText("红点用户");
                        }else{
                            vh1.tv_user_name.setText("  红点用户");
                        }
                    } else {
                        if(!admin && !owner){
                            vh1.tv_user_name.setText(""+currUser.getNickname());
                        }else{
                            vh1.tv_user_name.setText("  "+currUser.getNickname());
                        }
                    }
                    break;
                case TYPE_TWO:
                    loadAvatar(currUser.getAvatar(), vh2.iv_userhead_me);
                    break;
            }
        } else {
            final ViewHolder1 finalVh = vh1;
            final ViewHolder2 finalVh2 = vh2;
            coreService.fetchUserInfo(entity.getUserId(), new CallBack() {
                @Override
                public void process(Bundle data) {
                    int e = data.getInt("error_code");
                    if (CoreService.OK == e) {
                        User currUser = (User) data.getSerializable("user");
                        UserOfRole userOfRole = new UserOfRole();
                        userOfRole.setUser(currUser);
                        switch (type) { //设置属性
                            case TYPE_ONE:
                                boolean admin = actionAuthService.isRole(entity.getUserId(), HDApp.getInstance().getCurrRoomId(), authService.getLiveId()==null ?0l:authService.getLiveId(), AuthActionRequest.UserAuthType.UserAdmin);
                                boolean owner = actionAuthService.isRole(entity.getUserId(), HDApp.getInstance().getCurrRoomId(), authService.getLiveId()==null ?0l:authService.getLiveId(), AuthActionRequest.UserAuthType.UserOwner);
                                userOfRole.setIsAdmin(admin);
                                userOfRole.setIsOwner(owner);
                                if (admin) {//管理员
                                    finalVh.tv_user_type.setText(" 管理员 ");
                                    finalVh.tv_user_type.setBackgroundResource(R.drawable.drawable_color_yellow);
                                } else if (owner) {//频道主
                                    finalVh.tv_user_type.setText(" 频道主 ");
                                    finalVh.tv_user_type.setBackgroundResource(R.drawable.drawable_color_red);
                                } else {//普通用户
                                    finalVh.tv_user_type.setText("");
                                    finalVh.tv_user_type.setBackgroundResource(0);
                                }
                                loadAvatar(currUser.getAvatar(), finalVh.iv_userhead);
                                if ("".equals(currUser.getNickname()) || "NONE".equals(currUser.getNickname()) || null == currUser.getNickname()) {
                                    if(!admin && !owner){
                                        finalVh.tv_user_name.setText("红点用户");
                                    }else{
                                        finalVh.tv_user_name.setText("  红点用户");
                                    }
                                } else {
                                    if(!admin && !owner){
                                        finalVh.tv_user_name.setText(""+currUser.getNickname());
                                    }else{
                                        finalVh.tv_user_name.setText("  "+currUser.getNickname());
                                    }
                                }
                                break;
                            case TYPE_TWO:
                                loadAvatar(currUser.getAvatar(),finalVh2.iv_userhead_me);
                                break;
                        }
                        keyMap.put(currUser.getUserId(), userOfRole); //缓存列表
                    }

                }
            });
        }
        return convertView;
    }


    private void loadAvatar(String url ,ImageView iv){
        url = url.trim();
        if(url != null
                && !"NONE".equals(url)
                && url.length() != 0){
            picasso.load(url)
                    .placeholder(R.drawable.default_image_load_fail_user)
                    .resize(80, 80)
                    .centerCrop()
                    .into(iv);
        }
    }

    private boolean urlTextMsgOnClick(String contentMsg,final View tv){
        if(contentMsg == null){ return false; }
        String regex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]+?\\.(com|net|cn|me|tw|fr){0,1}[^\u4e00-\u9fa5\\s]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentMsg);

        SpannableString ss = new SpannableString(contentMsg);
        boolean hasUrl = false;
        while(matcher.find()) {
            if(hasUrl == false){
                ((TextView) tv).setText("");//清空文本内容
            }
            hasUrl =true;
            final String url = matcher.group();
            int startIndex = contentMsg.indexOf(url);
            ClickableSpan span1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent webIntent = new Intent(mContext, HDWebActivity.class);
                    webIntent.putExtra("url", url);
                    mContext.startActivity(webIntent);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(mContext.getResources().getColor(R.color.http_url_text_color));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(span1, startIndex, startIndex+url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ((TextView) tv).append(ss);
            ((TextView) tv).setMovementMethod(LinkMovementMethod.getInstance());
            ((TextView) tv).setHighlightColor(Color.TRANSPARENT);
        }
        return  hasUrl;
    }

    private void userIconOnClick(GroupChat groupChat){
        Intent intent = new Intent(mContext, HDUserActivity.class);
        intent.putExtra(HDUserActivity.USER_ID, groupChat.getUserId());
        intent.putExtra(HDUserActivity.ROOM_ID, groupChat.getRoomId());
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private void showMenu(String title, final String str) {
        new CommonMenuDialog(mContext, title, new CommonMenuDialog.DialogClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(int functionIndex) {
                if (functionIndex == 0) {//菜单功能1  复制
                    clip.setText(str); // 复制
                    Toast.makeText(mContext, "已复制", Toast.LENGTH_SHORT).show();
                }
            }
        }, "复制");
    }

    static class ViewHolder1 {
        public ImageView iv_userhead;
        public TextView tv_user_type;
//        public TextView tv_avail;
        public TextView tv_user_name;
        public TextView tv_chatcontent;
        public TextView tv_time;
    }

    static class ViewHolder2 {
        public ImageView iv_userhead_me;
        public TextView tv_chatcontent_me;
        public TextView tv_time;

    }


}
