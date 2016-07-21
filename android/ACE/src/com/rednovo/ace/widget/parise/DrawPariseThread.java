package com.rednovo.ace.widget.parise;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lilong on 16/3/8.
 */
public class DrawPariseThread extends Thread implements Runnable{

    private List<PathObj> list = null;// 路径
    private List<PathObj> tempList = null;// 路径
    private boolean flag;// flag
    private Paint paint;
    private Canvas pagerCanvas;
    private Bitmap pagerBitmap;// 每次使用这个bitmap刷新
    private ArrayList<Bitmap> pariseBitmaps;

    private int mScreenW, mScreenH;
    private Random random = new Random();
    private boolean runFlag = true;

    private WeakReference<SurfaceHolder> weakReferenceSurfaeHolder;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case Globle.KEY_DELAY_SHOW_PARISE:
                    PathObj pathObj = (PathObj) msg.obj;
                    if(list.size() >= 30) {
                        tempList.add(pathObj);
                    } else {
                        list.add(pathObj);
                    }

                    if(!flag) {
                        notifyT();
                    }else if(!isAlive() && runFlag) {
                        try {
                            start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    public DrawPariseThread(Context context, int screenW, int screenH, SurfaceHolder surfaceHolder) {
        mScreenW = screenW;
        mScreenH = screenH;

        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        pagerBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        pagerCanvas = new Canvas(pagerBitmap);

        pariseBitmaps = new ArrayList<Bitmap>();
        pariseBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_parise_1));
        pariseBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_parise_2));
        pariseBitmaps.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_parise_3));

        weakReferenceSurfaeHolder = new WeakReference<SurfaceHolder>(surfaceHolder);

    }

    /**
     * 指定消息缓存队列
     * @param list  线程操作队列
     * @param tempList  当线程操作队列中消息个数超过上显示放入临时缓存结核中
     */
    public void setList(List<PathObj> list, List<PathObj> tempList){
        flag = true;
        if (list == null) {
            throw new NullPointerException("缓存点赞消息集合不能为空");
        }
        this.list = list;
        this.tempList = tempList;
    }

    public void add(int count) {       // 添加多个点赞消息到缓存集合中
        for (int i = 0; i < count; i++) {
            if(!runFlag) {
                mHandler.removeMessages(Globle.KEY_DELAY_SHOW_PARISE);
                break;
            }
            Message msg = Message.obtain();
            msg.what = Globle.KEY_DELAY_SHOW_PARISE;
            msg.obj = new PathObj(mScreenW, mScreenH, pariseBitmaps.get(random.nextInt(pariseBitmaps.size())));
            mHandler.sendMessageDelayed(msg, 200 * i);
        }
    }

    public void add() { // 添加单个点赞消息
        Bitmap bitmap = pariseBitmaps.get(random.nextInt(pariseBitmaps.size()));
        if(list.size() >= 30) {
            tempList.add(new PathObj(mScreenW,mScreenH, bitmap));
        } else {
            list.add(new PathObj(mScreenW,mScreenH, bitmap));
        }
    }

    public void setFlage(boolean flag) {
        this.flag = flag;
    }

    public void joinThread() {  // 结束线程
        if(!flag) {
            notifyT();
        }
        runFlag = false;

        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void run() {
        while (runFlag) {
            if (!flag){
                waiteT();
            }

            if(!runFlag) {
                break;
            }

            long l1 = System.currentTimeMillis();
            myDraw();
            long l2 = System.currentTimeMillis();
            if (l2 - l1 < 33L) {
                long l3 = 33L - (l2 - l1);
                try {
                    Thread.sleep(l3);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }
            }

        }
        mHandler.removeMessages(Globle.KEY_DELAY_SHOW_PARISE);
        mHandler = null;

        this.paint = null;
        this.pagerCanvas = null;
        for(int i = 0; i < pariseBitmaps.size();) {
            pariseBitmaps.remove(i).recycle();
        }
        this.pariseBitmaps = null;
        if(weakReferenceSurfaeHolder != null) {
            weakReferenceSurfaeHolder.clear();
            weakReferenceSurfaeHolder = null;
//            mSurfaceHolder.getSurface().release();
//            mSurfaceHolder = null;
        }
        this.pagerBitmap = null;
    }

    /**
     * 游戏绘图
     */
    public void myDraw() {
        Canvas canvas = null;
        SurfaceHolder surfaceHolder = null;
        try {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            pagerCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            pagerCanvas.save(Canvas.ALL_SAVE_FLAG);// 保存
            drawQpath(pagerCanvas);
            pagerCanvas.restore();// 存储
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            surfaceHolder = weakReferenceSurfaeHolder.get();
            if(surfaceHolder == null) {
                return;
            }
            canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawPaint(paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                canvas.drawBitmap(pagerBitmap, 0, 0, null);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (canvas != null && surfaceHolder != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {
            }

        }
    }


    private void transferData() {
        if(tempList.size() > 0) {
            list.add(tempList.remove(0));
        }
    }


    /**
     * 绘制贝赛尔曲线
     *
     * @param canvas 主画布
     */
    public void drawQpath(Canvas canvas) {
        if (list.size() <= 0) {
            flag = false;
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                PathObj obj = list.get(i);
                if (obj.getAlpha() <= 0) {
                    list.remove(i);
                    transferData();
                    i--;
                    continue;
                }
                Rect src = obj.getSrcRect();
                Rect dst = obj.getDstRect();
                if (dst == null) {
                    list.remove(i);
                    transferData();
                    i--;
                    continue;
                }
                canvas.drawBitmap(obj.getBitmap(), src, dst, obj.getPaint());
            } catch (Exception e) {
                list.remove(i);
                transferData();
                i--;
            }

        }
    }

    public synchronized void waiteT(){
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyT(){
        flag = true;
        this.notifyAll();
    }
}
