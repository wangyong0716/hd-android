package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.model.Card;
import fm.dian.hdui.R;
import fm.dian.hdui.util.SysTool;
import fm.dian.hdui.view.BitmapTransform;
import fm.dian.service.blackboard.HDBlackboardCard.CardType;

public class HDChatBlackboardImageAdapter extends BaseAdapter {
    DisplayMetrics metric = new DisplayMetrics();
    LayoutInflater inflater;
    Context context;
    List<Card> dataList = new ArrayList<>();
    Picasso picasso;

    private boolean isSmall = false; //小黑板切换到小窗口
    public HDChatBlackboardImageAdapter(Context context) {
        this.context = context;
        this.picasso = Picasso.with(context);
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        inflater = LayoutInflater.from(context);
    }
    public void blackboardChangeSmall(boolean isSmall){
        this.isSmall =isSmall;
        this.notifyDataSetChanged();
    }

    public void resetData(List<Card> cards) {
        this.dataList = cards;
    }

    public List<Card> getData() {
        return this.dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Card card = dataList.get(position);

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_blackboard_image_adapter, null);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (card.getCardType() == CardType.IMAGE) {//图片
            String imageUrl = card.getData().toString();
            if (imageUrl != null) {
                try {
                    picasso.load(imageUrl)
                            .transform(new BitmapTransform(SysTool.getScreenWidth(context), SysTool.getScreenWidth(context)))
                            .noFade()
                            .resize(SysTool.getScreenWidth(context), SysTool.getScreenWidth(context))
                            .centerInside()
					.into(viewHolder.iv);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            viewHolder.iv.setVisibility(View.VISIBLE);
            viewHolder.tv.setVisibility(View.INVISIBLE);
        } else if (card.getCardType() == CardType.TEXT) {//文本
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.tv.getLayoutParams();
            if(isSmall){
                layoutParams.setMargins(10,25,10,15);
                viewHolder.tv.setTextSize(3);
                viewHolder.tv.setText(card.getData());
            }else{
                layoutParams.setMargins(30,70,30,30);
                viewHolder.tv.setTextSize(18);
                viewHolder.tv.setText(card.getData());
            }
            viewHolder.iv.setVisibility(View.INVISIBLE);
            viewHolder.tv.setVisibility(View.VISIBLE);
        }
        if(isSmall){
            convertView.setBackgroundResource(R.drawable.white_blackboard_rectangle_bg);
        }else{
            convertView.setBackgroundResource(R.color.chat_room_gl_stage_bg_color);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}

