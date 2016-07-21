package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.data.db.SqliteHelper;
import com.rednovo.ace.ui.activity.UserZoneActivity;
import com.rednovo.ace.ui.adapter.BrowsingHistoryAdapter;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览历史
 * Created by Dk on 16/2/25.
 */
public class BrowsingHistoryActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private PullToRefreshListView2 mPullToRefreshListView2;

    private ImageView imgNone;

    private LinearLayout llNone;

    private TextView tvNone;

    private BrowsingHistoryAdapter mAdapter;

    private List<LiveInfo> myHistoryList = new ArrayList<LiveInfo>();
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_pulltorefresh);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.browsing_history);
        findViewById(R.id.back_btn).setOnClickListener(this);

        imgNone = (ImageView) findViewById(R.id.img_ptrf_none);
        llNone = (LinearLayout) findViewById(R.id.ll_ptrf_none);
        tvNone = (TextView) findViewById(R.id.tv_ptrf_none);
        mPullToRefreshListView2 = (PullToRefreshListView2) findViewById(R.id.ptrl_list);
        mPullToRefreshListView2.setMode(PullToRefreshBase.Mode.DISABLED);
        mPullToRefreshListView2.setOnItemClickListener(this);
        myHistoryList = SqliteHelper.getInstance().getHistory(UserInfoUtils.getUserInfo().getUserId());
        mAdapter = new BrowsingHistoryAdapter(this, myHistoryList);
        mPullToRefreshListView2.setAdapter(mAdapter);

        if (myHistoryList.size() == 0) {
            llNone.setVisibility(View.VISIBLE);
            imgNone.setImageResource(R.drawable.img_no_content_bg);
            tvNone.setText(getString(R.string.no_content_message));
        }
        llNone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
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
        LiveInfo liveInfo = myHistoryList.get(position - mPullToRefreshListView2.getRefreshableView().getHeaderViewsCount());
        if (liveInfo != null) {
            Intent intent = new Intent(BrowsingHistoryActivity.this, UserZoneActivity.class);
            intent.putExtra(UserZoneActivity.USER_ID, liveInfo.getStarId());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        lastClickTime = 0;
        super.onResume();
    }
}
