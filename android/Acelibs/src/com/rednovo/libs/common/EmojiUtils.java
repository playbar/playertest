package com.rednovo.libs.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 静态表情工具类
 */
public class EmojiUtils {

    public static final int EXPRESSION_ALL_EMOJI_COUNT = 141;
    private static final int EMOTICON_GIF_SIZE_DIP = 20;
    private static final float FLOAT_NUMBER = 0.5f;

    private static final String EXPRESSION_EMOJI_PREFIX = "emoji_";


    private static final String RAW = "raw";
    private static final String DRAWABLE = "drawable";
    private static List<String> emoticonNames = Arrays.asList(BaseApplication.getApplication().getResources().getStringArray(R.array.array_expression));
    private static HashMap<String,String> emojiMap = new HashMap<String,String>(emoticonNames.size());
    static{
        int size = emoticonNames.size();
        for(int i = 0;i<size;i++){
            emojiMap.put(emoticonNames.get(i),EXPRESSION_EMOJI_PREFIX+String.format("%0" + Type.EXPRESSION.getDigit() + "d", i));
        }
    }
    /**
     * gif动画类型
     */
    public enum Type {
        /**
         * 表情 *
         */
        EXPRESSION(EXPRESSION_EMOJI_PREFIX, EXPRESSION_ALL_EMOJI_COUNT);

        private String mResNamePrefix;
        private int mCount;
        private int mDigit;

        private Type(String resNamePrefix, int count) {
            mResNamePrefix = resNamePrefix;
            mCount = count;
            mDigit = Integer.toString(count).length();
        }

        private String getResNamePrefix() {
            return mResNamePrefix;
        }

        /**
         * 获取图片的个数
         *
         * @return gif图片的个数
         */
        public int getCount() {
            return mCount;
        }

        /**
         * 获取资源文件后缀的位数 (如：3位 -> gif_expression_001) *
         */
        private int getDigit() {
            return mDigit;
        }
    }

    private static int getGifResId(Context context, Type type, int index) {
        String ext = String.format("%0" + type.getDigit() + "d", index);
        return context.getResources().getIdentifier(type.getResNamePrefix() + ext, RAW, context.getPackageName());
    }

    /**
     * @param context
     * @param index
     * @return
     */
    public static int getExpressionIcon(Context context, int index) {
        index = Math.abs(index) >= emoticonNames.size() ? emoticonNames.size() - 1 : Math.abs(index);
        return getExpressionResId(context, Type.EXPRESSION, index);
    }

    private static int getExpressionResId(Context context, Type type, int index) {
        String ext = String.format("%0" + type.getDigit() + "d", index);
        LogUtils.v("EmojiUtils", "ext=" + ext);
        return context.getResources().getIdentifier(type.getResNamePrefix() + ext, DRAWABLE, context.getPackageName());
    }

    public static void loadEmoji(Context context,SpannableStringBuilder stringBuilder, int startPos, int endPos, int defaultColor) {

        if (stringBuilder == null || startPos < 0 || startPos > endPos) {
            throw new IllegalArgumentException("loadEmoji Arguments error!");
        }
        String subStr = stringBuilder.subSequence(startPos, endPos).toString();
        String[] splits = subStr.split("/");
        if (splits == null || splits.length == 0) {
            return;
        }
        int start = startPos + splits[0].length();
        stringBuilder.setSpan(new ForegroundColorSpan(defaultColor), startPos, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        for (int i = 1; i < splits.length; i++) {
            String split = "/" + splits[i];
            boolean bFound = false;
            for (int j = 0; j < emoticonNames.size(); j++) {
                String expression = emoticonNames.get(j);
                if (split.startsWith(expression)) {
                    int resId = getExpressionIcon(context,j);
                    int size = (int) (context.getResources().getDisplayMetrics().density * EMOTICON_GIF_SIZE_DIP + FLOAT_NUMBER);
                    if (resId != 0) {
                        Drawable d = BaseApplication.getApplication().getResources().getDrawable(resId);
                        d.setBounds(0, 0, size, size);
                        ImageSpan imageSpan = new ImageSpan(d);
                        stringBuilder.setSpan(imageSpan, start, start + expression.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    if (expression.length() < split.length()) {
                        stringBuilder.setSpan(new ForegroundColorSpan(defaultColor), start + expression.length(), start + split.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    bFound = true;
                    break;
                }
            }
            if (!bFound) {
                stringBuilder.setSpan(new ForegroundColorSpan(defaultColor), start, start + split.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start += split.length();
        }
    }

    /**
     * 匹配表情
     * @param context
     * @param content
     * @return
     */
    public static SpannableStringBuilder loadEmoji(Context context,String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        int startPos = 0;
        int endPos = sb.length();
        int defaultColor = context.getResources().getColor(R.color.color_white);
        loadEmoji(context, sb, startPos, endPos, defaultColor);
        return sb;
    }
    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     *
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getEmojiString(Context context, String str, String zhengze) {
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
    public static void dealExpression(final Context context, final SpannableString spannableString, final Pattern patten, final int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {

        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            String resString = emojiMap.get(key);
            if(TextUtils.isEmpty(resString)){
                continue;
            }
            int resId = context.getResources().getIdentifier(resString, DRAWABLE, "com.rednovo.ace");
            if (resId != 0) {
                Drawable d = BaseApplication.getApplication().getResources().getDrawable(resId);
                // Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                // Drawable d = new BitmapDrawable(context.getResources(), bitmap);
                d.setBounds(0, 0, ShowUtils.dip2px(context,20), ShowUtils.dip2px(context,20));
                ImageSpan imageSpan = new ImageSpan(d);// 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                int end = matcher.start() + key.length(); // 计算该图片名字的长度，也就是要替换的字符串的长度
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
                if (end < spannableString.length()) { // 如果整个字符串还未验证完，则继续。。
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }
        }

    }

}
