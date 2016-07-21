package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.libs.R;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.widget.emoji.HorizontalElasticityContainerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物View
 */
public class GiftView extends RelativeLayout implements ViewPager.OnPageChangeListener, GiftViewPager.onGiftCheckChangedListener {

    private GiftViewPager mGiftVPager;
    private Context mContext;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private onItemCheckListener listener;

    public void setGifts(List<GiftListResult.GiftListEntity> gifts) {
        this.gifts = gifts;
        setData(mContext);
    }

    private List<GiftListResult.GiftListEntity> gifts;

    public GiftView(Context context) {
        super(context);
        init(context);
    }

    public GiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init(context);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void init(Context context) {
        removeAllViewsInLayout();
        mContext = context;

    }

    private void setData(Context context) {
        mGiftVPager = new GiftViewPager(getContext(), gifts);
        mGiftVPager.setOnPageChangeListener(this);
        mGiftVPager.setListener(this);
        HorizontalElasticityContainerView containerView = new HorizontalElasticityContainerView(getContext());
        containerView.setContentView(mGiftVPager);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.leftMargin = 15;
        lp.rightMargin = 15;
        addViewInLayout(containerView, -1, lp);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        //int marginBottom = ShowUtils.dip2px(context, 8);
        lp.setMargins(0, 0, 0, 0);
        addViewInLayout(linearLayout, -1, lp);

        initPoint(linearLayout);
    }

    private void initPoint(LinearLayout linearLayout) {
        final int commonSize = 10;
        ImageView imageView;
        int count = mGiftVPager.getAdapter().getCount();
        if (count <= 1)
            return;
        for (int i = 0; i < count; i++) {
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

    @Override
    public void onGiftCheckChange(GiftCheckButton button, boolean isChecked) {
        if (listener != null) {
            listener.onItemCheck(button, isChecked);
        }
    }

    public interface onItemCheckListener {
        void onItemCheck(GiftCheckButton button, boolean isChecked);
    }

    public void setListener(onItemCheckListener listener) {
        this.listener = listener;
    }
}
