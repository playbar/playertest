package com.rednovo.ace.view.dialog;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.UpdateAttacher;
import com.rednovo.libs.widget.dialog.BaseDialog;

import java.io.File;

/**
 * Created by Dk on 16/5/16.
 */
public class UpdateVersionDialog extends BaseDialog {

    private Context context;

    private TextView title;

    private TextView content;

    private Button leftBtn;

    private Button rightBtn;

    private String version;

    private String description;

    private String url;

    private UpdateAttacher.UpdateDialogOnClickListener updateDialogOnClickListener;

    private boolean isForcibly = false;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(updateDialogOnClickListener != null){
                updateDialogOnClickListener.onClick(v);
            }
            switch (v.getId()) {
                case R.id.id_update_dialog_left_btn:
                    dismiss();
                    break;
                case R.id.id_update_dialog_right_btn:
                    if(isForcibly){
                        rightBtn.setText(context.getString(R.string.downloading));
                    }else{
                        dismiss();
                    }
                    break;
            }
        }
    };

    private UpdateVersionDialog(Context context) {
        super(context, R.layout.layout_update_version_dialog);
        setCancelable(false);//禁止点击返回键消失
        setCanceledOnTouchOutside(false);//禁止点击屏幕其他地方消失

        title = (TextView) findViewById(R.id.id_update_dialog_title);
        content = (TextView) findViewById(R.id.id_update_dialog_content);
        leftBtn = (Button) findViewById(R.id.id_update_dialog_left_btn);
        rightBtn = (Button) findViewById(R.id.id_update_dialog_right_btn);
        leftBtn.setOnClickListener(onClickListener);
        rightBtn.setOnClickListener(onClickListener);
    }

    /**
     * @param context 上下文，建议传activity对象
     * @param version 升级的版本号
     * @param description 新版本描述
     * @param isForcibly 是否是强制更新，如是则隐藏取消按钮
     */
    public UpdateVersionDialog(Context context, String version, String description, String url, boolean isForcibly, UpdateAttacher.UpdateDialogOnClickListener updateDialogOnClickListener){
        this(context);
        this.context = context;
        this.version = version;
        this.description = description;
        this.isForcibly = isForcibly;
        this.url = url;
        this.updateDialogOnClickListener = updateDialogOnClickListener;
        title.setText(context.getString(R.string.find_new_version, version));
        content.setText(description);
        if(isForcibly){
            leftBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
