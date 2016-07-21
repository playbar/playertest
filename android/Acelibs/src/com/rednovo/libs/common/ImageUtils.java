package com.rednovo.libs.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by lizhen on 16/3/19.
 */
public class ImageUtils {


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toturn(Bitmap bitmap, String path, int mClipWidth, int mClipHeight) {
        float HwScale = 1.0f;
        int rotateDegree = readPictureDegree(path);
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if (rotateDegree == 90) {
            matrix.postRotate(90);
            bmpWidth = bitmap.getHeight();
            bmpHeight = bitmap.getWidth();
        }
        float scale = 1.0f;
        // TODO
        if (bmpWidth * HwScale <= bmpHeight) {
            scale = mClipWidth / bmpWidth;
        } else {
            scale = mClipHeight / bmpHeight;
        }

        matrix.postScale(scale, scale);

        Bitmap processedBitmap = null;
        try {
            processedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (processedBitmap != bitmap) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (OutOfMemoryError e) {

        }
        return processedBitmap;
    }


    /**
     * 本地图片转化为bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap decodeBitmap(String filePath) {
        if (filePath == null) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);

        int sampleFctor = 1;
        if (opt.outWidth > 480 || opt.outHeight > 800) {
            int sampleW = opt.outWidth / 480;
            int sampleH = opt.outHeight / 800;
            sampleFctor = sampleW < sampleH ? sampleW : sampleH;
        }
        if (sampleFctor < 1) {
            sampleFctor = 1;
        }
        Bitmap bitmap = null;
        while (bitmap == null) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = sampleFctor;
                bitmap = BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError oom) {
                ++sampleFctor;
            } catch (Exception e) {
                break;
            }
        }
        return bitmap;
    }

    public static Bitmap decodeBitmap(String filePath, int sampleSize) {
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        Bitmap bitmap = null;
        while (bitmap == null) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = sampleSize;
                bitmap = BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError oom) {
                ++sampleSize;
            } catch (Exception e) {
                break;
            }
        }
        return bitmap;
    }

    /**
     * 压缩图片尺寸
     * @param bitmap
     * @param scaleWidth
     * @param scaleHeight
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int scaleWidth, int scaleHeight){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, true);
        return resizedBitmap;
    }
}
