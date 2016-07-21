package com.rednovo.libs.widget.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rednovo.libs.R;
import com.rednovo.libs.common.ShowUtils;

import java.util.ArrayList;

/**
 * Created by CG on 13-12-3.
 *
 * @author ll
 * @version 3.2.0
 */
public class ExpressionPanel extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private ExpressionViewPager mExpression;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();

    /**
     * ExpressionPanel
     *
     * @param context context
     */
    public ExpressionPanel(Context context) {
        super(context);
        init(context);
    }

    /**
     * ExpressionPanel
     *
     * @param context context
     * @param attrs   attrs
     */
    public ExpressionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init(context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void init(Context context) {
        removeAllViewsInLayout();

        mExpression = new ExpressionViewPager(getContext());
        mExpression.setOnPageChangeListener(this);
        HorizontalElasticityContainerView containerView = new HorizontalElasticityContainerView(getContext());
        containerView.setContentView(mExpression);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        addViewInLayout(containerView, -1, lp);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        final int marginBottom = ShowUtils.dip2px(context, 12);
        lp.setMargins(0, 0, 0, marginBottom);
        addViewInLayout(linearLayout, -1, lp);

        initPoint(linearLayout);
    }

    private void initPoint(LinearLayout linearLayout) {
        final int commonSize = 10;
        ImageView imageView;
        for (int i = 0; i < mExpression.getAdapter().getCount(); i++) {
            imageView = new ImageView(getContext());
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.img_expression_point_selected);
            } else {
                imageView.setBackgroundResource(R.drawable.img_expression_point_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = commonSize;
            layoutParams.rightMargin = commonSize;
            layoutParams.width = commonSize;
            layoutParams.height = commonSize;
            linearLayout.addView(imageView, layoutParams);
            mPointViews.add(imageView);
        }
    }

    /**
     * 设置输入框
     * @param textView 输入框
     * @param showFirstFrameOnly
     * @param view 发送按钮
     */
    public void setTextView(EditText textView, boolean showFirstFrameOnly,View view) {
        mExpression.setTextView(textView, showFirstFrameOnly,view);
    }

    /**
     * 设置长度
     *
     * @param maxLength 长度
     */
    public void setMaxLength(int maxLength) {
        mExpression.setMaxLength(maxLength);
    }

    @Override
    public void onPageSelected(int position) {
        drawPoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    private void drawPoint(int index) {
        for (int i = 0; i < mPointViews.size(); i++) {
            mPointViews.get(i).setBackgroundResource(index == i ? R.drawable.img_expression_point_selected : R.drawable.img_expression_point_normal);
        }
    }
}
