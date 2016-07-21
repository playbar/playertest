package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.libs.ui.adapter.SimpleBaseAdapter;

import java.util.List;

/**
 * 礼物用
 */
public class GiftGridAdapter extends SimpleBaseAdapter {

    private static final int SIZE = 10;

    private Context mContext;
    private int mStart;
    private int mLength;
    private List<GiftListResult.GiftListEntity> gifts;

    public GiftGridAdapter(Context context, int start, int length,List<GiftListResult.GiftListEntity> gifts) {
        mContext = context;
        mStart = start;
        mLength = length;
        this.gifts = gifts;
    }

    @Override
    public int getCount() {
        return mLength;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_gift_check_button, null);
        }
        GiftCheckButton giftButton = (GiftCheckButton) convertView.findViewById(R.id.gift_button);
        if (position < mLength) {
//			gifView.setBackgroundResource(EmojiUtils.getExpressionIcon(mContext, mStart + position));
            giftButton.setData(gifts.get(mStart + position));
        }
        return convertView;
    }
}
