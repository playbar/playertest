package com.rednovo.libs.widget.dialog;

import android.content.Context;
import android.view.WindowManager;

import com.rednovo.libs.R;


/**
 * @author Zhen.Li
 * @since 2016-05-17
 */
public class LodingRoomDialog extends BaseDialog {

    public LodingRoomDialog(Context context) {
        super(context, R.layout.layout_room_loading_dialog);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
