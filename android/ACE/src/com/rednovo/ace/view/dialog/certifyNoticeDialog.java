package com.rednovo.ace.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.libs.widget.dialog.BaseDialog;

/**
 * 实名认证提示框
 */
public class certifyNoticeDialog extends BaseDialog {

    private TextView tvTitle, tvContent;

    private Button letftBtn;

    private Button rightBtn;
    private View line;
    private String type;
    private String certifyUrl;

    private boolean clickNeedDismiss = true;


    public certifyNoticeDialog(Context context, int resId) {
        super(context, R.layout.layout_certify_dialog);
        tvTitle = (TextView) findViewById(R.id.tv_simple_dialog);
        tvContent = (TextView) findViewById(R.id.tv_content);
        letftBtn = (Button) findViewById(R.id.btn_simeple_dialog_left);
        rightBtn = (Button) findViewById(R.id.btn_simple_dialog_right);
        line = findViewById(R.id.view_line);
        letftBtn.setOnClickListener(onClickListener);
        rightBtn.setOnClickListener(onClickListener);
    }

    /**
     * @param mType :0审核中 1 审核成功 2审核失败;"":尚未认证
     */
    public void setType(String mType) {
        this.type = mType;
        if ("".equals(type)) {
            letftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            tvTitle.setText(getContext().getString(R.string.certify_notice_title));
            tvContent.setText(getContext().getString(R.string.certify_notice_content));
            letftBtn.setText(getContext().getString(R.string.text_cancel));
            rightBtn.setText(getContext().getString(R.string.certify_now));
        } else if ("0".equals(type)) {
            tvTitle.setText(getContext().getString(R.string.certify_notice_doing));
            tvContent.setText(getContext().getString(R.string.certify_notice_link));
            letftBtn.setText(getContext().getString(R.string.tv_sure));
            letftBtn.setBackgroundResource(R.drawable.xml_simple_dialog_longbtn_bg);
            line.setVisibility(View.GONE);
            rightBtn.setVisibility(View.GONE);
        } else if ("1".equals(type)) {
        } else if ("2".equals(type)) {
            letftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
            tvTitle.setText(getContext().getString(R.string.certify_notice_fail));
            tvContent.setText(getContext().getString(R.string.certify_notice_fail_content));
            letftBtn.setText(getContext().getString(R.string.text_cancel));
            rightBtn.setText(getContext().getString(R.string.certify_again));
        }
    }

    public void setCertifyUrl(String mCertifyUrl) {
        this.certifyUrl = mCertifyUrl;
    }

    public void setClickNeedDismiss(boolean clickNeedDismiss) {
        this.clickNeedDismiss = clickNeedDismiss;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_simeple_dialog_left:
                    dismiss();
                    break;
                case R.id.btn_simple_dialog_right:
                    Intent intent = new Intent(getContext(), ACEWebActivity.class);
                    intent.putExtra("url", certifyUrl);
                    getContext().startActivity(intent);
                    dismiss();
                    break;
                default:
                    break;
            }
            if (clickNeedDismiss) {
                dismiss();
            }
        }
    };
}
