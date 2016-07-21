package com.rednovo.ace.ui.activity.my;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.adapter.WithdrawalsRecordsAdapter;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;

/**
 * 提现记录
 * Created by Dk on 16/3/9.
 */
public class WithdrawalsRecordsActivity extends BaseActivity{

    private PullToRefreshListView2 ptrlList;

    private WithdrawalsRecordsAdapter mWithdrawalsRecordsAdapter;

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.layout_pulltorefresh);

        ptrlList = (PullToRefreshListView2) findViewById(R.id.ptrl_list);

        mWithdrawalsRecordsAdapter = new WithdrawalsRecordsAdapter(this);
        ptrlList.setAdapter(mWithdrawalsRecordsAdapter);
    }
}
