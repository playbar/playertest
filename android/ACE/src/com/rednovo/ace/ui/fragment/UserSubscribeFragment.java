package com.rednovo.ace.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.parser.MySubscribeListResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.ace.ui.adapter.HomePageSubscribeAdapter;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/2/26.
 */
public class UserSubscribeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private PullToRefreshListView2 mPullToRefreshListView;
    private HomePageSubscribeAdapter subscribeAdapter;

    private String userId;

    private int page = 1;
    private final int PAGE_SIZE = 20;

    private List<MySubscribeListResult.MySubscribe> mySubscribeList = new ArrayList<MySubscribeListResult.MySubscribe>();
    private ImageView imgNone;
    private LinearLayout llNone;
    private TextView tvNone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.layout_user_subscribe_fragment, null);
        mPullToRefreshListView = (PullToRefreshListView2) view.findViewById(R.id.lv_user_subscribe);
        mPullToRefreshListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新的任务
                page = 1;
                reqSubscribed(userId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多的任务
                page++;
                reqSubscribed(userId);
            }
        });

        imgNone = (ImageView) view.findViewById(R.id.img_ptrf_none);
        llNone = (LinearLayout) view.findViewById(R.id.ll_ptrf_none);
        tvNone = (TextView) view.findViewById(R.id.tv_ptrf_none);
        llNone.setOnClickListener(this);

        subscribeAdapter = new HomePageSubscribeAdapter(getActivity());
        mPullToRefreshListView.setAdapter(subscribeAdapter);
        mPullToRefreshListView.setOnItemClickListener(this);

        if (getArguments() != null) {
            userId = getArguments().getString(UserZoneActivity.USER_ID);

            reqSubscribed(userId);
        }

        return view;
    }

    public void initData(String userId) {
        this.userId = userId;
        page = 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        MySubscribeListResult.MySubscribe mySubscribe = subscribeAdapter.getItem(position);
        if (mySubscribe != null) {
            if(userId.equals(mySubscribe.getUserId())) {
                return;
            }
            userId = mySubscribe.getUserId();

            Intent intent = new Intent(getContext(), UserZoneActivity.class);
            intent.putExtra(UserZoneActivity.USER_ID, userId);
            intent.putExtra(Globle.KEY_SHOW_LIVEING_BTN, getArguments().getBoolean(Globle.KEY_SHOW_LIVEING_BTN, true));
            startActivity(intent);
            getActivity().finish();
        }
    }

    public void reqSubscribed(String userId) {
        ReqRelationApi.reqSubscribeList(getActivity(), userId, page + "", PAGE_SIZE + "", new RequestCallback<MySubscribeListResult>() {
            @Override
            public void onRequestSuccess(MySubscribeListResult object) {
                if (!isOnCreateView) {
                    return;
                }
                mPullToRefreshListView.onRefreshComplete();

                if (object != null && object.getSubscribeList() != null) {

                    for (int i = 0; i < object.getSubscribeList().size(); i++) {
                        if (object.getSubscribeList().get(i) == null) {
                            object.getSubscribeList().remove(i);
                            i--;
                        }
                    }

                    if (page == 1) {
                        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                        if (object.getSubscribeList().size() == 0) {
                            llNone.setVisibility(View.VISIBLE);
                            imgNone.setImageResource(R.drawable.img_no_content_bg);
                            tvNone.setText(getString(R.string.no_content_message));
                        } else if (object.getSubscribeList() != null) {
                            llNone.setVisibility(View.GONE);
                            mySubscribeList.clear();
                            mySubscribeList.addAll(object.getSubscribeList());
                            subscribeAdapter.setMySubscribeList(mySubscribeList);
                            subscribeAdapter.notifyDataSetChanged();
                        }

                    } else {
                        if (object.getSubscribeList().size() == 0) {
                            --page;
                            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else if (mySubscribeList != null) {
                            mySubscribeList.addAll(object.getSubscribeList());
                            subscribeAdapter.setMySubscribeList(mySubscribeList);
                            subscribeAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onRequestFailure(MySubscribeListResult error) {
                if (!isOnCreateView) {
                    return;
                }
                mPullToRefreshListView.onRefreshComplete();
                page = 1;
                if(mySubscribeList != null) {
                    mySubscribeList.clear();
                    if (mySubscribeList.size() == 0) {
                        imgNone.setImageResource(R.drawable.img_no_internet_bg);
                        tvNone.setText(getString(R.string.no_internet_message));
                        llNone.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mySubscribeList != null) {
            mySubscribeList.clear();
            mySubscribeList = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ptrf_none:
                reqSubscribed(userId);
                break;
        }
    }
}
