package com.rednovo.ace.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Dk on 16/5/10.
 */
public class FreeGridView extends GridView{

    public FreeGridView(Context context) {
        super(context);
    }

    public FreeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
