package com.rednovo.libs.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Dk on 16/2/27.
 */
public class Utils {

    /**
     * 获取app的版本号
     *
     * @param context
     * @return 版本号
     */
    public static String getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version;
        if (packInfo != null) {
            version = packInfo.versionName;
        } else {
            version = "";
        }
        return version;
    }

    /**
     * 获取app的versionCode
     *
     * @param context
     * @return 版本号
     */
    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version;
        if (packInfo != null) {
            version = packInfo.versionCode;
        } else {
            version = 1;
        }
        return version;
    }

    /***
     * 获取包信息
     *
     * @return
     */
    private static PackageInfo getPackageInfo() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = BaseApplication.getApplication().getPackageManager().getPackageInfo(BaseApplication.getApplication().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    public static String getAPPVersionCode() {

        try {
            return String.valueOf(getPackageInfo().versionCode);
        } catch (Exception ex) {
            return "";
        }

    }

    private boolean isFlymeOs4x() {
        String sysVersion = android.os.Build.VERSION.RELEASE;
        if ("4.4.4".equals(sysVersion)) {
            String sysIncrement = android.os.Build.VERSION.INCREMENTAL;
            String displayId = android.os.Build.DISPLAY;
            if (!TextUtils.isEmpty(sysIncrement)) {
                return sysIncrement.contains("Flyme_OS_4");
            } else {
                return displayId.contains("Flyme OS 4");
            }
        }
        return false;
    }


    /**
     * 获取 int的随机数
     *
     * @param range
     * @return
     */
    public static int getRandom(int range) {
        Random random = new Random();
        return random.nextInt(range);
    }

    /**
     * bitmap转数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 图片毛玻璃效果
     *
     * @param context
     * @param sentBitmap
     * @param radius
     * @return
     */
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
//        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int dv[] = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
            LogUtils.e("Utils", e.toString());
        }
        return version;
    }

    /**
     * 根据定位结果返回定位信息的字符串
     *
     * @param location
     * @return
     */
    public synchronized static String getLocationStr(AMapLocation location) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            if (location.getProvider().equalsIgnoreCase(
                    android.location.LocationManager.GPS_PROVIDER)) {
                // 以下信息只有提供者是GPS时才会有
                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : "
                        + location.getSatellites() + "\n");
            } else {
                // 提供者是GPS时是没有以下信息的
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
            }
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        return sb.toString();
    }

    /**
     * 判断手机是否是小米系统
     *
     * @return
     */
    public static boolean isMIUI() {
        Properties pp = System.getProperties();
        String sys = pp.getProperty("http.agent", "");
        if (sys.contains("MIUI")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是GT19228
     *
     * @return
     */
    public static boolean isGT19228() {
        try {
            String model = android.os.Build.MODEL;
            String brand = android.os.Build.BRAND;
            if ("GT-I9228".equals(model) && "samsung".equals(brand)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        } catch (Error e) {
            return false;
        }
    }

    /**
     * 获取文件夹大小(字节)
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File f) throws Exception {
        long size = 0;
        File[] fileList = f.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    /**
     * 获取图片缓存大小
     *
     * @return
     */
    public static String getImageCacheSize(Context context) {
        long size = 0;
        try {
            size = getFolderSize(new File(StorageUtils.getCacheDirectory(context) + "/" + FrescoEngine.CACHE_FILE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        float f = Float.valueOf(size) / 1024 / 1024;
        BigDecimal b = new BigDecimal(f);

        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "M";
    }

    /**
     * 删除某个文件夹下所有文件
     *
     * @param file
     */
    public static void deleteAllFiles(File file) {
        File files[] = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void cleanCache(Context context) {
        deleteAllFiles(new File(StorageUtils.getCacheDirectory(context) + "/" + FrescoEngine.CACHE_FILE_NAME));
    }
    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }
}
