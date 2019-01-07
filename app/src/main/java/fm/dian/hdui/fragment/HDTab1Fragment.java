package fm.dian.hdui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fm.dian.hddata.business.model.HDBanner;
import fm.dian.hddata.business.service.core.room.HDRoomCache;
import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.HomePageService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.HomePageRoom;
import fm.dian.hdservice.model.Room;
import fm.dian.hdui.R;
import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
import fm.dian.hdui.activity.HDChannelActivity;
import fm.dian.hdui.activity.adapter.HDTab1ListAdapter;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.fragment.HDTab2Fragment.StateChageType;
import fm.dian.hdui.view.CommonDialog;
import fm.dian.hdui.view.pullllayout.PullRefreshLayout;
import fm.dian.hdui.view.stickyHeader.engin.SimpleSectionedListAdapter;

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class HDTab1Fragment extends HDBaseFragment implements SimpleSectionedListAdapter.CallBack {
    int page = 0;
    int pageMaxCount = 20;//每次加载数量
    View footerView;
    PullRefreshLayout layout;
    ListView mListView;
    HDTab1ListAdapter mAdapter;

    List<HomePageRoom> rooms = new ArrayList<HomePageRoom>();
    List<HDBanner> banners = new ArrayList<HDBanner>();

    boolean isFristLoaded = true;// 解决下拉刷新首次加载完成造成headerview跳动
    boolean noMore = false;
    boolean isWarned = false;//是否提示过没有更多
    boolean isPullRefresh = false;

    private SimpleSectionedListAdapter simpleSectionedGridAdapter;
    private HomePageService homePageService = HomePageService.getInstance();

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab1, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupView();
        checkBaseHandlerEnable();
        (new Handler()).postDelayed(new Runnable() {

            @Override
            public void run() {
                roomList();
            }

        }, 300);
    }


    private void setupView() {
        footerView = getLayoutInflater(getArguments()).inflate(R.layout.item_listview_more, null);
        mListView = (ListView) getView().findViewById(R.id.mListView);
        mListView.addFooterView(footerView, null, false);
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
                page = 0;
                isPullRefresh = true;
                roomList();
                banner();
            }
        });

        mAdapter = new HDTab1ListAdapter(getActivity(), rooms);


        simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), mAdapter);
        HDBaseTabFragmentActivity.self.sections.clear();
        simpleSectionedGridAdapter.setSections(HDBaseTabFragmentActivity.self.sections.toArray(new SimpleSectionedListAdapter.Section[0]));
        simpleSectionedGridAdapter.addCallBackListener(this);
        HDBaseTabFragmentActivity.self.addSectionAdapter(simpleSectionedGridAdapter);

        mListView.setAdapter(simpleSectionedGridAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != rooms && rooms.size() > 0 && position > HDBaseTabFragmentActivity.self.getSelectionSize() - 1) {
                    HomePageRoom homePageRoom = rooms.get(position - HDBaseTabFragmentActivity.self.sections.size());
                    final Room room = mAdapter.getRoom(homePageRoom.getRoomId());
//				Toast.makeText(getActivity(), room.name+"  "+position, Toast.LENGTH_SHORT).show();
                    if (room != null && position > HDBaseTabFragmentActivity.self.getSelectionSize() - 1) {
//					HDBaseTabFragmentActivity.self.enterRoom(room.roomId);

                        AuthService.getInstance().joinRoom(room.getRoomId(), "", new CallBack() {
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
        });
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    //滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        isPullRefresh = false;
                        roomList();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
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

    public void onUserChage(StateChageType type, long userId) {
        if (StateChageType.onChanged == type) {
//			Toast.makeText(getActivity(), "tab1用户状态改变了", Toast.LENGTH_SHORT).show();
            isWarned = true;
            roomList();
        } else if (StateChageType.onCreate == type) {

        } else if (StateChageType.onDelete == type) {

        } else if (StateChageType.onCancelFollow == type) {

        } else if (StateChageType.onAddFollow == type) {

        }
    }

    public void onRoomChage(StateChageType type, long roomId) {
        HomePageRoom tempRoom = null;
        if (StateChageType.onChanged == type) {
//			Toast.makeText(getActivity(), "房间状态改变", Toast.LENGTH_SHORT).show();
            isWarned = true;
            isPullRefresh = true;
            roomList();
        } else if (StateChageType.onCreate == type) {
            HomePageRoom homePageRoom = new HomePageRoom();
            homePageRoom.setRoomId(roomId);
            rooms.add(homePageRoom);
            mAdapter.resetData(rooms);
            mAdapter.notifyDataSetChanged();
        } else if (StateChageType.onDelete == type) {
            for (HomePageRoom homePageRoom : rooms) {
                if (homePageRoom.getRoomId() == roomId) {
                    tempRoom = homePageRoom;
                    break;
                }
            }
            if (tempRoom != null) {
                removeCancelRoom(tempRoom);
            }
        } else if (StateChageType.onCancelFollow == type) {

        } else if (StateChageType.onAddFollow == type) {

        }
    }

    private void removeCancelRoom(HomePageRoom homePageRoom) {
        final HDRoomCache roomCache = new HDRoomCache();
        roomCache.removeRoom(homePageRoom.getRoomId());

        rooms.remove(homePageRoom);
        mAdapter.resetData(rooms);
        mAdapter.notifyDataSetChanged();
    }

    private void alertBlackListMan(){
        new CommonDialog(getActivity(),
                CommonDialog.ButtonType.oneButton,
                new CommonDialog.DialogClickListener() {
                    public void doPositiveClick() {}

                    public void doNegativeClick() {}
                }, "你已被频道管理员拉黑");
    }

    public void roomList() {
        homePageService.fetchRoomList(page * pageMaxCount, pageMaxCount, new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HomePageService.OK == e) {
                    final List<HomePageRoom> tempRooms = (List<HomePageRoom>) data.getSerializable("room_list");
                    if (tempRooms != null && tempRooms.size() > 0) {
                        if (isPullRefresh) {
                            rooms.clear();
                            isPullRefresh = false;
                        }
                        for(HomePageRoom room:tempRooms){
                            if(!rooms.contains(room)){
                                rooms.add(room);
                            }
                        }
                        if (rooms != null && rooms.size() > 0) {
                            mAdapter.resetData(rooms);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (tempRooms.size() < pageMaxCount) {
                            noMore = true;
                        } else {
                            noMore = false;
                        }
                        page++;

                        if (tempRooms != null && tempRooms.size() < pageMaxCount) {
                            footerView.findViewById(R.id.tv_footer).setVisibility(View.VISIBLE);
                            footerView.findViewById(R.id.pb_footer).setVisibility(View.INVISIBLE);
                        } else {
                            footerView.findViewById(R.id.tv_footer).setVisibility(View.INVISIBLE);
                            footerView.findViewById(R.id.pb_footer).setVisibility(View.VISIBLE);
                        }
                        if (rooms.size() < 6) {
                            mListView.removeFooterView(footerView);
                        }
                    }
                }
                cancelRefrash();
            }
        });
    }

    public void banner() {//此版本未开发
        homePageService.fetchBanner(new CallBack() {
            @Override
            public void process(Bundle data) {
                int e = data.getInt("error_code");
                if (HomePageService.OK == e) {

                }
            }
        });
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
