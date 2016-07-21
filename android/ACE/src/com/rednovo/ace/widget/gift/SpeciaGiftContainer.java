package com.rednovo.ace.widget.gift;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.libs.common.ScreenUtils;

import java.util.ArrayList;


/**
 * Created by lilong on 16/5/6.
 */
public class SpeciaGiftContainer extends LinearLayout{
    private Context mContext = null;
    private BaseGiftView giftView;

    private boolean isVisible = false;

    private int currentGiftId = -1;

    private ArrayList<ReciveGiftInfo> specilaGiftList = new ArrayList<ReciveGiftInfo>(10);

    public SpeciaGiftContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        isVisible = true;
    }

    public SpeciaGiftContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        isVisible = true;
    }

    public SpeciaGiftContainer(Context context) {
        super(context);
        mContext = context;
        isVisible = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = ScreenUtils.getScreenHeight(mContext);
        int width = ScreenUtils.getScreenWidth(mContext);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if(visibility == View.GONE) {
            isVisible = false;
            setGiftContainerVisible(isVisible);
        } else {
            isVisible = true;
            setGiftContainerVisible(isVisible);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startAnimation() {
        if(!isVisible) {
            return;
        }
        ReciveGiftInfo giftInfo = null;

        if (giftView == null) {
            if(specilaGiftList.size() > 0) {
                giftInfo = specilaGiftList.remove(0);
                int giftId = Integer.parseInt(giftInfo.getGiftId());
                currentGiftId = giftId;
                giftView = getGiftView(giftId);
            } else {
                removeAllViews();
                currentGiftId = -1;
                return;
            }

        } else if(giftView.isRunning()) {
            return;
        } else {
            if(specilaGiftList.size() > 0) {
                giftInfo = specilaGiftList.remove(0);
                int giftId = Integer.parseInt(giftInfo.getGiftId());
                // 判断上次执行动画的礼物和现在的礼物是否相同
                if (currentGiftId != giftId) {      // 不相同，获取新的礼物View
                    currentGiftId = giftId;
                    giftView = getGiftView(giftId);
                    removeAllViews();   // 删除上次礼物动画的View
                }
            } else {
                removeAllViews();
                giftView.recyclResource();
                giftView = null;
                currentGiftId = -1;
                return;
            }
        }

        if (giftView != null) {
            giftView.startAnimation(this, new BaseGiftView.AnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    startAnimation();
                }
            });
        } else if(specilaGiftList.size() > 0) {
            startAnimation();
        }
    }

    /**
     * 根据礼物Id生成相应礼物对应的View
     * @param giftId
     * @return
     */
    private BaseGiftView getGiftView(int giftId) {
        BaseGiftView view = null;
        switch (giftId) {
            case 18:
                view = new FlowerGiftView(mContext);
                break;
            case 19:
                view = new CarGiftView(mContext);
                break;
            case 20:
                view = new BoatGiftView(mContext);
                break;
        }

        return view;
    }

    public void addGift(ReciveGiftInfo giftInfo) {

        specilaGiftList.add(giftInfo);
        if(isVisible) {
            if(specilaGiftList.size() == 1) {
                startAnimation();
            }
        }
    }

    public void setGiftContainerVisible(boolean isVisible) {
        this.isVisible = isVisible;

        if(isVisible) {   // 允许接受礼物动画
            if (specilaGiftList.size() > 0) {
                startAnimation();
            }
        } else {        // 清楚礼物消息缓存或暂停礼物动画
            // specilaGiftList.clear();
            if(giftView != null) {
                giftView.cancleAnimation();
            }
        }
    }

}
