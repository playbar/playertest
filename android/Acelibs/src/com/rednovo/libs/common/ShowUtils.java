package com.rednovo.libs.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.rednovo.libs.widget.dialog.LoadingDialog;
import com.rednovo.libs.widget.dialog.LodingRoomDialog;

/**
 * 显示相关的工具类
 *
 * @author hubin
 * @date 2014-11-07
 */
public class ShowUtils {

    private static Context mContext;

    private static Toast toast;

    private static LoadingDialog sProgressDialog;

    private static DisplayMetrics sDisplayMetrics;
    private static LodingRoomDialog createLoaingDialog = null;

    public static void init(Context context) {
        mContext = context;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        sDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    /**
     * 将dp转换成px
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转成为dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转成为px
     */
    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxValue) {
        return (pxValue / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 精确获取屏幕尺寸（例如：3.5、4.0、5.0寸屏幕）
     *
     * @param ctx
     * @return
     */
    public static double getScreenPhysicalSize(Context ctx) {
        ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(sDisplayMetrics);
        double diagonalPixels = Math.sqrt(Math.pow(sDisplayMetrics.widthPixels, 2) + Math.pow(sDisplayMetrics.heightPixels, 2));
        return diagonalPixels / (160 * sDisplayMetrics.density);
    }

    /**
     * 获取屏幕宽度 单位：像素
     *
     * @return 屏幕宽度
     */
    public static int getWidthPixels() {
        return sDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度 单位：像素
     *
     * @return 屏幕高度
     */
    public static int getHeightPixels() {
        return sDisplayMetrics.heightPixels;
    }

    /**
     * 判断是否是平板（官方用法）
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 显示Toast
     *
     * @param message
     */
    public static void showToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }

    /**
     * 显示Toast
     *
     * @param resID
     */
    public static void showToast(int resID) {
        showToast(mContext.getResources().getString(resID));
    }

    /**
     * 显示一个进度对话框
     *
     * @param context 上下文对象
     * @param resId   提示信息资源ID
     */
    public static synchronized void showProgressDialog(Context context, int resId) {
        showProgressDialog(context, context.getResources().getString(resId), true);
    }

    /**
     * 显示一个进度对话框
     *
     * @param context 上下文对象
     * @param message 提示信息
     */
    public static synchronized void showProgressDialog(Context context, String message) {
        showProgressDialog(context, message, true, true);
    }

    /**
     * 显示一个进度对话框
     *
     * @param context                      上下文对象
     * @param resId                        提示信息资源ID
     * @param canceledOnTouchOutsideEnable 是否允许触摸对话框以外的地方，关闭对话框
     */
    public static synchronized void showProgressDialog(Context context, int resId, boolean canceledOnTouchOutsideEnable) {
        showProgressDialog(context, context.getResources().getString(resId), canceledOnTouchOutsideEnable, true);
    }

    /**
     * 显示一个进度对话框
     *
     * @param context                      上下文对象
     * @param message                      提示信息
     * @param canceledOnTouchOutsideEnable 是否允许触摸对话框以外的地方，关闭对话框
     */
    public static synchronized void showProgressDialog(Context context, String message, boolean canceledOnTouchOutsideEnable) {
        showProgressDialog(context, message, canceledOnTouchOutsideEnable, true);
    }

    /**
     * 显示一个进度对话框
     *
     * @param context                      上下文对象
     * @param resId                        提示信息资源ID
     * @param canceledOnTouchOutsideEnable 是否允许触摸对话框以外的地方，关闭对话框
     * @param cancel                       点击back键时，是否关闭对话框
     */
    public static synchronized void showProgressDialog(Context context, int resId, boolean canceledOnTouchOutsideEnable, boolean cancel) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }
        showProgressDialog(context, context.getString(resId), canceledOnTouchOutsideEnable, cancel);
    }

    /**
     * 显示一个进度对话框
     *
     * @param context                      上下文对象 此处的上下文对象必须使用对应调取Activity的上下文，不然报错
     * @param message                      提示信息
     * @param canceledOnTouchOutsideEnable 是否允许触摸对话框以外的地方，关闭对话框
     * @param cancel                       点击back键时，是否关闭对话框
     */
    public static synchronized void showProgressDialog(Context context, String message, boolean canceledOnTouchOutsideEnable, boolean cancel) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }

        try {
            if (sProgressDialog != null) {
                sProgressDialog.dismiss();
                sProgressDialog = null;
            }

            sProgressDialog = new LoadingDialog(context);
            sProgressDialog.setCanceledOnTouchOutside(canceledOnTouchOutsideEnable);
            sProgressDialog.setCancelable(cancel);
            sProgressDialog.setLoadingText(message);
            sProgressDialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭对话框
     */
    public static void dismissProgressDialog() {
        try {
            if (sProgressDialog != null) {
                sProgressDialog.dismiss();
                sProgressDialog = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static void showCreateRoomLoading(Activity activity, boolean isCancel, DialogInterface.OnCancelListener onCancelListener) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (createLoaingDialog == null) {
            createLoaingDialog = new LodingRoomDialog(activity);
        }
        createLoaingDialog.setCancelable(isCancel);
        createLoaingDialog.setCanceledOnTouchOutside(isCancel);
        if (onCancelListener != null) {
            createLoaingDialog.setOnCancelListener(onCancelListener);
        }
        createLoaingDialog.show();
    }

    public static void dimossCreateRoomLoaing() {
        if (createLoaingDialog != null) {
            createLoaingDialog.dismiss();
            createLoaingDialog = null;
        }
    }


    /**
     * 进度对话框是否在显示中
     *
     * @return true - 显示中
     */
    public static boolean isProgressDialogShowing() {
        return sProgressDialog != null && sProgressDialog.isShowing();
    }

    /**
     * 设置进度对话框关闭监听
     *
     * @param listener 进度对话框关闭监听
     */
    public static void setProgressDialogDismissListener(DialogInterface.OnDismissListener listener) {
        if (sProgressDialog != null && listener != null) {
            sProgressDialog.setOnDismissListener(listener);
        }
    }
}
