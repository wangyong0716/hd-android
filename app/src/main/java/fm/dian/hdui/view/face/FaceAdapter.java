package fm.dian.hdui.view.face;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import fm.dian.hdui.R;

@SuppressWarnings({"deprecation", "unused"})
public class FaceAdapter extends BaseAdapter {

    private List<ChatEmoji> data;

    private LayoutInflater inflater;
    private Context mContext;

    private int size = 0;

    public FaceAdapter(Context context, List<ChatEmoji> list) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
        mContext = context;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatEmoji emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_chat_activity_bottom_bar_face, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (emoji.getId() == R.drawable.face_del_icon) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageResource(emoji.getId());
        } else if (TextUtils.isEmpty(emoji.getCharacter())) {
            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageDrawable(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setImageResource(emoji.getId());
        }

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}