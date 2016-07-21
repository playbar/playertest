package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.GiftListResult;

/**
 * 礼物框
 */
public class GiftCheckButton extends FrameLayout {
    private Context mContext;
    private RelativeLayout rb_container;
    private SimpleDraweeView image;
    private TextView tv_first;
    private TextView tv_second;
    private boolean checked;
    private ImageView iv_bg;
    private ImageView iv_bg2;

    private int image_normal;
    private int image_checked;
    private int first_text_color_normal;
    private int first_text_color_checked;
    private int second_text_color_normal;
    private int second_text_color_checked;
    private int first_text;
    private int second_text;
    private int bg_color_normal;
    private int bg_color_checked;
    private GiftListResult.GiftListEntity gift;

    public GiftCheckButton(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public GiftCheckButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GiftCheckButton);
        try {
            image_normal = a.getResourceId(R.styleable.GiftCheckButton_image_normal, R.drawable.ic_launcher);
            image_checked = a.getResourceId(R.styleable.GiftCheckButton_image_checked, R.drawable.ic_launcher);
            first_text = a.getResourceId(R.styleable.GiftCheckButton_first_text, R.string.app_name);
            first_text_color_normal = a.getResourceId(R.styleable.GiftCheckButton_first_text_color_normal, R.color.color_white);
            first_text_color_checked = a.getResourceId(R.styleable.GiftCheckButton_first_text_color_checked, R.color.color_white);

            second_text = a.getResourceId(R.styleable.GiftCheckButton_second_text, R.string.app_name);
            second_text_color_normal = a.getResourceId(R.styleable.GiftCheckButton_second_text_color_normal, R.color.color_white);
            second_text_color_checked = a.getResourceId(R.styleable.GiftCheckButton_second_text_color_checked, R.color.color_white);
            bg_color_normal = a.getResourceId(R.styleable.GiftCheckButton_bg_normal, R.color.transparent);
            bg_color_checked = a.getResourceId(R.styleable.GiftCheckButton_bg_checked, R.color.transparent);

            checked = a.getBoolean(R.styleable.GiftCheckButton_checked, false);
        } finally {
            a.recycle();
        }
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.gift_check_button, this);
        rb_container = (RelativeLayout) findViewById(R.id.rb_container);
        image = (SimpleDraweeView) findViewById(R.id.image);
        tv_first = (TextView) findViewById(R.id.tv_first);
        tv_second = (TextView) findViewById(R.id.tv_second);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        iv_bg2 = (ImageView) findViewById(R.id.iv_bg2);
        rb_container.setOnClickListener(listener);

        image.setImageResource(image_normal);
        tv_first.setText(first_text);
        tv_second.setText(second_text);
        iv_bg.setVisibility(View.INVISIBLE);

    }

    public void check(boolean check) {
        if (check) {
            rb_container.setBackgroundColor(mContext.getResources().getColor(bg_color_checked));
//			image.setImageResource(image_checked);
            tv_first.setTextColor(mContext.getResources().getColor(first_text_color_checked));
            tv_second.setTextColor(mContext.getResources().getColor(second_text_color_checked));
            iv_bg.setVisibility(View.VISIBLE);
            //iv_bg2.setVisibility(View.INVISIBLE);
            checked = true;
        } else {
            rb_container.setBackgroundColor(mContext.getResources().getColor(bg_color_normal));
//			image.setImageResource(image_normal);
            tv_first.setTextColor(mContext.getResources().getColor(first_text_color_normal));
            tv_second.setTextColor(mContext.getResources().getColor(second_text_color_normal));
            iv_bg.setVisibility(View.INVISIBLE);
            // iv_bg2.setVisibility(View.VISIBLE);
            checked = false;
        }
    }

    public void setData(GiftListResult.GiftListEntity gift) {
        this.gift = gift;
        tv_first.setText(gift.getName());
        tv_second.setText(gift.getSendPrice() + "");
        Uri uri = Uri.parse(gift.getPic());
        image.setImageURI(uri);
        check(checked);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isChecked()) {
//                check(false);
//                ((GiftGridView) getParent().getParent()).setSelection(GiftCheckButton.this, false);
            } else {
                check(true);
                ((GiftGridView) getParent().getParent()).setSelection(GiftCheckButton.this, true);
            }
        }
    };

    public boolean isChecked() {
        return checked;
    }


    public GiftListResult.GiftListEntity getGift() {
        return gift;
    }
}
