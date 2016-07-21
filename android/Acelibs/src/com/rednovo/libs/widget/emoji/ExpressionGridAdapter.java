package com.rednovo.libs.widget.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rednovo.libs.R;
import com.rednovo.libs.common.EmojiUtils;
import com.rednovo.libs.common.ImageDisplayUtils;
import com.rednovo.libs.ui.adapter.SimpleBaseAdapter;

/**
 * 表情用
 */
public class ExpressionGridAdapter extends SimpleBaseAdapter {

	private static final int SIZE = 28;

	private Context mContext;
	private int mStart;
	private int mLength;

	/**
	 * ExpressionGridAdapter
	 * 
	 * @param context context
	 * @param start start
	 * @param length length
	 */
	public ExpressionGridAdapter(Context context, int start, int length) {
		mContext = context;
		mStart = start;
		mLength = length;
	}

	@Override
	public int getCount() {
		return SIZE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_live_item_face, null);
		}
		ImageView gifView = (ImageView) convertView.findViewById(R.id.item_face);
		if (position < mLength) {
			gifView.setBackgroundResource(EmojiUtils.getExpressionIcon(mContext, mStart + position));
		} else if (position == SIZE - 1) {
			ImageDisplayUtils.setImageResourceSafely(gifView, R.drawable.delete);
		} else {
			gifView.setImageDrawable(null);
		}
		return convertView;
	}
}
