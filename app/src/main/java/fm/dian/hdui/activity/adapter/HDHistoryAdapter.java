package fm.dian.hdui.activity.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fm.dian.hdservice.HistoryService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HistoryListElement;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDHistoryActivity;
import fm.dian.hdui.activity.HDShareActivity;
import fm.dian.hdui.util.DateUtil;
import fm.dian.hdui.view.CommonInputDialog;
import fm.dian.hdui.view.CommonMenuDialog;

public class HDHistoryAdapter extends BaseAdapter {
    DisplayMetrics metric = new DisplayMetrics();
    LayoutInflater inflater;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    Context mContext;
    List<HistoryListElement> dataList = new ArrayList<HistoryListElement>();

    boolean isPubType = false;//true：已发布tab   false:未发布tab
    boolean isNormalUser = false;
    HistoryService historyService = HistoryService.getInstance();

    public HDHistoryAdapter(Context context, boolean isPub) {
        this.mContext = context;
        isPubType = isPub;
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        inflater = LayoutInflater.from(context);
    }

    public void setUserType(boolean userType) {
        this.isNormalUser = userType;
    }

    public void resetData(List<HistoryListElement> recordList) {
        this.dataList = recordList;
    }

    public List<HistoryListElement> getData() {
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
        final HistoryListElement record = dataList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_publish_history, null);
            viewHolder.tv_vedio_name = (TextView) convertView.findViewById(R.id.tv_vedio_name);
            viewHolder.tv_play_count = (TextView) convertView.findViewById(R.id.tv_play_count);
            viewHolder.tv_vedio_time = (TextView) convertView.findViewById(R.id.tv_vedio_time);
            viewHolder.tv_publish_time = (TextView) convertView.findViewById(R.id.tv_publish_time);
            viewHolder.tv_pub = (TextView) convertView.findViewById(R.id.tv_pub);
            viewHolder.tv_more = (TextView) convertView.findViewById(R.id.tv_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (isPubType) {//已发表tab
            viewHolder.tv_play_count.setVisibility(View.VISIBLE);
            viewHolder.tv_pub.setVisibility(View.GONE);

        } else {//未发布tab
            viewHolder.tv_play_count.setVisibility(View.GONE);
            viewHolder.tv_pub.setVisibility(View.VISIBLE);

        }
        if (isNormalUser) {
            viewHolder.tv_more.setVisibility(View.GONE);
            viewHolder.tv_publish_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_publish_time.setVisibility(View.GONE);
        }

        if (record == null) {//数据错误
            return convertView;
        }
        viewHolder.tv_vedio_name.setText(record.getName());
        if (record.getPublishTime() != null) {
            viewHolder.tv_publish_time.setText(dateFormat.format(new Date(record.getPublishTime())));
        }
        viewHolder.tv_play_count.setText(" " + record.getPlayCount());
        if (record.getStartTime() != null && record.getEndTime() != null) {
            String formatDuring = DateUtil.formatDuring(new Date(record.getStartTime()), new Date(record.getEndTime()));
            viewHolder.tv_vedio_time.setText(" " + formatDuring);
        }
        viewHolder.tv_pub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                historyService.updateHistory(record.getHistoryId(), record.getName(), true, false, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        int e = data.getInt("error_code");
                        if (HistoryService.OK == e) {
                            upLoadData();
                        } else {
                            Toast.makeText(mContext, "发布失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        viewHolder.tv_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new CommonMenuDialog(mContext, record.getName(), new CommonMenuDialog.DialogClickListener() {
                    @Override
                    public void onItemClick(int functionIndex) {
                        if (functionIndex == 0) {//菜单功能1
                            Intent shareIntent = new Intent(mContext, HDShareActivity.class);
                            shareIntent.putExtra(HDShareActivity.ROOM_ID, record.getRoomId());
                            shareIntent.putExtra(HDShareActivity.LIVE_ID, record.getHistoryId());
                            shareIntent.putExtra("shareUrl", record.getShareUrl());
                            mContext.startActivity(shareIntent);
                            ((Activity) mContext).overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
                        }else if (functionIndex == 1) {//菜单功能1
                            new CommonInputDialog(mContext, CommonInputDialog.ButtonType.TowButton, new CommonInputDialog.DialogClickListener() {//输入名字
                                @Override
                                public void doPositiveClick(String input) {//以 input 为名字发布
                                    historyService.updateHistory(record.getHistoryId(), input, isPubType, false, new CallBack() {
                                        @Override
                                        public void process(Bundle data) {
                                            int e = data.getInt("error_code");
                                            if (HistoryService.OK == e) {
                                                upLoadData();
                                            } else {
                                                Toast.makeText(mContext, "修改标题失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void doNegativeClick() {

                                }
                            }, "");

                        } else if (!isPubType && functionIndex == 2) {// 未发布tab没有撤销功能
                            historyService.updateHistory(record.getHistoryId(), record.getName(), false, true, new CallBack() {
                                @Override
                                public void process(Bundle data) {
                                    int e = data.getInt("error_code");
                                    if (HistoryService.OK == e) {
                                        upLoadData();
                                    } else {
                                        Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else if (functionIndex == 2) {
                            historyService.updateHistory(record.getHistoryId(), record.getName(), false, false, new CallBack() {
                                @Override
                                public void process(Bundle data) {
                                    int e = data.getInt("error_code");
                                    if (HistoryService.OK == e) {
                                        upLoadData();
                                    } else {
                                        Toast.makeText(mContext, "撤销失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else if (functionIndex == 3) {
                            historyService.updateHistory(record.getHistoryId(), record.getName(), false, true, new CallBack() {
                                @Override
                                public void process(Bundle data) {
                                    int e = data.getInt("error_code");
                                    if (HistoryService.OK == e) {
                                        upLoadData();
                                    } else {
                                        Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }, "分享","修改标题", isPubType ? "撤销发布" : null, "删除");
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_vedio_name;
        TextView tv_publish_time;
        TextView tv_play_count;
        TextView tv_vedio_time;
        TextView tv_pub;
        TextView tv_more;
    }

    private void upLoadData() {
        ((HDHistoryActivity) mContext).loadData();
    }
}

