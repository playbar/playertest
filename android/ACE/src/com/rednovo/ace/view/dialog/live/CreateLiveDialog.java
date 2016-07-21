/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.view.dialog.live;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.activity.PhotoEditActivity;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * @author Zhen.Li
 * @fileNmae CreateLiveDialog
 * @since 2016-03-03
 */
public class CreateLiveDialog extends BaseDialog implements View.OnClickListener {

    private static CreateLiveDialog mInstance;
    private Activity mContext;

    public static CreateLiveDialog getDialog(Activity context, float dimAmount) {
        if (mInstance == null) {
            mInstance = new CreateLiveDialog(context, dimAmount);
        }
        return mInstance;
    }

    public CreateLiveDialog(Activity context, float dimAmount) {
        super(context, R.layout.dialog_create_live_bottom, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.dialogBottomWindowAnim);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = dimAmount;
        getWindow().setAttributes(layoutParams);
        this.mContext = context;
        mInstance = this;
        findViewById(R.id.btn_take_photo).setOnClickListener(this);
        findViewById(R.id.btn_select_photo).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        mInstance = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                redirect(PhotoEditActivity.TAKE_PIC);
                dismiss();
                break;
            case R.id.btn_select_photo:
                redirect(PhotoEditActivity.GET_PIC);
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void redirect(String type) {
        Intent intent = new Intent(mContext, PhotoEditActivity.class);
        intent.putExtra(PhotoEditActivity.OPTION_TAG, type);
        mContext.startActivityForResult(intent, 0X100);
    }
}
