package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.util.AttributeSet;

import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.libs.ui.adapter.ViewPagerAdapter;
import com.rednovo.libs.widget.emoji.NestedViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CG on 13-12-3.
 *
 * @author ll
 * @version 3.2.0
 */
public class GiftViewPager extends NestedViewPager implements GiftGridView.onCheckChangedListener {

    private static final int NUM_PER_PAGER = 8;

    private List<GiftGridView> mGridViewList;

    private List<GiftListResult.GiftListEntity> gifts;
    private onGiftCheckChangedListener listener;

    public GiftViewPager(Context context) {
        super(context);
        init();
    }

    public GiftViewPager(Context context, List<GiftListResult.GiftListEntity> gifts) {
        super(context);
        this.gifts = gifts;
        init();
    }

    public GiftViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int pageCount = (int) Math.ceil(((double) gifts.size() / NUM_PER_PAGER));
        mGridViewList = new ArrayList<GiftGridView>(pageCount);
        for (int i = 0; i < pageCount; i++) {
            GiftGridView gridView = new GiftGridView(getContext());
            gridView.setListener(this);
            int start = i * NUM_PER_PAGER;
            int end = Math.min(start + NUM_PER_PAGER, gifts.size());
            List<GiftListResult.GiftListEntity> usGifts = gifts.subList(start, end);
            gridView.setGiftList(gifts, usGifts, start);
            mGridViewList.add(gridView);
        }
        setAdapter(new ViewPagerAdapter<GiftGridView>(mGridViewList));
    }

    @Override
    public void onCheckChange(GiftCheckButton button, boolean isChecked) {
        if (listener != null) {
            listener.onGiftCheckChange(button, isChecked);
        }
    }

    public interface onGiftCheckChangedListener {
        void onGiftCheckChange(GiftCheckButton button, boolean isChecked);
    }

    public void setListener(onGiftCheckChangedListener listener) {
        this.listener = listener;
    }


}
