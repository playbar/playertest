/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:zhen.Li
 *       			开发时间:2016-5-19/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：AceBtlibs
 *                  fileName：MainActivity.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.core.video;

import android.app.Activity;

/**
 * @author zhen.Li/2016-5-19
 */
public class MainActivity extends Activity {

//	private MagicCameraDisplay mMagicCameraDisplayGL30 = null;
//	private GLSurfaceView mGlSurfaceView;
//	private boolean isOpen;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main_layout);
//		initConstants();
//		mGlSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview_camera);
//		// LayoutParams params = new LayoutParams(Constants.mScreenWidth, Constants.mScreenHeight);
//		// mGlSurfaceView.setLayoutParams(params);
//		mMagicCameraDisplayGL30 = new MagicCameraDisplay(this, mGlSurfaceView, "rtmp://up.17ace.tv/live/lizhen");
//		findViewById(R.id.btn_switch).setOnClickListener(this);
//		findViewById(R.id.btn_switch_flash).setOnClickListener(this);
//		findViewById(R.id.btn_beauty).setOnClickListener(this);
//		mMagicCameraDisplayGL30.startVideo();
//	}
//
//	@SuppressLint("NewApi")
//	private void initConstants() {
//		Point outSize = new Point();
//		getWindowManager().getDefaultDisplay().getRealSize(outSize);
//		Constants.mScreenWidth = outSize.x;
//		Constants.mScreenHeight = outSize.y;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.app.Activity#onResume()
//	 */
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.view.View.OnClickListener#onClick(android.view.View)
//	 */
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_switch:
//			mMagicCameraDisplayGL30.switchCamera();
//			break;
//
//		case R.id.btn_switch_flash:
//			mMagicCameraDisplayGL30.switchFlash();
//			break;
//		case R.id.btn_beauty:
//			if (isOpen) {
//				isOpen = false;
//			} else {
//				isOpen = true;
//			}
//			mMagicCameraDisplayGL30.openBeauty(isOpen);
//			break;
//		default:
//			break;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.app.Activity#onDestroy()
//	 */
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		mMagicCameraDisplayGL30.onDestroy();
//	}
}
