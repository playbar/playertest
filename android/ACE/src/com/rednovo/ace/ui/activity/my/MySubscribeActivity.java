package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.parser.MySubscribeListResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.ace.ui.adapter.MySubscribeAdapter;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订阅
 * Created by Dk on 16/2/25.
 */
public class MySubscribeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int PAGE_SIZE = 10;

    private PullToRefreshListView2 mPullToRefreshListView2;

    private ImageView imgNone;

    private LinearLayout llNone;

    private TextView tvNone;

    private MySubscribeAdapter mAdapter;

    private List<MySubscribeListResult.MySubscribe> mySubscribeList = new ArrayList<MySubscribeListResult.MySubscribe>();

    private int page = 1;

    private View footView;
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_pulltorefresh);

        footView = View.inflate(this, R.layout.layout_pulltorefersh_foot, null);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.my_subscribe);
        findViewById(R.id.back_btn).setOnClickListener(this);

        imgNone = (ImageView) findViewById(R.id.img_ptrf_none);
        llNone = (LinearLayout) findViewById(R.id.ll_ptrf_none);
        tvNone = (TextView) findViewById(R.id.tv_ptrf_none);
        llNone.setOnClickListener(this);
        mPullToRefreshListView2 = (PullToRefreshListView2) findViewById(R.id.ptrl_list);
        mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新的任务

                page = 1;
                getMySubscribe();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多的任务
                ++page;
                getMySubscribe();
            }
        });
        mAdapter = new MySubscribeAdapter(this);
        mPullToRefreshListView2.setAdapter(mAdapter);
        mPullToRefreshListView2.setOnItemClickListener(this);
        footView.setOnClickListener(this);
        getMySubscribe();
    }

    private void getMySubscribe() {
        //ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqRelationApi.reqSubscribeList(this, UserInfoUtils.getUserInfo().getUserId(), page + "", PAGE_SIZE + "", new RequestCallback<MySubscribeListResult>() {
            @Override
            public void onRequestSuccess(MySubscribeListResult object) {
                mPullToRefreshListView2.onRefreshComplete();
                if (object != null && object.getSubscribeList() != null) {
                    if (mPullToRefreshListView2.getRefreshableView().getFooterViewsCount() != 0) {
                        mPullToRefreshListView2.getRefreshableView().removeFooterView(footView);
                    }
                    if (llNone.getVisibility() == View.VISIBLE) {
                        llNone.setVisibility(View.GONE);
                    }
                    if (page == 1) {
                        mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.BOTH);
                        if (object.getSubscribeList().size() == 0) {
                            llNone.setVisibility(View.VISIBLE);
                            imgNone.setImageResource(R.drawable.img_no_content_bg);
                            tvNone.setText(getString(R.string.no_content_message));
                        } else if (mySubscribeList != null) {
                            mySubscribeList.clear();
                            mySubscribeList.addAll(object.getSubscribeList());
                            mAdapter.setMySubscribeList(mySubscribeList);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (object.getSubscribeList().size() == 0) {
                            --page;
                            mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            mPullToRefreshListView2.getRefreshableView().addFooterView(footView);
                        } else if (mySubscribeList != null) {
                            mySubscribeList.addAll(object.getSubscribeList());
                            mAdapter.setMySubscribeList(mySubscribeList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                ShowUtils.dismissProgressDialog();
            }

            @Override
            public void onRequestFailure(MySubscribeListResult error) {
                //ShowUtils.dismissProgressDialog();
                mPullToRefreshListView2.onRefreshComplete();
                if (mySubscribeList.size() == 0) {
                    llNone.setVisibility(View.VISIBLE);
                    tvNone.setText(getString(R.string.no_internet_message));
                    imgNone.setImageResource(R.drawable.img_no_internet_bg);
                }
                //ShowUtils.showToast(error.getErrCode()+error.getErrMsg());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.ll_ptrf_none:
                getMySubscribe();
                break;
            default:

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(System.currentTimeMillis() - lastClickTime < 2000){
            return;
        }
        lastClickTime = System.currentTimeMillis();
        MySubscribeListResult.MySubscribe mySubscribe = mySubscribeList.get(position - mPullToRefreshListView2.getRefreshableView().getHeaderViewsCount());
        if (mySubscribe != null) {
            Intent intent = new Intent(MySubscribeActivity.this, UserZoneActivity.class);
            intent.putExtra(UserZoneActivity.USER_ID, mySubscribe.getUserId());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        lastClickTime = 0;
        super.onResume();
    }
}
