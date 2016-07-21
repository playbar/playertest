package com.rednovo.ace.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * 单选项弹框
 * Created by Dk on 16/5/18.
 */
public class SingleOptionDialog extends BaseDialog {

    public interface OnSingleButtonClickListener {
        void onClick();
    }

    private TextView tvTitle;

    private TextView tvContent;

    private Button singleBtn;

    public OnSingleButtonClickListener onSingleButtonClickListener;

    public SingleOptionDialog(Context context) {
        super(context, R.layout.layout_single_option_dialog);
        tvTitle = (TextView) findViewById(R.id.id_single_option_dialog_title);
        tvContent = (TextView) findViewById(R.id.id_single_option_dialog_content);
        singleBtn = (Button) findViewById(R.id.id_single_option_dialog_btn);
        singleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onSingleButtonClickListener != null) {
                    onSingleButtonClickListener.onClick();
                }
            }
        });
    }


    public SingleOptionDialog(Context context, String title, String content, String btnText) {
        this(context);
        tvTitle.setText(title);
        tvContent.setText(content);
        singleBtn.setText(btnText);
    }

    public void setText(String title, String content, String btnText) {
        tvTitle.setText(title);
        tvContent.setText(content);
        singleBtn.setText(btnText);
    }

    public SingleOptionDialog setOnSingleButtonClickListener(OnSingleButtonClickListener onSingleButtonClickListener) {
        this.onSingleButtonClickListener = onSingleButtonClickListener;
        return this;
    }
}
