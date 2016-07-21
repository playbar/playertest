package com.rednovo.libs.widget.pulltorefresh.library.internal;

import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.rednovo.libs.widget.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.rednovo.libs.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MyLoadingLayout extends LoadingLayout {

    //	 private AnimationDrawable animationDrawable;
    private final Animation mRotateAnimation, mResetRotateAnimation;
    static final int FLIP_ANIMATION_DURATION = 150;

    public MyLoadingLayout(Context context, Mode mode,
                           Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        // 初始化
//	        mHeaderImage.setImageResource(R.anim.refresh_progress_animation);
//	        animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.GONE);

        final int rotateAngle = mode == Mode.PULL_FROM_START ? 180 : -180;
        mRotateAnimation = new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        mResetRotateAnimation = new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);
    }

    // 默认图片
    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.default_indicator_arrow;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            final int dHeight = imageDrawable.getIntrinsicHeight();
            final int dWidth = imageDrawable.getIntrinsicWidth();

            /**
             * We need to set the width/height of the ImageView so that it is
             * square with each side the size of the largest drawable dimension.
             * This is so that it doesn't clip when rotated.
             */
            ViewGroup.LayoutParams lp = mHeaderImage.getLayoutParams();
            lp.width = lp.height = Math.max(dHeight, dWidth);
            mHeaderImage.requestLayout();

            /**
             * We now rotate the Drawable so that is at the correct rotation,
             * and is centered.
             */
            mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
            Matrix matrix = new Matrix();
            matrix.postTranslate((lp.width - dWidth) / 2f, (lp.height - dHeight) / 2f);
            matrix.postRotate(getDrawableRotationAngle(), lp.width / 2f, lp.height / 2f);
            mHeaderImage.setImageMatrix(matrix);
        }
    }

    private float getDrawableRotationAngle() {
        float angle = 0f;
        switch (mMode) {
            case PULL_FROM_END:
                if (mScrollDirection == Orientation.HORIZONTAL) {
                    angle = 90f;
                } else {
                    angle = 180f;
                }
                break;

            case PULL_FROM_START:
                if (mScrollDirection == Orientation.HORIZONTAL) {
                    angle = 270f;
                }
                break;

            default:
                break;
        }

        return angle;
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
        // NO-OP
        float angle;
//			if (mRotateDrawableWhilePulling) {
//				angle = scaleOfLayout * 90f;
//			} else {
//				angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
//			}
//
//			mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
//			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        if(mHeaderImage.getVisibility() == View.GONE) {
            mHeaderImage.setVisibility(View.VISIBLE);
            mPullToRefreshText.setVisibility(View.VISIBLE);
        }
    }

    // 下拉以刷新
    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
        System.out.println("==pullToRefreshImpl");
        if (mRotateAnimation == mHeaderImage.getAnimation()) {
            mHeaderImage.startAnimation(mResetRotateAnimation);
        }
    }

    // 正在刷新时回调
    @Override
    protected void refreshingImpl() {
        // 播放帧动画
        // animationDrawable.start();
        System.out.println("==refreshingImpl");
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.GONE);
        mPullToRefreshText.setVisibility(View.GONE);
        mHeaderProgress.setVisibility(View.VISIBLE);
    }

    // 释放以刷新
    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
        System.out.println("==releaseToRefreshImpl");
        mHeaderImage.startAnimation(mRotateAnimation);
    }

    // 重新设置
    @Override
    protected void resetImpl() {
//	        mHeaderImage.setVisibility(View.VISIBLE);
//	        mHeaderImage.clearAnimation();
        System.out.println("==resetImpl");
        mHeaderImage.clearAnimation();
        mHeaderProgress.setVisibility(View.GONE);
        mHeaderImage.setVisibility(View.GONE);
    }

}
