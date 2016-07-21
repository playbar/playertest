package com.rednovo.ace.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rednovo.ace.R;

/**
 * TODO: document your custom view class.
 */
public class StrokeTextView extends TextView {
    private TextView borderText = null;///用于描边的TextView

    public StrokeTextView(Context context) {
        super(context);
        borderText = new TextView(context);
        init();
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context, attrs);
        init();
    }

    public StrokeTextView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        borderText = new TextView(context, attrs, defStyle);
        init();
    }

    public void init() {
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(4);                                  //设置描边宽度
        tp1.setStyle(Paint.Style.STROKE);                             //对文字只描边
        borderText.setTextColor(getResources().getColor(R.color.color_fff100));  //设置描边颜色
        borderText.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        borderText.draw(canvas);
        super.onDraw(canvas);
    }

}
