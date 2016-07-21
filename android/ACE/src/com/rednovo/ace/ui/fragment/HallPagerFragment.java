package com.rednovo.ace.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.AlarmUtil;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.HotTimerReceiver;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.net.api.ReqBannerApi;
import com.rednovo.ace.net.api.ReqRoomApi;
import com.rednovo.ace.net.parser.BannerResult;
import com.rednovo.ace.net.parser.HallSubscribeResult;
import com.rednovo.ace.net.parser.HotResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.ace.ui.activity.live.LiveActivity;
import com.rednovo.ace.ui.adapter.HallAnchorsListAdapter;
import com.rednovo.ace.ui.adapter.SubscribeListAdapter;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.ace.widget.LocalImageHolderView;
import com.rednovo.libs.common.NetWorkUtil;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.widget.banner.ConvenientBanner;
import com.rednovo.libs.widget.banner.holder.CBViewHolderCreator;
import com.rednovo.libs.widget.banner.listener.OnItemClickListener;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 主页主播列表界面
 */
public class HallPagerFragment extends LazyFragment implements OnRefreshListener2<ListView>, OnScrollListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public final static String KEY_ID = "key_id";
    public final int HOT_PAGER = 0;
    public final int SUBSCRIBE_PAGER = 1;

    private int mFragmentId;
    private int mPagerType = HOT_PAGER;
    private boolean hasInitData = false;
    private boolean isScrolling = false;
    private boolean isRequesting = false;
    private boolean isPause = false;
    private HallAnchorsListAdapter hallAnchorsListAdapter;
    private SubscribeListAdapter subscribeListAdapter;

    private View hallPagerView;
    private PullToRefreshListView2 ptrlHallPagerList;
    private LinearLayout llSubscribeHint;
    private View llRecommendWords2;
    private View llRecommendWords1;

    private ImageView pbProgress;
    private List<BannerResult.AdEntity> bannerList = new ArrayList<BannerResult.AdEntity>();
    private AnimationDrawable animationDrawable;

    private SimpleLoginDialog simpleLoginDialog;

    private LinearLayout llLastHint;
    private int[] bannerIndicator = new int[]{R.drawable.img_expression_point_normal, R.drawable.img_expression_point_selected};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            mFragmentId = b.getInt(KEY_ID);
            switch (mFragmentId) {
                case HOT_PAGER:
                    mPagerType = HOT_PAGER;
                    break;
                case SUBSCRIBE_PAGER:
                    mPagerType = SUBSCRIBE_PAGER;
                    break;
            }
        }
        super.onCreate(savedInstanceState);
    }

    private ConvenientBanner<BannerResult.AdEntity> convenientBanner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        hallPagerView = inflater.inflate(R.layout.fragment_hall_pager_layout, container, false);

        pbProgress = (ImageView) hallPagerView.findViewById(R.id.pb_progress);
        animationDrawable = (AnimationDrawable) pbProgress.getDrawable();
        animationDrawable.start();
        ptrlHallPagerList = (PullToRefreshListView2) hallPagerView.findViewById(R.id.ptrl_hall_pager_list);
        ptrlHallPagerList.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        ptrlHallPagerList.getRefreshableView().setVisibility(View.GONE);
        ptrlHallPagerList.setMode(Mode.PULL_FROM_START);
        ptrlHallPagerList.setOnRefreshListener(this);
        ptrlHallPagerList.setOnScrollListener(this);
        ptrlHallPagerList.setOnItemClickListener(this);

        View noMoreHint = View.inflate(getActivity(), R.layout.view_no_more_hint, null);
        llLastHint = (LinearLayout) noMoreHint.findViewById(R.id.ll_content_hint);
        llLastHint.setOnClickListener(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ptrlHallPagerList.getRefreshableView().addFooterView(noMoreHint);
        noMoreHint.setLayoutParams(params);

        if (mPagerType == HOT_PAGER) {
            hallAnchorsListAdapter = new HallAnchorsListAdapter(getActivity());
            ptrlHallPagerList.setAdapter(hallAnchorsListAdapter);
        } else {
            llSubscribeHint = (LinearLayout) hallPagerView.findViewById(R.id.ll_subscribe_hint);
            subscribeListAdapter = new SubscribeListAdapter(getActivity());
            ptrlHallPagerList.setAdapter(subscribeListAdapter);
        }

        isPrepared = true;
        lazyLoad();

        AlarmUtil.alarm(40, HotTimerReceiver.class);
        EventBus.getDefault().register(this);
        return hallPagerView;
    }

    private void addSubscribeHint() {
        llRecommendWords1 = View.inflate(getActivity(), R.layout.layout_subscribe_hint, null);

        ptrlHallPagerList.getRefreshableView().addHeaderView(llRecommendWords1);
        llRecommendWords2 = View.inflate(getActivity(), R.layout.layout_subscribe_title, null);
        ptrlHallPagerList.getRefreshableView().addHeaderView(llRecommendWords2);
        ptrlHallPagerList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 2) {
                    llSubscribeHint.setVisibility(View.VISIBLE);
                } else {
                    llSubscribeHint.setVisibility(View.GONE);
                }
            }
        });
        llRecommendWords1.setVisibility(View.VISIBLE);
        llRecommendWords1.findViewById(R.id.rl_hint_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserInfoUtils.isAlreadyLogin()) {
                    return;
                }
                if (simpleLoginDialog == null) {
                    simpleLoginDialog = new SimpleLoginDialog(getContext());
                }
                simpleLoginDialog.show();
            }
        });
    }

    private void initBanner() {
        if (convenientBanner != null) {
            convenientBanner.setVisibility(View.VISIBLE);
            convenientBanner.notifyDataSetChanged();
            return;
        }
        convenientBanner = new ConvenientBanner<BannerResult.AdEntity>(getActivity());
        if (bannerList.size() != 1) {
            convenientBanner.setCanLoop(true);
        } else {
            convenientBanner.setCanLoop(false);
        }
        int bannerHeight = ScreenUtils.getScreenWidth(getActivity()) * 1 / 3;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, bannerHeight);
        ptrlHallPagerList.getRefreshableView().addHeaderView(convenientBanner);
        convenientBanner.setLayoutParams(params);
        if (bannerList.size() != 1) {
            convenientBanner.startTurning(5000);
        }

        convenientBanner.setPages(cbViewHolderCreator, bannerList).setPageIndicator(bannerIndicator)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String bannerUrl = bannerList.get(position).getAddres();
                        if (!TextUtils.isEmpty(bannerUrl)) {
                            Intent intent = new Intent(getActivity(), ACEWebActivity.class);
                            intent.putExtra("url", bannerList.get(position).getAddres());
                            intent.putExtra("INTENT_TITLE", bannerList.get(position).getTitle());
                            getActivity().startActivity(intent);
                        }

                    }
                });

    }

    private CBViewHolderCreator cbViewHolderCreator = new CBViewHolderCreator<LocalImageHolderView>() {

        private LocalImageHolderView localImageHolderView = new LocalImageHolderView();

        @Override
        public LocalImageHolderView createHolder() {

            return localImageHolderView;
        }

    };

    public void onLoadMoreData() {
        onRefreshComplete();
    }

    private void onRefreshData() {
        switch (mPagerType) {
            case HOT_PAGER:
                reqLiveAnchorsList();
                reqBannerList();
                break;
            case SUBSCRIBE_PAGER:
                reqSubscribeList();
                break;
        }

    }

    private void reqBannerList() {
        ReqBannerApi.reqBanner(getActivity(), new RequestCallback<BannerResult>() {
            @Override
            public void onRequestSuccess(BannerResult object) {
                if (!isOnCreateView) {
                    return;
                }
                if (object != null) {
                    if (object.getAd() != null && object.getAd().size() > 0) {

                        bannerList.clear();

                        if (object.getAd() != null && object.getAd().size() > 0) {
                            bannerList.addAll(object.getAd());
                            initBanner();
                        }
                    }
                }
            }

            @Override
            public void onRequestFailure(BannerResult error) {
                if (!isOnCreateView) {
                    return;
                }

                if(isPause) {
                    return;
                }
                if (convenientBanner != null) {
                    convenientBanner.setVisibility(View.GONE);
                    // ptrlHallPagerList.getRefreshableView().removeHeaderView(convenientBanner);
                }
            }
        });
    }

    private void reqLiveAnchorsList() {
        isRequesting = true;
        ReqRoomApi.reqLiveList(this.getActivity(), "1", "100", new RequestCallback<HotResult>() {
            @Override
            public void onRequestSuccess(final HotResult object) {
                hasInitData = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isOnCreateView) {
                            return;
                        }
                        onRefreshComplete();

                        setProgressGone();

                        if (object != null) {
                            if (object.getShowList().size() <= 0) {
                                hallAnchorsListAdapter.setMyEmptyContent(true);
                                llLastHint.setVisibility(View.GONE);
                            } else {
                                llLastHint.setVisibility(View.VISIBLE);
                            }
                            hallAnchorsListAdapter.setData(object.getShowList(), object.getUserList());

                        }

                        ptrlHallPagerList.getRefreshableView().setVisibility(View.VISIBLE);
                    }
                }, 300);
                isRequesting = false;

            }

            @Override
            public void onRequestFailure(HotResult e) {
                if (!isOnCreateView) {
                    return;
                }

                if(isPause) {
                    return;
                }
                hasInitData = false;
                onRefreshComplete();
                isRequesting = false;
                setProgressGone();
                ptrlHallPagerList.getRefreshableView().setVisibility(View.VISIBLE);
                llLastHint.setVisibility(View.GONE);
                hallAnchorsListAdapter.setMyEmptyContent(false);
                hallAnchorsListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setProgressGone() {
        pbProgress.clearAnimation();
        animationDrawable = null;
        hallPagerView.findViewById(R.id.ll_loading_layout).setVisibility(View.GONE);
    }

    private void reqSubscribeList() {
        String userId = "";
        if (UserInfoUtils.isAlreadyLogin()) {
            userId = UserInfoUtils.getUserInfo().getUserId();
        }
        ReqRoomApi.reqHallSubscribeList(getActivity(), userId, new RequestCallback<HallSubscribeResult>() {

            @Override
            public void onRequestSuccess(final HallSubscribeResult object) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onRefreshComplete();
                        setProgressGone();
                        boolean isShowHint = true;


                        if (object != null) {
                            hasInitData = true;

                            List<HallSubscribeResult.ShowListEntity> showList = null;
                            if (object.getShowList().size() > 0) {
                                showList = object.getShowList();
                                isShowHint = false;
                            } else {
                                showList = object.getRecommandList();
                            }

                            if (object.getShowList().size() <= 0 && object.getRecommandList().size() <= 0) {
                                subscribeListAdapter.setMyEmptyContent(true);
                                llLastHint.setVisibility(View.GONE);
                            } else {
                                llLastHint.setVisibility(View.VISIBLE);
                            }

                            subscribeListAdapter.setData(object.getUserList(), showList);
                        }

                        // 根据数据判断是否显示 订阅的主播中没有上线的，没有订阅过
                        if (isShowHint) {
                            if (llRecommendWords1 == null) {
                                addSubscribeHint();
                            }

                            TextView tvHint = (TextView) llRecommendWords1.findViewById(R.id.tv_hint);
                            ImageView ivImgIcon = (ImageView) llRecommendWords1.findViewById(R.id.iv_img_icon);
                            if (!UserInfoUtils.isAlreadyLogin()) {
                                tvHint.setText(getString(R.string.subscribe_no_login));
                                ivImgIcon.setImageResource(R.drawable.subscribe_un_login);
                            } else {
                                tvHint.setOnClickListener(null);
                                if ("0".equals(object.getIfSubscribe())) {
                                    tvHint.setText(getString(R.string.subscribe_ask));
                                    ivImgIcon.setImageResource(R.drawable.subscribe_do_sub);
                                } else {
                                    tvHint.setText(getString(R.string.subscribe_no_liveing));
                                    ivImgIcon.setImageResource(R.drawable.subscribe_to_hot);
                                }
                            }
                        } else {
                            ptrlHallPagerList.getRefreshableView().removeHeaderView(llRecommendWords1);
                            ptrlHallPagerList.getRefreshableView().removeHeaderView(llRecommendWords2);
                            llRecommendWords1 = null;
                            llRecommendWords2 = null;
                        }
                        llSubscribeHint.setVisibility(View.GONE);
                        ptrlHallPagerList.getRefreshableView().setVisibility(View.VISIBLE);
                    }
                }, 300);
            }

            @Override
            public void onRequestFailure(HallSubscribeResult error) {
                if(isPause) {
                    System.out.println("=========后台查询出错");
                    return;
                }
                isRequesting = false;
                hasInitData = false;
                onRefreshComplete();
                setProgressGone();
                ptrlHallPagerList.getRefreshableView().setVisibility(View.VISIBLE);
                llLastHint.setVisibility(View.GONE);
                subscribeListAdapter.setMyEmptyContent(false);
                subscribeListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onRefreshComplete() {
        ptrlHallPagerList.onRefreshComplete();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        onRefreshData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        onLoadMoreData();
    }

    private boolean isPrepared = false;

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || hasInitData) {
            return;
        }

        onRefreshData();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:    // 停止滑动
                isScrolling = false;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:    // 开始滑动
                isScrolling = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!NetWorkUtil.checkNetwork()) {
            return;
        }

        int listViewHeaderCnt = ptrlHallPagerList.getRefreshableView().getHeaderViewsCount();

        if (position < listViewHeaderCnt) {
            return;
        }
        position = position - listViewHeaderCnt;

        Intent intent = new Intent(getActivity(), LiveActivity.class);
        LiveInfo liveInfo = new LiveInfo();
        switch (mPagerType) {
            case HOT_PAGER:
                if (hallAnchorsListAdapter != null && !hallAnchorsListAdapter.isNetAvailable()) {
                    reqBannerList();
                    reqLiveAnchorsList();
                    return;
                }

                Map<String, Object> itemData = hallAnchorsListAdapter.getItem(position);
                if (itemData == null) {
                    return;
                }
                HotResult.Data roomInfo = (HotResult.Data) itemData.get("roomInfo");
                HotResult.User userInfo = (HotResult.User) itemData.get("userInfo");
                if (userInfo != null) {
                    String showImage = userInfo.getShowImg();
                    String nickName = userInfo.getNickName();
                    String profile = userInfo.getProfile();
                    String rank = userInfo.getRank();
                    String sex = userInfo.getSex();
                    String singnature = userInfo.getSignature();
                    liveInfo.setShowImg(TextUtils.isEmpty(showImage) ? "" : showImage);
                    liveInfo.setNickName(TextUtils.isEmpty(nickName) ? "" : nickName);
                    liveInfo.setProfile(TextUtils.isEmpty(profile) ? "" : profile);
                    liveInfo.setRank(TextUtils.isEmpty(rank) ? "" : rank);
                    liveInfo.setSex(TextUtils.isEmpty(sex) ? "" : sex);
                    liveInfo.setSignature(TextUtils.isEmpty(singnature) ? "" : singnature);

                }

                if (roomInfo != null) {
                    String startId = roomInfo.getUserId();
                    String showId = roomInfo.getShowId();
                    String audienceCnt = roomInfo.getMemberCnt();
                    String downStreanUrl = roomInfo.getDownStreamUrl();
                    liveInfo.setStarId(TextUtils.isEmpty(startId) ? "" : startId);
                    liveInfo.setShowId(TextUtils.isEmpty(showId) ? "" : showId);
                    liveInfo.setAudienceCnt(TextUtils.isEmpty(audienceCnt) ? "" : audienceCnt);
                    liveInfo.setDownStreanUrl(TextUtils.isEmpty(downStreanUrl) ? "" : downStreanUrl);
                }

                break;
            case SUBSCRIBE_PAGER:
                if (subscribeListAdapter != null && !subscribeListAdapter.isNetAvailable()) {
                    reqSubscribeList();
                    return;
                }
                Map<String, Object> subscribeData = subscribeListAdapter.getItem(position);
                if (subscribeData == null) {
                    return;
                }
                HallSubscribeResult.ShowListEntity showEntity = (HallSubscribeResult.ShowListEntity) subscribeData.get("roomInfo");
                HallSubscribeResult.UserListEntity userEntity = (HallSubscribeResult.UserListEntity) subscribeData.get("userInfo");
                if (userEntity != null) {
                    String nickName = userEntity.getNickName();
                    String profile = userEntity.getProfile();
                    String rank = userEntity.getRank();
                    String sex = userEntity.getSex();
                    String signature = userEntity.getSignature();
                    liveInfo.setNickName(nickName == null ? "" : nickName);
                    liveInfo.setProfile(profile == null ? "" : profile);
                    liveInfo.setRank(rank == null ? "" : rank);
                    liveInfo.setSex(sex == null ? "" : sex);
                    liveInfo.setSignature(signature == null ? "" : signature);
                }

                if (showEntity != null) {
                    String starId = showEntity.getUserId();
                    String showId = showEntity.getShowId();
                    String audienceCnt = showEntity.getMemberCnt();
                    String downStreanUrl = showEntity.getDownStreamUrl();
                    liveInfo.setStarId(starId == null ? "" : starId);
                    liveInfo.setShowId(showId == null ? "" : showId);
                    liveInfo.setAudienceCnt(audienceCnt == null ? "" : audienceCnt);
                    liveInfo.setDownStreanUrl(downStreanUrl == null ? "" : downStreanUrl);
                }
                break;
        }

        intent.putExtra("live_info", liveInfo);
        getActivity().startActivity(intent);
    }

    /**
     * 收到socket消息
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onReceiveMsg(BaseEvent msg) {
        switch (msg.id) {
            case Globle.KEY_ALARM_REFRESH_HALL:
                if (NetWorkUtil.isNetworkAvailable()) {
                    switch (mPagerType) {
                        case HOT_PAGER:
                            if (isScrolling || isRequesting) {
                                break;
                            }
                            reqBannerList();
                            reqLiveAnchorsList();
                            break;
                        case SUBSCRIBE_PAGER:
                            if (isScrolling || isRequesting) {
                                break;
                            }
                            reqSubscribeList();
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(mFragmentId + "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println(mFragmentId + "onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        System.out.println(mFragmentId + "onHiddenChanged");
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println(mFragmentId + "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println(mFragmentId + "onStop");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println(mFragmentId + "onViewCreated");
    }

    @Override
    public void onClick(View v) {

    }
}
