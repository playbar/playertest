package com.rednovo.ace.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.rednovo.ace.net.parser.MyFansListResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.ace.ui.adapter.HomePageFansAdapter;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/2/26.
 */
public class UserFansFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private PullToRefreshListView2 mPullToRefreshListView;
    private HomePageFansAdapter fansAdapter;
    private String userId;
    private List<MyFansListResult.MyFansResult> myFansList = new ArrayList<MyFansListResult.MyFansResult>();

    private int page = 1;
    private final int PAGE_SIZE = 20;
    private ImageView imgNone;
    private LinearLayout llNone;
    private TextView tvNone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(), R.layout.layout_user_fans_fragment, null);
        mPullToRefreshListView = (PullToRefreshListView2) view.findViewById(R.id.lv_user_fans);
        mPullToRefreshListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新的任务
                page = 1;
                reqFans(userId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多的任务
                page++;
                reqFans(userId);
            }
        });

        imgNone = (ImageView) view.findViewById(R.id.img_ptrf_none);
        llNone = (LinearLayout) view.findViewById(R.id.ll_ptrf_none);
        tvNone = (TextView) view.findViewById(R.id.tv_ptrf_none);
        llNone.setOnClickListener(this);

        fansAdapter = new HomePageFansAdapter(getActivity());
        mPullToRefreshListView.setAdapter(fansAdapter);
        mPullToRefreshListView.setOnItemClickListener(this);

        if (getArguments() != null) {
            userId = getArguments().getString(UserZoneActivity.USER_ID);

            reqFans(userId);
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
        MyFansListResult.MyFansResult myFansResult = fansAdapter.getItem(position);

        if (myFansResult != null) {
            if(userId.equals(myFansResult.getUserId())) {
                return;
            }
            userId = myFansResult.getUserId();

            Intent intent = new Intent(getContext(), UserZoneActivity.class);
            intent.putExtra(UserZoneActivity.USER_ID, userId);
            intent.putExtra(Globle.KEY_SHOW_LIVEING_BTN, getArguments().getBoolean(Globle.KEY_SHOW_LIVEING_BTN, true));
            startActivity(intent);
            getActivity().finish();
        }
    }

    public void reqFans(String userId) {
        ReqRelationApi.reqFansList(getActivity(), userId, page + "", PAGE_SIZE + "", new RequestCallback<MyFansListResult>() {
            @Override
            public void onRequestSuccess(MyFansListResult object) {
                if (!isOnCreateView) {
                    return;
                }
                mPullToRefreshListView.onRefreshComplete();

                if (object != null && object.getFansList() != null) {

                    for (int i = 0; i < object.getFansList().size(); i++) {
                        if (object.getFansList().get(i) == null) {
                            object.getFansList().remove(i);
                            i--;
                        }
                    }

                    if (page == 1) {
                        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                        if (object.getFansList().size() == 0) {
                            llNone.setVisibility(View.VISIBLE);
                            imgNone.setImageResource(R.drawable.img_no_content_bg);
                            tvNone.setText(getString(R.string.no_content_message));
                        } else if (object.getFansList() != null) {
                            llNone.setVisibility(View.GONE);
                            myFansList.clear();
                            myFansList.addAll(object.getFansList());
                            fansAdapter.setMyFansList(myFansList);
                            fansAdapter.notifyDataSetChanged();
                        }

                    } else {
                        if (object.getFansList().size() == 0) {
                            --page;
                            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else if (myFansList != null) {
                            myFansList.addAll(object.getFansList());
                            fansAdapter.setMyFansList(myFansList);
                            fansAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onRequestFailure(MyFansListResult error) {
                if (!isOnCreateView) {
                    return;
                }
                mPullToRefreshListView.onRefreshComplete();
                page = 1;
                if(myFansList != null) {
                    myFansList.clear();
                    if (myFansList.size() == 0) {
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

        if(myFansList != null) {
            myFansList.clear();
            myFansList = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ptrf_none:
                reqFans(userId);
                break;
        }
    }
}
