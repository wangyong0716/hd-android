package fm.dian.hdui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdui.R;
import fm.dian.hdui.util.SysTool;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class CommonMenuDialog {
    //上下文菜单 可变参数是菜单项,一个字符串表示一个item
    public CommonMenuDialog(Context context, String title, final DialogClickListener listener, String... params) {
        final Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(R.layout.layout_common_menu_dialog);

        TextView tv_title = (TextView) dialog.findViewById(R.id.title_text_view);
        ListView mListView = (ListView) dialog.findViewById(R.id.mListView);
        if (title != null && title.length() > 0) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
            dialog.findViewById(R.id.v_line).setVisibility(View.GONE);
        }

        List<String> items = new ArrayList<String>();
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                items.add(params[i]);
            }
        }

        SimpleAdapter mAdapter = new SimpleAdapter(items, context);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                listener.onItemClick(position);
                dialog.dismiss();
            }
        });
        ;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static interface DialogClickListener {
        public void onItemClick(int functionIndex);

    }

    class SimpleAdapter extends BaseAdapter {
        List<String> items = new ArrayList<String>();
        Context mContext;


        public SimpleAdapter(List<String> items, Context mContext) {
            super();
            this.items = items;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvTextView = new TextView(mContext);
            tvTextView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            tvTextView.setText((String) getItem(position));
            int tvPadding = SysTool.convertDipToPx(mContext, 15);
            tvTextView.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
            tvTextView.setTextSize(16);
            tvTextView.setBackgroundResource(R.drawable.tab3_item_bg_selector);
            return tvTextView;
        }

    }
}