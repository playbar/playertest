package com.rednovo.ace.widget.parise;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class VoteSurface extends SurfaceView implements Callback{

	private SurfaceHolder mSurfaceHolder;
	private DrawPariseThread mDrawThread;
	public static int screenW, screenH;

	private List<PathObj> list = new ArrayList<PathObj>();// 路径
	private List<PathObj> tempList = new ArrayList<PathObj>();

	private Context mContext;

	private long start_time = 0;

	private boolean isSurfaceCreated = false;

	/**
	 * SurfaceView初始化函数
	 */
	public VoteSurface(Context context) {
		super(context);
		init(context);
	}

	public VoteSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VoteSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		if (isInEditMode()) { return; }
		mContext = context;
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

		setFocusable(true);
		setZOrderOnTop(true);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	/**
	 * SurfaceView视图创建，响应此函数
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		isSurfaceCreated = true;
		screenW = this.getWidth();
		screenH = this.getHeight();

		if(mDrawThread == null) {
			mDrawThread = new DrawPariseThread(mContext, screenW, screenH, holder);
			mDrawThread.setPriority(Thread.MAX_PRIORITY);
			mDrawThread.setList(list, tempList);
		}
	}

	public void stopDraw() {
		isSurfaceCreated = false;
		list.clear();
		tempList.clear();
		if(mDrawThread != null) {
			mDrawThread.joinThread();
			mDrawThread = null;
		}

		Canvas canvas = null;
		try {
			canvas = mSurfaceHolder.lockCanvas();
			if (canvas != null) {
				Paint paint = new Paint();
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawPaint(paint);
			}
		} catch (Exception ex) {
		}finally {
			if(canvas != null)
				mSurfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * 在点赞队列中添加一条数据， 如果执行点赞动画的线程不存在或处于等待状态，启动线程或唤醒线程
     */
	public void click() {
		if(!isSurfaceCreated) {
			return;
		}
		if (System.currentTimeMillis() - start_time > 50) {
			// 禁止同时显示多个赞
			start_time = System.currentTimeMillis();

			mDrawThread.add();

			startThread();
		}
	}

	public void add(int count) {
		if(!isSurfaceCreated) {
			return;
		}

		if(count <= 1) {
			click();
		} else {
			mDrawThread.add(count);
		}
	}

	// 启动回话线程
	private void startThread() {
		if(mDrawThread == null || !mDrawThread.isAlive()) {
			mDrawThread = new DrawPariseThread(mContext, screenW, screenH, mSurfaceHolder);
			mDrawThread.setPriority(Thread.MAX_PRIORITY);
			mDrawThread.setList(list, tempList);
			mDrawThread.start();
		}else if(mDrawThread != null && mDrawThread.isAlive()){
			mDrawThread.notifyT();
		}
	}

	/**
	 * SurfaceView视图状态发生改变，响应此函数
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	/**
	 * SurfaceView视图消亡时，响应此函数
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceCreated = false;

		if(mDrawThread != null){
			mDrawThread.setFlage(false);
		}
		list.clear();
		tempList.clear();
	}

	@Override
	protected void onDetachedFromWindow() {


		super.onDetachedFromWindow();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void destroyHolder() {
		if(mDrawThread != null) {
			mDrawThread.joinThread();
			mDrawThread = null;
		}

		if(mSurfaceHolder != null) {
			mSurfaceHolder.removeCallback(this);
			mSurfaceHolder.getSurface().release();
			mSurfaceHolder = null;
		}
	}
}
