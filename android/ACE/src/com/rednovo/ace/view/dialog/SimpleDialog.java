package com.rednovo.ace.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * Created by Dk on 16/3/12.
 */
public class SimpleDialog extends BaseDialog {

    public interface OnSimpleDialogBtnClickListener {
        void onSimpleDialogLeftBtnClick();
        void onSimpleDialogRightBtnClick();
    }

    private OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener;

    private TextView tvTitle;

    private Button letftBtn;

    private Button rightBtn;

    private boolean clickNeedDismiss = true;

    public SimpleDialog(Context context, OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener) {
        super(context, R.layout.layout_simple_dialog);
        this.onSimpleDialogBtnClickListener = onSimpleDialogBtnClickListener;

        tvTitle = (TextView) findViewById(R.id.tv_simple_dialog);
        letftBtn = (Button) findViewById(R.id.btn_simeple_dialog_left);
        rightBtn = (Button) findViewById(R.id.btn_simple_dialog_right);
        letftBtn.setOnClickListener(onClickListener);
        rightBtn.setOnClickListener(onClickListener);
    }

    public SimpleDialog(Context context, OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener, int title){
        this(context, onSimpleDialogBtnClickListener);
        tvTitle.setText(context.getString(title));
    }

    public SimpleDialog(Context context, OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener, String title){
        this(context, onSimpleDialogBtnClickListener);
        tvTitle.setText(title);
    }

    public SimpleDialog(Context context, OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener, int title, int leftBtn, int rightbtn){
        this(context, onSimpleDialogBtnClickListener, title);
        letftBtn.setText(context.getString(leftBtn));
        rightBtn.setText(context.getString(rightbtn));
    }

    public SimpleDialog(Context context, OnSimpleDialogBtnClickListener onSimpleDialogBtnClickListener, String title, String leftBtn, String rightbtn){
        this(context, onSimpleDialogBtnClickListener, title);
        letftBtn.setText(leftBtn);
        rightBtn.setText(rightbtn);
    }

    public SimpleDialog(Context context){
        this(context, null);
    }

    public void setClickNeedDismiss(boolean clickNeedDismiss){
        this.clickNeedDismiss = clickNeedDismiss;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_simeple_dialog_left:
                    if(onSimpleDialogBtnClickListener != null){
                        onSimpleDialogBtnClickListener.onSimpleDialogLeftBtnClick();
                    }
                    break;
                case R.id.btn_simple_dialog_right:
                    if(onSimpleDialogBtnClickListener != null){
                        onSimpleDialogBtnClickListener.onSimpleDialogRightBtnClick();
                    }
                    break;
            }
            if(clickNeedDismiss){
                dismiss();
            }
        }
    };

    public void setTitleText(String text) {
        tvTitle.setText(text);
    }

    public void setLeftBtnText(String text) {
        letftBtn.setText(text);
    }

    public void setRightText(String text) {
        rightBtn.setText(text);
    }
}
