package com.rednovo.ace.widget.tabbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rednovo.ace.R;

/**
 * 底部标题栏按钮
 *
 * @author Xd/2015年5月11日
 */
public class TabRadioButton extends LinearLayout {
	private Context mContext;
	private RelativeLayout rb_container;
	private ImageView image;
	private TextView title;
	private boolean isChecked = false;
	// private TextView notify;

	private int img_normal;
	private int img_checked;
	private int text;
	private int text_color_normal;
	private int text_color_checked;
	private int bg_color_normal;
	private int bg_color_checked;

	// private boolean have_notify;

	public TabRadioButton(Context context) {
		super(context);
		mContext = context;
		initView(context);
	}

	public TabRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyRadioButton);
		try {
			img_normal = a.getResourceId(R.styleable.MyRadioButton_img_normal, R.drawable.ic_launcher);
			img_checked = a.getResourceId(R.styleable.MyRadioButton_img_checked, R.drawable.ic_launcher);
			text = a.getResourceId(R.styleable.MyRadioButton_text, R.string.app_name);
			text_color_normal = a.getResourceId(R.styleable.MyRadioButton_text_color_normal, R.color.color_ffffff);
			text_color_checked = a.getResourceId(R.styleable.MyRadioButton_text_color_checked, R.color.color_ffffff);
			bg_color_normal = a.getResourceId(R.styleable.MyRadioButton_bg_color_normal, R.color.transparent);
			bg_color_checked = a.getResourceId(R.styleable.MyRadioButton_bg_color_checked, R.color.transparent);
			isShowImg = a.getBoolean(R.styleable.MyRadioButton_is_show_img, false);
		} finally {
			a.recycle();
		}
		initView(context);
	}

	private void initView(Context context) {
		inflate(context, R.layout.layout_tab_item, this);

		rb_container = (RelativeLayout) findViewById(R.id.rb_container);
		image = (ImageView) findViewById(R.id.img_id);
		title = (TextView) findViewById(R.id.tv_title_id);
		// notify = (TextView) findViewById(R.id.notify);
		rb_container.setOnClickListener(listener);
		if (!isShowImg) {
			image.setVisibility(View.GONE);
			image.setBackgroundResource(0);
		} else {
			image.setVisibility(View.VISIBLE);
			image.setImageResource(img_normal);
		}
		title.setText(text);
	}

	public void check(boolean check) {
		if (check) {
			// rb_container.setBackgroundColor(mContext.getResources().getColor(bg_color_checked));
			image.setImageResource(img_checked);
			title.setTextColor(mContext.getResources().getColor(text_color_checked));
			isChecked = true;
		} else {
			// rb_container.setBackgroundColor(mContext.getResources().getColor(bg_color_normal));
			image.setImageResource(img_normal);
			title.setTextColor(mContext.getResources().getColor(text_color_normal));
			isChecked = false;
		}
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isChecked)
				((NavigationBar) getParent()).setSelection(getId());
		}
	};
	private boolean isShowImg;

	public boolean isChecked() {
		return isChecked;
	}
}
