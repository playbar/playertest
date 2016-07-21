package com.rednovo.ace.ui.activity.my;

import android.os.Bundle;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.adapter.ConsumptionRecordsAdapter;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshListView2;


/**
 * 消费记录
 * Created by Dk on 16/2/26.
 */
public class ConsumptionRecordsActivity extends BaseActivity {

    private PullToRefreshListView2 ptrlList;

    private ConsumptionRecordsAdapter mConsumptionRecordsAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_pulltorefresh);

        ptrlList = (PullToRefreshListView2) findViewById(R.id.ptrl_list);

        mConsumptionRecordsAdapter = new ConsumptionRecordsAdapter(this);
        ptrlList.setAdapter(mConsumptionRecordsAdapter);
    }
}
