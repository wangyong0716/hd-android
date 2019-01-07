package fm.dian.hdui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.SubscribeListService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HomePageRoom;
import fm.dian.hdservice.model.Room;
import fm.dian.hdservice.model.SubscribeListRoom;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity.BaseViewPagerListener;
import fm.dian.hdui.activity.HDChannelActivity;
import fm.dian.hdui.activity.adapter.HDTab2ListAdapter;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.net.oldinterface.HDUserCache;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;
import fm.dian.hdui.view.stickyHeader.engin.SimpleSectionedListAdapter;

@SuppressLint("InflateParams")
public class HDTab2Fragment extends HDBaseFragment implements BaseViewPagerListener, SimpleSectionedListAdapter.CallBack {
    int page = 0;
    int pageMaxCount = 20;//每次加载数量
    boolean isFristLoaded = true;// 解决下拉刷新首次加载完成造成headerview跳动
    View footerView;

    PullRefreshLayout layout;
    ListView mListView;
    List<SubscribeListRoom> rooms = new ArrayList<SubscribeListRoom>();
    HDTab2ListAdapter mAdapter;
    boolean isPullRefresh = false;

    SubscribeListService subscribeListService = SubscribeListService.getInstance();
    CoreService coreService = CoreService.getInstance();
    private SimpleSectionedListAdapter simpleSectionedGridAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab2, null);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRecycle", true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupView();

        checkBaseHandlerEnable();
        loadFaveRooms();
    }

    public void onUserChage(StateChageType type, long userId) {
        if (StateChageType.onChanged == type) {
            //Toast.makeText(getActivity(), "tab2用户状态改变了", Toast.LENGTH_SHORT).show();
            isFristLoaded = true;
            reloadListViewData();
        } else if (StateChageType.onCreate == type) {

        } else if (StateChageType.onDelete == type) {

        } else if (StateChageType.onCancelFollow == type) {

        } else if (StateChageType.onAddFollow == type) {

        }
    }

    public void onRoomChage(StateChageType type, long roomId) {
        SubscribeListRoom tempSubRoom = null;
        if (StateChageType.onChanged == type) {
            //Toast.makeText(getActivity(), "tab2房间状态改变", Toast.LENGTH_SHORT).show();
            isPullRefresh = true;
            reloadListViewData();
        } else if (StateChageType.onCreate == type) {
            isPullRefresh = true;
            reloadListViewData();
        } else if (StateChageType.onDelete == type) {
            for (SubscribeListRoom subscribeListRoom : rooms) {
                if (subscribeListRoom.getRoomId() == roomId) {
                    tempSubRoom = subscribeListRoom;
                    break;
                }
            }
            if (tempSubRoom != null) {//本行修复 删除频道闪退
                removeCancelRoom(tempSubRoom);
            }
        } else if (StateChageType.onCancelFollow == type) {
            for (SubscribeListRoom sRoom : rooms) {
                if (sRoom.getRoomId() == roomId) {
                    tempSubRoom = sRoom;
                    break;
                }
            }
            if (tempSubRoom != null) {
                removeCancelRoom(tempSubRoom);
            }
        } else if (StateChageType.onAddFollow == type) {
            isPullRefresh = true;
            reloadListViewData();
        }
    }


    private void setupView() {
        footerView = getLayoutInflater(getArguments()).inflate(R.layout.item_listview_more, null);
        HDBaseTabFragmentActivity.self.addPageListener(this.getClass(), this);
        mListView = (ListView) getView().findViewById(R.id.mListView);
        mListView.addFooterView(footerView);

        mAdapter = new HDTab2ListAdapter(getActivity(), new ArrayList<SubscribeListRoom>());
        simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), mAdapter);
        HDBaseTabFragmentActivity.self.sections.clear();
        simpleSectionedGridAdapter.setSections(HDBaseTabFragmentActivity.self.sections.toArray(new SimpleSectionedListAdapter.Section[0]));
        simpleSectionedGridAdapter.addCallBackListener(this);
        HDBaseTabFragmentActivity.self.addSectionAdapter(simpleSectionedGridAdapter);

        mListView.setAdapter(simpleSectionedGridAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (null != rooms && rooms.size() > 0 && position > HDBaseTabFragmentActivity.self.getSelectionSize() - 1) {
                    final SubscribeListRoom hdSubscribeRoom = rooms.get(position - HDBaseTabFragmentActivity.self.getSelectionSize());
                    final Room room = mAdapter.getRoom(hdSubscribeRoom.getRoomId());
                    if (room != null) {
                        if (room.getIsCanceled()) {
                            final Dialog builder = new Dialog(getActivity(), R.style.HDDialog);
                            builder.setContentView(R.layout.layout_common_dialog_fragment);
                            builder.setCanceledOnTouchOutside(true);
                            builder.show();
                            TextView tv_content = (TextView) builder.findViewById(R.id.content_text_view);
                            tv_content.setText("该频道已被管理员关闭");
                            builder.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    removeCancelRoom(hdSubscribeRoom);
                                    builder.dismiss();
                                }
                            });

                        } else {
                            AuthService.getInstance().joinRoom(hdSubscribeRoom.getRoomId(), "", new CallBack() {
                                @Override
                                public void process(Bundle data) {
                                    int error_code = data.getInt("error_code");
                                    if (AuthService.OK == error_code) {
                                        HDApp.getInstance().setCurrRoomId(room.getRoomId());

                                        Intent channelIntent = new Intent(getActivity(), HDChannelActivity.class);
                                        channelIntent.putExtra("roomId", room.getRoomId());
                                        startActivity(channelIntent);
                                    } else if (AuthService.IN_BLACKLIST == error_code) {
                                        alertBlackListMan();
                                    } else {
                                        Toast.makeText(getActivity(), "error code=" + error_code, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }
                    }
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        isPullRefresh = false;
                        loadFaveRooms();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        layout = (PullRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);//旋转刷新箭头
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        cancelRefrash();
                    }
                }, 5 * 1000);
                reloadListViewData();
            }

        });
    }

    private void removeCancelRoom(final SubscribeListRoom hdSubscribeRoom) {
        long userId = new HDUserCache().getLoginUser().userId;
        coreService.cancelFollow(hdSubscribeRoom.getRoomId(), userId, new CallBack() {
            @Override
            public void process(Bundle data) {
                rooms.remove(hdSubscribeRoom);
                mAdapter.resetData(rooms);
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    private void reloadListViewData() {
        page = 0;
        isPullRefresh = true;
        loadFaveRooms();
    }

    private void cancelRefrash() {
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 300);
    }
    private void alertBlackListMan(){
        new CommonDialog(getActivity(),
                CommonDialog.ButtonType.oneButton,
                new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {}

                    public void doNegativeClick() {}
                }, "你已被频道管理员拉黑");
    }


    private void loadFaveRooms() {//目前不做分页，一次取200条
        subscribeListService.fetchSubscribelist(page * pageMaxCount, pageMaxCount*10, new CallBack() {
            @Override
            public void process(Bundle data) {
                int error_code = data.getInt("error_code");
                if (SubscribeListService.OK == error_code) {
                    ArrayList<SubscribeListRoom> tempList = (ArrayList<SubscribeListRoom>) data.getSerializable("room_list");
                    if (tempList != null && tempList.size() > 0) {
                        if (isPullRefresh) {
                            isPullRefresh = false;
                            rooms.clear();
                            page = 0;
                        }
                        for(SubscribeListRoom room:tempList){
                            if(!rooms.contains(room)){
                                rooms.add(room);
                            }
                        }
                        if (rooms != null && rooms.size() > 0) {
                            mListView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.resetData(rooms);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }, 800);
                            page++;
                        }

                        if (tempList != null && tempList.size() < pageMaxCount) {
                            footerView.findViewById(R.id.tv_footer).setVisibility(View.VISIBLE);
                            footerView.findViewById(R.id.pb_footer).setVisibility(View.INVISIBLE);
                        } else {
                            footerView.findViewById(R.id.tv_footer).setVisibility(View.INVISIBLE);
                            footerView.findViewById(R.id.pb_footer).setVisibility(View.VISIBLE);
                        }
                        if (rooms.size() < pageMaxCount) {
                            mListView.removeFooterView(footerView);
                        }
                    }
                }

                cancelRefrash();
            }
        });
    }


    public enum StateChageType {
        onChanged, onCreate, onDelete, onCancelFollow, onAddFollow
    }

    @Override
    public void onCurrPage() {
        if (rooms != null && rooms.size() == 0) {
            isPullRefresh = true;
            loadFaveRooms();
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onHeaderClick() {
        long roomId = HDApp.getInstance().getCurrRoomId();
        Intent channelIntent = new Intent(getActivity(), HDChannelActivity.class);
        channelIntent.putExtra("roomId", roomId);
        channelIntent.putExtra("jumpChatAct", true);
        startActivity(channelIntent);
    }

    @Override
    public void onHeaderClose() {
        Intent seeding = new Intent(HDBaseTabFragmentActivity.SeedingBarBroadcastReceiver.RECEIVER_KEY);
        seeding.putExtra("type",HDBaseTabFragmentActivity.msg_what_tab1_seeding_cancel);
        getActivity().sendBroadcast(seeding);
    }
}
