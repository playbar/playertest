package com.rednovo.libs.common;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.rednovo.libs.R;

/**
 * Created by Dk on 16/3/19.
 */
public class LevelUtils {
    private static final int MAX_LEVEL = 50;
    private static final int[] LEVEL_ICON_IDS = {
            R.drawable.img_level_icon_00, R.drawable.img_level_icon_01, R.drawable.img_level_icon_02,
            R.drawable.img_level_icon_03, R.drawable.img_level_icon_04, R.drawable.img_level_icon_05,
            R.drawable.img_level_icon_06, R.drawable.img_level_icon_07, R.drawable.img_level_icon_08,
            R.drawable.img_level_icon_09, R.drawable.img_level_icon_10, R.drawable.img_level_icon_11,
            R.drawable.img_level_icon_12, R.drawable.img_level_icon_13, R.drawable.img_level_icon_14,
            R.drawable.img_level_icon_15, R.drawable.img_level_icon_16, R.drawable.img_level_icon_17,
            R.drawable.img_level_icon_18, R.drawable.img_level_icon_19, R.drawable.img_level_icon_20,
            R.drawable.img_level_icon_21, R.drawable.img_level_icon_22, R.drawable.img_level_icon_23,
            R.drawable.img_level_icon_24, R.drawable.img_level_icon_25, R.drawable.img_level_icon_26,
            R.drawable.img_level_icon_27, R.drawable.img_level_icon_28, R.drawable.img_level_icon_29,
            R.drawable.img_level_icon_30, R.drawable.img_level_icon_31, R.drawable.img_level_icon_32,
            R.drawable.img_level_icon_33, R.drawable.img_level_icon_34, R.drawable.img_level_icon_35,
            R.drawable.img_level_icon_36, R.drawable.img_level_icon_37, R.drawable.img_level_icon_38,
            R.drawable.img_level_icon_39, R.drawable.img_level_icon_40, R.drawable.img_level_icon_41,
            R.drawable.img_level_icon_42, R.drawable.img_level_icon_43, R.drawable.img_level_icon_44,
            R.drawable.img_level_icon_45, R.drawable.img_level_icon_46, R.drawable.img_level_icon_47,
            R.drawable.img_level_icon_48, R.drawable.img_level_icon_49, R.drawable.img_level_icon_50
    };

    public static int getLevelIcon(int level) {
        if (level < 0) {
            level = 0;
        }
        if (level > MAX_LEVEL) {
            level = MAX_LEVEL;
        }
        return LEVEL_ICON_IDS[level];
    }

    public static int getLevelIcon(String level) {
        int lv = 0;
        try {
            lv = Integer.parseInt(level);
            return getLevelIcon(lv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LEVEL_ICON_IDS[lv];
    }

    public static Drawable getLevelIcon(Context context, int level) {
        return context.getResources().getDrawable(getLevelIcon(level));
    }
}
