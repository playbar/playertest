package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridView;

import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.libs.R;

import java.util.List;

/**
 * Created by CG on 13-12-3.
 *
 * @author ll
 * @version 3.2.0
 */
public class GiftGridView extends GridView{

    private static final int COLUMN = 4;

    private List<GiftListResult.GiftListEntity> mAllGifts;
    private List<GiftListResult.GiftListEntity> mUsedGifts;
    private onCheckChangedListener listener;
    public GiftGridView(Context context) {
        super(context);
        init(context);
    }
    public GiftGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setNumColumns(COLUMN);
        setHorizontalSpacing(10);
        setVerticalSpacing(10);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setBackgroundColor(Color.TRANSPARENT);
        setCacheColorHint(0);
        setPadding(0, 20, 0, 0);
        setSelector(R.drawable.transparent);
        setGravity(Gravity.CENTER);
    }

    /**
     * 礼物数据
     * @param gifts
     * @param usedGifts
     * @param start
     */
    public void setGiftList(List<GiftListResult.GiftListEntity> gifts, List<GiftListResult.GiftListEntity> usedGifts, int start) {
        mAllGifts = gifts;
        mUsedGifts = usedGifts;
        setAdapter(new GiftGridAdapter(getContext(), start, usedGifts.size(), mAllGifts));
    }
    public void setSelection(GiftCheckButton button, boolean isChecked) {
        if(listener != null){
            listener.onCheckChange(button, isChecked);
        }

    }
    public interface onCheckChangedListener {
        void onCheckChange(GiftCheckButton button, boolean isChecked);
    }
    public void setListener(onCheckChangedListener listener) {
        this.listener = listener;
    }

}
