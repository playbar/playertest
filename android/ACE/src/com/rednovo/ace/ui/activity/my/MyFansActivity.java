
package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.parser.MyFansListResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.ace.ui.adapter.MyFansAdapter;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的粉丝
 * Created by Dk on 16/2/25.
 */
public class MyFansActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    private static final int PAGE_SIZE = 10;

    private PullToRefreshListView2 mPullToRefreshListView2;

    private ImageView imgNone;

    private LinearLayout llNone;

    private TextView tvNone;

    private MyFansAdapter mAdapter;

    private List<MyFansListResult.MyFansResult> myFansList = new ArrayList<MyFansListResult.MyFansResult>();

    private int page = 1;

    private View footView;
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_pulltorefresh);

        footView = View.inflate(this, R.layout.layout_pulltorefersh_foot, null);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.my_fans);
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
                getMyFans();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多的任务
                ++page;
                getMyFans();
            }
        });
        mPullToRefreshListView2.setOnItemClickListener(this);
        footView.setOnClickListener(this);
        mAdapter = new MyFansAdapter(this);
        mPullToRefreshListView2.setAdapter(mAdapter);
        getMyFans();
    }

    private void getMyFans() {
        //ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqRelationApi.reqFansList(this, UserInfoUtils.getUserInfo().getUserId(), page + "", PAGE_SIZE + "", new RequestCallback<MyFansListResult>() {
            @Override
            public void onRequestSuccess(MyFansListResult object) {
                mPullToRefreshListView2.onRefreshComplete();
                if (object != null && object.getFansList() != null) {
                    if (mPullToRefreshListView2.getRefreshableView().getFooterViewsCount() != 0) {
                        mPullToRefreshListView2.getRefreshableView().removeFooterView(footView);
                    }
                    if (llNone.getVisibility() == View.VISIBLE) {
                        llNone.setVisibility(View.GONE);
                    }
                    if (page == 1) {
                        mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.BOTH);
                        if (object.getFansList().size() == 0) {
                            llNone.setVisibility(View.VISIBLE);
                            imgNone.setImageResource(R.drawable.img_no_content_bg);
                            tvNone.setText(getString(R.string.no_content_message));
                        } else if (myFansList != null) {
                            myFansList.clear();
                            myFansList.addAll(object.getFansList());
                            mAdapter.setMyFansList(myFansList);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (object.getFansList().size() == 0) {
                            --page;
                            mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            mPullToRefreshListView2.getRefreshableView().addFooterView(footView);
                        } else if (myFansList != null) {
                            myFansList.addAll(object.getFansList());
                            mAdapter.setMyFansList(myFansList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                ShowUtils.dismissProgressDialog();
            }

            @Override
            public void onRequestFailure(MyFansListResult error) {
                //ShowUtils.dismissProgressDialog();
                mPullToRefreshListView2.onRefreshComplete();
                if (myFansList.size() == 0) {
                    imgNone.setImageResource(R.drawable.img_no_internet_bg);
                    tvNone.setText(getString(R.string.no_internet_message));
                    llNone.setVisibility(View.VISIBLE);
                }
                //ShowUtils.showToast(error.getErrCode() + error.getErrMsg());
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
                getMyFans();
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
        MyFansListResult.MyFansResult myFansResult = myFansList.get(position - mPullToRefreshListView2.getRefreshableView().getHeaderViewsCount());
        if (myFansResult != null) {
            Intent intent = new Intent(MyFansActivity.this, UserZoneActivity.class);
            intent.putExtra(UserZoneActivity.USER_ID, myFansResult.getUserId());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        lastClickTime = 0;
        super.onResume();
    }
}
