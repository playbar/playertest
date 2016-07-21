package com.rednovo.libs.widget.emoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.rednovo.libs.R;
import com.rednovo.libs.common.EmojiUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;

import java.util.List;

/**
 * Created by CG on 13-12-3.
 *
 * @author ll
 * @version 3.2.0
 */
public class ExpressionGridView extends GridView implements AdapterView.OnItemClickListener {

    private static final int SIZE = 28;
    private static final int COLUMN = 7;

    private List<String> mAllExpression;
    private List<String> mUsedExpression;
    private int mMaxLength = Integer.MAX_VALUE;
    private EditText mShowView;
    private boolean mShowFirstFrameOnly;
    private boolean mIsEmoticonChanged;
    private int start;
    private View sendView;

    /**
     * ExpressionGridView
     *
     * @param context context
     */
    public ExpressionGridView(Context context) {
        super(context);
        init(context);
    }

    /**
     * ExpressionGridView
     *
     * @param context context
     * @param attrs   attrs
     */
    public ExpressionGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setNumColumns(COLUMN);
        setHorizontalSpacing(0);
        setVerticalSpacing(0);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setBackgroundColor(Color.TRANSPARENT);
        setCacheColorHint(0);
        setSelector(R.drawable.transparent);
        setGravity(Gravity.CENTER);
    }

    /**
     * setExpression
     *
     * @param allExpression  allExpression
     * @param usedExpression usedExpression
     * @param start          start
     */
    public void setExpression(List<String> allExpression, List<String> usedExpression, int start) {
        mAllExpression = allExpression;
        mUsedExpression = usedExpression;
        this.start = start;
        setAdapter(new ExpressionGridAdapter(getContext(), start, usedExpression.size()));
        setOnItemClickListener(this);
    }

    /**
     * setTextView
     *
     * @param textView           MessageEditText
     * @param showFirstFrameOnly showFirstFrameOnly
     */
    public void setTextView(EditText textView, boolean showFirstFrameOnly,View view) {
        if (mShowView != null) {
            mShowView.removeTextChangedListener(mTextWatcher);
        }
        mShowView = textView;
        mShowView.addTextChangedListener(mTextWatcher);
        mShowFirstFrameOnly = showFirstFrameOnly;
        sendView = view;
    }

    /**
     * 设置长度
     *
     * @param maxLength 长度
     */
    public void setMaxLength(int maxLength) {
        mMaxLength = maxLength;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        displayTxt(position);
        if (position == SIZE - 1) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mShowView.dispatchKeyEvent(event);
            return;
        }else if(position < mUsedExpression.size()){
            String str = mShowView.getText().toString();
            String txt = mUsedExpression.get(position);
            if(str.length() + txt.length() <= mMaxLength){
                int resId = EmojiUtils.getExpressionIcon(getContext(), start + position);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                d.setBounds(0, 0, ShowUtils.dip2px(getContext(), 20), ShowUtils.dip2px(getContext(), 20));
                ImageSpan imageSpan = new ImageSpan(d);
                SpannableString spannableString = new SpannableString(txt);
                spannableString.setSpan(imageSpan, 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mShowView.getText().insert(mShowView.getSelectionStart(), spannableString);
            }
        }
    }

    /**
     * 表情对应的文字
     * @param position
     */
    private void displayTxt(int position) {
        if (mShowView != null) {
            String str = mShowView.getText().toString();
            int start = mShowView.getSelectionStart();
            int end = mShowView.getSelectionEnd();
            String leftStr = str.substring(0, start);
            String rightStr = str.substring(end, str.length());
            if (position == SIZE - 1) {
                mIsEmoticonChanged = true;
                if (start == end && leftStr.length() > 0) {
                    leftStr = processDelete(leftStr);
                }
                mShowView.setText(leftStr + rightStr);
                mShowView.setSelection(leftStr.length());
            } else if (position < mUsedExpression.size()) {
                if (str.length() + mUsedExpression.get(position).length() <= mMaxLength) {
                    mIsEmoticonChanged = true;
                    mShowView.setText(leftStr + mUsedExpression.get(position) + rightStr);
                    int length = mShowView.getText().toString().length();
                    mShowView.setSelection(Math.min(leftStr.length() + mUsedExpression.get(position).length(), length));
                }
            }
        }
    }

    private String processDelete(String srcStr) {
        String dstStr = srcStr;
        if (!StringUtils.isEmpty(srcStr)) {
            boolean bExpressionFound = false;
            for (String expression : mAllExpression) {
                if (srcStr.endsWith(expression)) {
                    dstStr = srcStr.substring(0, srcStr.length() - expression.length());
                    bExpressionFound = true;
                    break;
                }
            }
            if (!bExpressionFound) {
                dstStr = srcStr.substring(0, srcStr.length() - 1);
            }
        }
        return dstStr;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private Editable mPreEditable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String s1 = mShowView.getText().toString().trim();
            if(TextUtils.isEmpty(s1)){
                sendView.setEnabled(false);
            }else{
                sendView.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals(mPreEditable)) {
                mPreEditable = s;
                if (mIsEmoticonChanged) {
                    mIsEmoticonChanged = false;

                    /*processExpression(mPreEditable.toString());*/
                    mShowView.setSelection(mShowView.getSelectionStart(), mShowView.getSelectionEnd());
                }
            }
        }
    };
}
