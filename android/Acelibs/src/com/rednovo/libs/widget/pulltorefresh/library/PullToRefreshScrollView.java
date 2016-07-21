/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.rednovo.libs.widget.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.rednovo.libs.R;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {

	public PullToRefreshScrollView(Context context) {
		super(context);
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshScrollView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshScrollView(Context context, Mode mode,
			AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected ScrollView createRefreshableView(Context context,
			AttributeSet attrs) {
		ScrollView scrollView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			scrollView = new InternalScrollViewSDK9(context, attrs);
		} else {
			scrollView = new ScrollView(context, attrs);
		}

		scrollView.setId(R.id.scrollview);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild
					.getHeight() - getHeight());
		}
		return false;
	}

	/**
	 * 
	 * 滚动的回调接口
	 * 
	 * @author xiaanming
	 *
	 */
	public interface OnScrollChangedListener{
//		public void onScroll();
		 public void onScrollChanged(int scrollY);
//	     public void onDownMotionEvent();
//	    public void onUpOrCancelMotionEvent();
	}

	private OnScrollChangedListener onScrollListener;
	/**
	 * 设置滚动接口
	 * @param onScrollListener
	 */
	public void setOnScrollListener(OnScrollChangedListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
	
	
	@TargetApi(9)
	final class InternalScrollViewSDK9 extends ScrollView {
		private float xDistance, yDistance, xLast, yLast;  
		public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
			
		}
		 @Override  
		    public boolean onInterceptTouchEvent(MotionEvent ev) {  
		        switch (ev.getAction()) {  
		            case MotionEvent.ACTION_DOWN:  
		                xDistance = yDistance = 0f;  
		                xLast = ev.getX();  
		                yLast = ev.getY();  
		                break;  
		            case MotionEvent.ACTION_MOVE:  
		                final float curX = ev.getX();  
		                final float curY = ev.getY();  
		                  
		                xDistance += Math.abs(curX - xLast);  
		                yDistance += Math.abs(curY - yLast);  
		                xLast = curX;  
		                yLast = curY;  

//		    			if(onScrollListener!=null){
//		    				onScrollListener.onScroll();
//		    			}
		                if(xDistance > yDistance){  
		                    return false;  
		                }    
		        }  
		  
		        return super.onInterceptTouchEvent(ev);  
		    }  
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY,
					scrollX, scrollY, scrollRangeX, scrollRangeY,
					maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshScrollView.this, deltaX,
					scrollX, deltaY, scrollY, getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight()
						- (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
		
		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			if(onScrollListener!=null){
//				onScrollListener.onScroll();
				onScrollListener.onScrollChanged(t);
			}
			super.onScrollChanged(l, t, oldl, oldt);
			
		}
		
		public void setCallbacks(OnScrollChangedListener listener) {
			onScrollListener = listener;
	    }
		
//		@Override
//	    public int computeVerticalScrollRange() {
//	        return super.computeVerticalScrollRange();
//	    }
		
//		@Override
//		public boolean onTouchEvent(MotionEvent ev) {
//			// TODO Auto-generated method stub
//			 if (onScrollListener != null) {
//		            switch (ev.getActionMasked()) {
//		                case MotionEvent.ACTION_DOWN:
//		                	onScrollListener.onDownMotionEvent();
//		                    break;
//		                case MotionEvent.ACTION_UP:
//		                case MotionEvent.ACTION_CANCEL:
//		                	onScrollListener.onUpOrCancelMotionEvent();
//		                    break;
//		            }
//		        }
//			return super.onTouchEvent(ev);
//		}
	}
	

}
