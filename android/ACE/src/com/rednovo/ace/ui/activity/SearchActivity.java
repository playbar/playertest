package com.rednovo.ace.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.parser.SearchResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.adapter.SearchListAdapter;
import com.rednovo.libs.common.KeyBoardUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.ui.base.BaseActivity;

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener,AdapterView.OnItemClickListener {

    private ListView anchorsListView;
    private TextView btnBack;
    private EditText editText;
    private SearchListAdapter searchResultAdapter;
    private ImageView ivProgress;
    private AnimationDrawable animationDrawable;
    private LinearLayout ivEmpty;
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setContentView(R.layout.layout_search);


        anchorsListView = (ListView) findViewById(R.id.lv_anchors_list);
        anchorsListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        btnBack = (TextView) findViewById(R.id.iv_back_btn);
        editText = (EditText) findViewById(R.id.et_search_content);
        ivProgress = (ImageView) findViewById(R.id.pb_progress);
        animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
        ivProgress.setVisibility(View.GONE);
        ivEmpty = (LinearLayout) findViewById(R.id.view_empty);

        editText.setOnEditorActionListener(this);
        btnBack.setOnClickListener(this);
        anchorsListView.setOnItemClickListener(this);

        searchResultAdapter = new SearchListAdapter(this);
        anchorsListView.setAdapter(searchResultAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_btn:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            onSearch(editText.getText().toString().trim());
            KeyBoardUtils.closeKeybord(editText, this);
            // ivProgress.setVisibility(View.VISIBLE);
            // animationDrawable.start();
            return true;
        }
        return false;
    }

    private void onSearch(String mobile) {
        if(StringUtils.isEmpty(mobile)) {
            return;
        }

        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqRelationApi.reqSearchResult(this, mobile, 1, 100, new RequestCallback<SearchResult>() {
            @Override
            public void onRequestSuccess(SearchResult object) {
                if(object != null && object.getUserList().size() > 0) {
                    ivEmpty.setVisibility(View.GONE);
                    searchResultAdapter.setData(object.getUserList());
                } else {
                    searchResultAdapter.setData(null);
                    ivEmpty.setVisibility(View.VISIBLE);
                }

                dismissProgress();
            }

            @Override
            public void onRequestFailure(SearchResult error) {
                dismissProgress();
            }
        });
    }

    private void dismissProgress() {
        animationDrawable.stop();
        ivProgress.setVisibility(View.GONE);
        ShowUtils.dismissProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(System.currentTimeMillis() - lastClickTime < 2000){
            return;
        }
        lastClickTime = System.currentTimeMillis();
        SearchResult.UserListEntity userEntity = searchResultAdapter.getItem(position);

        if(userEntity == null) {
            return;
        }
        Intent intent = new Intent(this, UserZoneActivity.class);
        intent.putExtra(UserZoneActivity.USER_ID, userEntity.getUserId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ivProgress.clearAnimation();
    }

    @Override
    protected void onResume() {
        lastClickTime = 0;
        super.onResume();
    }
}
