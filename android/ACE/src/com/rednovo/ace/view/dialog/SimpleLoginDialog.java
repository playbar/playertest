package com.rednovo.ace.view.dialog;

import android.content.Context;
import android.content.Intent;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.activity.LoginActivity;

/**
 * Created by Dk on 16/3/18.
 */
public class SimpleLoginDialog extends SimpleDialog{

    public SimpleLoginDialog(final Context context) {
        super(context, new OnSimpleDialogBtnClickListener() {

            @Override
            public void onSimpleDialogLeftBtnClick() {

            }

            @Override
            public void onSimpleDialogRightBtnClick() {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        }, R.string.is_login, R.string.text_cancel, R.string.login_now);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
