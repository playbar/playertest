package com.rednovo.ace.widget.parise;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by lilong on 16/4/25.
 * 随机路径
 */
public class PathObj {
    private Paint paint;

    public Path path;
    public PathMeasure pathMeasure;

    public float speed;// 速度
    private final float acceleratedSpeed = 0.2f;// 加速度，目前恒定
    private final float speedMax = 15;

    public int curPos;
    public int length;// 路径总长度
    private int startX = 300, startY = 0, controlX, controlY, controlX2, controlY2, endX, endY;
    private float[] p = new float[2];// 坐标点，很重要

    private int time;// 执行的次数
    private int scaleTime = 15;// 放大

    private Rect src;
    private Rect dst;

    private Bitmap bitmap;

    private int bitmapWidth;// 图片宽度
    private int bitmapHeight;// 图片高度

    private int bitmapWidthDst;// 最终放大宽度
    private int bitmapHeightDst;// 最终放大高度

    private int alpha = 255;// 透明度范围 0-255
    private final int alphaOffset = 12;// 透明度偏移量
    private Random random = new Random();

    /**
     * 初始化路径
     *
     * @param width 控件宽度
     * @param height 控件高度
     * @param bitmap 绘制的图片
     */
    public PathObj(int width, int height, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.bitmapWidth = bitmap.getWidth();
        this.bitmapHeight = bitmap.getHeight();

        bitmapWidthDst = bitmapWidth;// + bitmapWidth/4;
        bitmapHeightDst = bitmapHeight;// + bitmapHeight/4;

        curPos = 0;
        startX = width / 2;
        startY = height - bitmapHeightDst / 4;
        endY = 0;
        endX = random.nextInt(width);// + width / 2;
        controlX = random.nextInt(width - bitmapWidthDst / 2) + bitmapWidthDst / 2;
        controlY = random.nextInt(height / 2) + height / 2 - bitmapHeightDst / 4;
        controlX2 = random.nextInt(width - bitmapWidthDst / 2) + bitmapWidthDst / 2;
        controlY2 = random.nextInt(height / 4) + height / 4; //  + height / 2
        src = new Rect(0, 0, bitmapWidth, bitmapHeight);
        dst = new Rect(0, 0, bitmapWidthDst / 2, bitmapHeightDst / 2);
        paint = new Paint();
        paint.setAntiAlias(true);

        path = new Path();
        pathMeasure = new PathMeasure();
        path.moveTo(startX, startY);
        path.cubicTo(controlX, controlY, controlX2, controlY2, endX, endY);

        pathMeasure.setPath(path, false);
        length = (int) pathMeasure.getLength();
        // speed = random.nextFloat()*3f;
        speed = random.nextInt(3) + 5f;

    }

    /**
     * 获取bitmap，用来canvas
     *
     * @return
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 画笔
     *
     * @return
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * 获取起始Rect
     *
     * @return
     */
    public Rect getSrcRect() {
        return src;
    }

    /**
     * 暂时不用了
     *
     * @return
     */
    @Deprecated
    public float[] getPos() {
        curPos += speed;
        speed += acceleratedSpeed;
        if (curPos > length) {
            curPos = length;
            return null;
        }
        pathMeasure.getPosTan(curPos, p, null);
        time++;
        alpha();
        return p;
    }

    /**
     * 获取目标Rect，根据当前点坐标计算Rect，已经加入放大/淡出动画
     *
     * @return
     */
    public Rect getDstRect() {

        curPos += speed;
//			if (time < scaleTime) {
//				speed = 5;
//			} else {
//			}
        if (speed <= speedMax) {
            speed += acceleratedSpeed;
        }

        if (curPos > length) {
            curPos = length;
            return null;
        }

        pathMeasure.getPosTan(curPos, p, null);

        if (time < scaleTime) {
            // 放大动画
            float s = (float) time / scaleTime;
            dst.left = (int) (p[0] - bitmapWidthDst / 3 * s);
            dst.right = (int) (p[0] + bitmapWidthDst / 3 * s);
            dst.top = (int) (p[1] - bitmapHeightDst / 3 * s);
            dst.bottom = (int) (p[1] + bitmapHeightDst / 3 * s);
        } else {
            dst.left = (int) (p[0] - bitmapWidthDst / 3);
            dst.right = (int) (p[0] + bitmapWidthDst / 3);
            dst.top = (int) (p[1] - bitmapHeightDst / 3);
            dst.bottom = (int) (p[1] + bitmapHeightDst / 3);
        }
        time++;
        // 淡出动画
        alpha();
        return dst;
    }

    private int alpha() {
        int offset = length - curPos;
        if (offset < length * 2 / 3) {
            alpha -= alphaOffset;
            if (alpha < 0) {
                alpha = 0;
            }
            paint.setAlpha(alpha);
        } else if (offset <= 10) {
            alpha = 0;
            paint.setAlpha(alpha);
        }
        return 0;
    }

    public int getAlpha() {
        return alpha;
    }

    /**
     * 获取当前执行次数
     *
     * @return
     */
    public int getTime() {
        return time;
    }
}
