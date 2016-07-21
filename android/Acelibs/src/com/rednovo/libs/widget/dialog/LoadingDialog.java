package com.rednovo.libs.widget.dialog;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rednovo.libs.R;
import com.rednovo.libs.common.StringUtils;

/**
 * 等待框
 * Created by Dk on 16/3/9.
 */
public class LoadingDialog extends BaseDialog {

    public LoadingDialog(Context context) {
        super(context, R.layout.layout_loading_dialog);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0;
        getWindow().setAttributes(layoutParams);
        AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.id_loading_img)).getDrawable();
        animationDrawable.start();
    }

    /**
     * 设置loading中的提示语, 默认无提示语
     *
     * @param content content
     */
    public void setLoadingText(String content) {
        if (!StringUtils.isEmpty(content)) {
            ((TextView) findViewById(R.id.tv_content_text)).setText(content);
        }
    }

    @Override
    public void dismiss() {
        findViewById(R.id.id_loading_img).clearAnimation();
        super.dismiss();
    }
}
