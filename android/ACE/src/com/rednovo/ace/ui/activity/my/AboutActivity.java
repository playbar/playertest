package com.rednovo.ace.ui.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.libs.common.SystemUtils;
import com.rednovo.libs.ui.base.BaseActivity;
import com.rednovo.libs.common.Utils;

/**
 * Created by Dk on 16/2/27.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_about);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.about);
        findViewById(R.id.back_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_about_version_code)).setText(getString(R.string.version_code_text, Utils.getAppVersion(this)));
        findViewById(R.id.tv_about_version_code).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new SimpleDialog(AboutActivity.this, null, insideVersion()).show();
                return false;
            }
        });
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

    private String insideVersion(){
        return Utils.getAppVersion(this) + "_" + Utils.getAppVersionCode(this) + "_" + SystemUtils.getIntegerChannel();
    }
}
