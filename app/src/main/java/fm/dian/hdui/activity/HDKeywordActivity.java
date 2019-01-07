package fm.dian.hdui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.GroupChatService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.KeyWord;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.view.CommonInputDialog;
import fm.dian.hdui.view.CommonMenuDialog;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;

/**
 * ******************
 * fileName  :HDHistoryActivity.java
 * author    : song
 * createTime:2015-3-30 下午4:09:03
 * fileDes   :
 */
@SuppressLint("InflateParams")
public class HDKeywordActivity extends HDBaseActivity {
    private PullRefreshLayout layout;
    private ListView lv_member;
    private TextView tv_des;
    private KeyWordAdapter mAdapter;

    private GroupChatService groupChatService = GroupChatService.getInstance();

    public long liveId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_keyword_activity);
        initUI();

        loadKeywords();
    }

    private void loadKeywords() {
        groupChatService.getKeywords(liveId, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (GroupChatService.OK == e) {
                    List<KeyWord> keyWordList = (List<KeyWord>) data.getSerializable("keyword_list");
                    if (keyWordList != null) {
                        mAdapter.resetData(keyWordList);
                        mAdapter.notifyDataSetChanged();
                        if (keyWordList.size() == 0) {
                            tv_des.setText("还没有关键字，请点击右上角\"添加\"");
                        } else {
                            tv_des.setText("已添加" + keyWordList.size() + "个关键字");
                        }
                    } else {
                        keyWordList = new ArrayList<KeyWord>();
                        mAdapter.resetData(keyWordList);
                        mAdapter.notifyDataSetChanged();
                        tv_des.setText("还没有关键字，请点击右上角\"添加\"");
                    }
                }
            }
        });
    }

    private void cancelRefrash() {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 300);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_common_action_bar_right:
                new CommonInputDialog(this, CommonInputDialog.ButtonType.TowButton, new CommonInputDialog.DialogClickListener() {//输入名字
                    public void doPositiveClick(String input) {
                        addKeyword(input);
                    }

                    public void doNegativeClick() {
                    }
                }, "添加关键词");
                break;
            default:
                break;
        }
    }

    private void addKeyword(String keyword) {
        groupChatService.addKeyword(liveId, keyword, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (GroupChatService.OK == e) {
                    Toast.makeText(HDKeywordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    loadKeywords();
                    layout.invalidate();

                }
            }
        });
    }

    private void removeKeyword(long id) {
        groupChatService.removeKeywords(id, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (GroupChatService.OK == e) {
                    Toast.makeText(HDKeywordActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadKeywords();
                    layout.invalidate();
                }
            }
        });
    }

    @Override
    public void initUI() {
        super.initActionBar(this);
        tv_common_action_bar_right.setText("添加");
        tv_common_action_bar_right.setBackgroundResource(R.drawable.btn_common_red_white_bg_selector_small);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, 15, 0);
        tv_common_action_bar_right.setLayoutParams(lp);
        setActionBarTitle("关键词过滤");
        liveId = getIntent().getLongExtra("liveId", 0);

        mAdapter = new KeyWordAdapter(this);

        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_member = (ListView) findViewById(R.id.lv_member);
        lv_member.setAdapter(mAdapter);
        lv_member.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new CommonMenuDialog(HDKeywordActivity.this, "关键词操作", new CommonMenuDialog.DialogClickListener() {
                    @Override
                    public void onItemClick(int functionIndex) {
                        if (functionIndex == 0) {//菜单功能1
                            KeyWord keyword = (KeyWord) mAdapter.getItem(position);
                            removeKeyword(keyword.getId());
                        }
                    }
                }, "删除");

                return false;
            }
        });

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadKeywords();
                cancelRefrash();
            }
        });
    }

    public class KeyWordAdapter extends BaseAdapter {
        private Context mContext;
        List<KeyWord> dataList = new ArrayList<>();

        public KeyWordAdapter(Context context) {
            super();
            this.mContext = context;
        }

        public void resetData(List<KeyWord> keyWordList) {
            this.dataList = keyWordList;
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            KeyWord keyWord = dataList.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_sample_arrayadaper, null);
            }
            TextView tv_des = (TextView) convertView.findViewById(R.id.tv_txt);
            tv_des.setText(keyWord.getWord());

            return convertView;
        }
    }
}
