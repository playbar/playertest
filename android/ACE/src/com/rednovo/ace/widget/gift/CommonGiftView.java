package com.rednovo.ace.widget.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.data.events.CommonGiftAnimEvent;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by lilong on 16/3/12.
 */
public class CommonGiftView extends RelativeLayout{

    public static final int GIFTVIEWIDLE = 0;
    public static final int GIFTVIEWRUNNING = 1;
    public static final int GIFTVIEWPAUSE = 2;

    private Context mContext = null;

    private RelativeLayout rlSenderDesc;
    private TextView tvNickName;
    private TextView tvGiftName;
    private SimpleDraweeView ivGiftIcon;
    private TextView stvGiftCnt;

    private String giftViewId = "";
    private boolean isRunning = false;
    private List<ReciveGiftInfo> reciveGiftInfoList = null;
    private int giftViewPosition = 0;
    private int giftViewStatus = GIFTVIEWIDLE;
    private boolean animatorFlag = true;
    private boolean hasReceivedNewMsg = false;

    private Animator scaleGiftIcon;
    private Animator scaleGiftCountSet;
    private Animator outAnimation;
    private Animator transSenderDesc;

    private com.nineoldandroids.animation.Animator scaleGiftIconForCoolpad;
    private com.nineoldandroids.animation.Animator scaleGiftCountSetForCoolpad;
    private com.nineoldandroids.animation.Animator outAnimationForCoolpad;
    private com.nineoldandroids.animation.Animator transSenderDescForCoolpad;

    public CommonGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.commonGiftView);
        giftViewPosition = Integer.parseInt(this.getTag().toString()); // a.getResourceId(R.styleable.commonGiftView_order, 1);
        initView();
    }

    public CommonGiftView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CommonGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.commonGiftView);
        giftViewPosition = a.getResourceId(R.styleable.commonGiftView_order, 1);
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.common_gift_anim_view, this);

        rlSenderDesc = (RelativeLayout) findViewById(R.id.rl_sender_desc);
        tvNickName = (TextView) findViewById(R.id.tv_sender_nickname);
        tvGiftName = (TextView) findViewById(R.id.tv_gift_name);
        ivGiftIcon = (SimpleDraweeView) findViewById(R.id.iv_gift_icon);
        stvGiftCnt = (TextView) findViewById(R.id.tv_gift_count);     // StrokeTextView

        rlSenderDesc.setVisibility(View.GONE);
        ivGiftIcon.setVisibility(View.GONE);
        stvGiftCnt.setVisibility(View.GONE);
    }

    public boolean startAnimation(String key, List<ReciveGiftInfo> reciveGiftInfoList) {
        if(reciveGiftInfoList == null || reciveGiftInfoList.size() <= 0) {
            return false;
        }

        hasReceivedNewMsg(false);

        if(!animatorFlag) {
            reciveGiftInfoList.clear();
            reciveGiftInfoList = null;
            this.reciveGiftInfoList.clear();
            return false;
        }

        isRunning = true;
        this.reciveGiftInfoList = reciveGiftInfoList;
        giftViewId = key;

        if(giftViewStatus == GIFTVIEWPAUSE) {
            inflateData();
        } else {
            ReciveGiftInfo currentGift = reciveGiftInfoList.remove(0);
            tvNickName.setText(currentGift.getSenderName() + " ");
            tvGiftName.setText("送出 : " + currentGift.getGiftName());
            stvGiftCnt.setText("X" + currentGift.getGiftCnt());
            ivGiftIcon.getHierarchy().setFailureImage(AceApplication.getApplication().getResources().getDrawable( R.drawable.default_gift_icon));
            FrescoEngine.setSimpleDraweeView(ivGiftIcon, currentGift.getGiftUrl(), ImageRequest.ImageType.DEFAULT);

            startAnimation();
        }
        giftViewStatus = GIFTVIEWRUNNING;

        return isRunning;
    }

    public void inflateData() {

        if(!animatorFlag) {
            if(reciveGiftInfoList != null)
                reciveGiftInfoList.clear();
            reciveGiftInfoList = null;
            stopAnimation();
            return;
        }

        // 当前连送礼物数量展示结束
        if (reciveGiftInfoList.size() <= 0) {
            giftViewStatus = GIFTVIEWPAUSE;
            Handler handler = getHandler();
            final CommonGiftAnimEvent animFinishEvent = new CommonGiftAnimEvent();
            animFinishEvent.giftViewTag = giftViewPosition;
            animFinishEvent.id = Globle.KEY_EVENT_GIFT_FINISH;
            if(handler != null && !hasReceivedNewMsg) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(animFinishEvent);
                    }
                }, 1000);
            } else {
                EventBus.getDefault().post(animFinishEvent);
            }
        } else {        // 当前连送礼物继续叠加数量
            ReciveGiftInfo currentGift = reciveGiftInfoList.remove(0);
            stvGiftCnt.setVisibility(View.VISIBLE);
            stvGiftCnt.setText("X" + currentGift.getGiftCnt() + "");
            stvGiftCnt.requestLayout();

            startGiftCntAnim();
        }
    }

    public void setAnimatorFlag(boolean flag) {
        animatorFlag = flag;
    }

    public boolean compareTo(String key) {
        if(key.equals(giftViewId)){
            return true;
        }

        return false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRuuning) {
        this.isRunning = isRuuning;
    }

    private void startAnimation() {
        if(Build.MODEL.contains("Coolpad")) {
            com.nineoldandroids.animation.AnimatorSet animatorStartSet = getAnimationForCoolpad();
            animatorStartSet.start();
        } else {
            AnimatorSet animaStartSet = getStartAnimation();
            animaStartSet.start();
        }
    }

    private com.nineoldandroids.animation.AnimatorSet getAnimationForCoolpad() {
        rlSenderDesc.setVisibility(View.VISIBLE);

        float snderDescTransX = mContext.getResources().getDimension(R.dimen.gift_desc_width);
        if (transSenderDescForCoolpad == null) {
            transSenderDescForCoolpad = com.nineoldandroids.animation.ObjectAnimator.ofFloat(rlSenderDesc, "translationX", -snderDescTransX, 0.0F);
            transSenderDescForCoolpad.setDuration(500);
            transSenderDescForCoolpad.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                    ivGiftIcon.setVisibility(View.VISIBLE);
                }
            });
            transSenderDescForCoolpad.setInterpolator(new LinearInterpolator());
        }

        if (scaleGiftIconForCoolpad == null)
            scaleGiftIconForCoolpad = getScaleGiftIconAnimatorForCoolpad();

        if (scaleGiftCountSetForCoolpad == null)
            scaleGiftCountSetForCoolpad = getGiftCntAnimForCoolpad();

        com.nineoldandroids.animation.AnimatorSet animaStartSet = new com.nineoldandroids.animation.AnimatorSet();
        animaStartSet.playSequentially(transSenderDescForCoolpad, scaleGiftIconForCoolpad, scaleGiftCountSetForCoolpad);
        animaStartSet.start();

        return animaStartSet;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private AnimatorSet getStartAnimation() {
        rlSenderDesc.setVisibility(View.VISIBLE);

        float snderDescTransX = mContext.getResources().getDimension(R.dimen.gift_desc_width);
        if (transSenderDesc == null) {
            transSenderDesc = ObjectAnimator.ofFloat(rlSenderDesc, View.TRANSLATION_X, -snderDescTransX, 0.0F);
            transSenderDesc.setDuration(500);
            transSenderDesc.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ivGiftIcon.setVisibility(View.VISIBLE);
                }
            });
            transSenderDesc.setInterpolator(new LinearInterpolator());
        }

        if (scaleGiftIcon == null)
            scaleGiftIcon = getScaleGiftIconAnimator();

        if (scaleGiftCountSet == null)
            scaleGiftCountSet = getGiftCntAnim();

        AnimatorSet animaStartSet = new AnimatorSet();
        animaStartSet.playSequentially(transSenderDesc, scaleGiftIcon, scaleGiftCountSet);
        animaStartSet.start();

        return animaStartSet;
    }

    private com.nineoldandroids.animation.Animator getScaleGiftIconAnimatorForCoolpad() {
        com.nineoldandroids.animation.ObjectAnimator scaleY = com.nineoldandroids.animation.ObjectAnimator.ofFloat(ivGiftIcon, "scaleX", 1.5F, 1.0F);
        com.nineoldandroids.animation.ObjectAnimator scaleX = com.nineoldandroids.animation.ObjectAnimator.ofFloat(ivGiftIcon, "scaleY", 1.5F, 1.0F);

        com.nineoldandroids.animation.AnimatorSet scaleSet = new com.nineoldandroids.animation.AnimatorSet();
        scaleSet.setDuration(300);
        scaleSet.playTogether(scaleX, scaleY);
        scaleSet.setInterpolator(new LinearInterpolator());
        scaleSet.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {

                stvGiftCnt.setVisibility(View.VISIBLE);
            }
        });
        scaleSet.setInterpolator(new AccelerateInterpolator());
        return scaleSet;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Animator getScaleGiftIconAnimator() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivGiftIcon, View.SCALE_X, 1.5F, 1.0F);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivGiftIcon, View.SCALE_Y, 1.5F, 1.0F);

        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.setDuration(300);
        scaleSet.playTogether(scaleX, scaleY);
        scaleSet.setInterpolator(new LinearInterpolator());
        scaleSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                stvGiftCnt.setVisibility(View.VISIBLE);
            }
        });
        scaleSet.setInterpolator(new AccelerateInterpolator());
        return scaleSet;
    }

    private void startGiftCntAnim() {
        if(Build.MODEL.contains("Coolpad")) {
            if(scaleGiftCountSetForCoolpad == null) {
                scaleGiftCountSetForCoolpad = getGiftCntAnimForCoolpad();
            }

            scaleGiftCountSetForCoolpad.start();
        } else {
            if(scaleGiftCountSet == null) {
                scaleGiftCountSet = getGiftCntAnim();
            }

            scaleGiftCountSet.start();
        }
    }

    private com.nineoldandroids.animation.Animator getGiftCntAnimForCoolpad() {
        com.nineoldandroids.animation.ObjectAnimator scaleXS = com.nineoldandroids.animation.ObjectAnimator.ofFloat(stvGiftCnt, "scaleX", 0.1F, 2.0F);
        com.nineoldandroids.animation.ObjectAnimator scaleYS = com.nineoldandroids.animation.ObjectAnimator.ofFloat(stvGiftCnt, "scaleY", 0.1F, 2.0F);
        com.nineoldandroids.animation.AnimatorSet giftCountSetS = new com.nineoldandroids.animation.AnimatorSet();
        giftCountSetS.setDuration(200);

        giftCountSetS.setInterpolator(new AccelerateInterpolator()); //new BounceInterpolator()
        giftCountSetS.playTogether(scaleXS, scaleYS);

        com.nineoldandroids.animation.ObjectAnimator scaleXU = com.nineoldandroids.animation.ObjectAnimator.ofFloat(stvGiftCnt, "scaleX", 2.0F, 1F);
        com.nineoldandroids.animation.ObjectAnimator scaleYU = com.nineoldandroids.animation.ObjectAnimator.ofFloat(stvGiftCnt, "scaleY", 2.0F, 1F);
        com.nineoldandroids.animation.AnimatorSet giftCountSetU = new com.nineoldandroids.animation.AnimatorSet();
        giftCountSetU.setDuration(150);
        giftCountSetU.setInterpolator(new AccelerateInterpolator()); // BounceInterpolator
        giftCountSetU.playTogether(scaleXU, scaleYU);

        com.nineoldandroids.animation.AnimatorSet giftCountSet = new com.nineoldandroids.animation.AnimatorSet();
        giftCountSet.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                CommonGiftView.this.inflateData();
            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {
            }
        });
        giftCountSet.playSequentially(giftCountSetS, giftCountSetU);
        return giftCountSet;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Animator getGiftCntAnim() {
        ObjectAnimator scaleXS = ObjectAnimator.ofFloat(stvGiftCnt, View.SCALE_X, 0.1F, 2.0F);
        ObjectAnimator scaleYS = ObjectAnimator.ofFloat(stvGiftCnt, View.SCALE_Y, 0.1F, 2.0F);
        AnimatorSet giftCountSetS = new AnimatorSet();
        giftCountSetS.setDuration(200);

        giftCountSetS.setInterpolator(new AccelerateInterpolator()); //new BounceInterpolator()
        giftCountSetS.playTogether(scaleXS, scaleYS);

        ObjectAnimator scaleXU = ObjectAnimator.ofFloat(stvGiftCnt, View.SCALE_X, 2.0F, 1F);
        ObjectAnimator scaleYU = ObjectAnimator.ofFloat(stvGiftCnt, View.SCALE_Y, 2.0F, 1F);
        AnimatorSet giftCountSetU = new AnimatorSet();
        giftCountSetU.setDuration(150);
        giftCountSetU.setInterpolator(new AccelerateInterpolator()); // BounceInterpolator
        giftCountSetU.playTogether(scaleXU, scaleYU);

        AnimatorSet giftCountSet = new AnimatorSet();
        giftCountSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                CommonGiftView.this.inflateData();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        giftCountSet.playSequentially(giftCountSetS, giftCountSetU);
        return giftCountSet;
    }

    private com.nineoldandroids.animation.Animator getOutAnimationForCoolpad() {
        com.nineoldandroids.animation.ObjectAnimator outTran = com.nineoldandroids.animation.ObjectAnimator.ofFloat(this, "translationY", 0.0F, -180.0F * giftViewPosition);
        com.nineoldandroids.animation.ObjectAnimator outAlpha = com.nineoldandroids.animation.ObjectAnimator.ofFloat(this, "alpha", 1.0F, 0.1F);
        com.nineoldandroids.animation.AnimatorSet outSet = new com.nineoldandroids.animation.AnimatorSet();

        outSet.setDuration(400 * giftViewPosition);
        outSet.setInterpolator(new LinearInterpolator());
        outSet.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                CommonGiftView.this.setRunning(false);
                giftViewStatus = GIFTVIEWIDLE;
                giftViewId = "";
                ivGiftIcon.setVisibility(View.GONE);
                stvGiftCnt.setVisibility(View.GONE);
                rlSenderDesc.setVisibility(View.GONE);
                CommonGiftView.this.setTranslationY(0.0F);
                CommonGiftView.this.setTranslationX(0.0F);
                CommonGiftView.this.setAlpha(1.0F);

                CommonGiftAnimEvent animFinishEvent = new CommonGiftAnimEvent();
                animFinishEvent.id = Globle.KEY_EVENT_GIFT_FINISH;
                animFinishEvent.giftViewTag = giftViewPosition;
                EventBus.getDefault().post(animFinishEvent);
            }
        });


        outSet.playTogether(outTran, outAlpha);
        return outSet;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Animator getOutAnimation() {
        ObjectAnimator outTran = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0.0F, -180.0F * giftViewPosition);
        ObjectAnimator outAlpha = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0F, 0.1F);
        AnimatorSet outSet = new AnimatorSet();

        outSet.setDuration(400 * giftViewPosition);
        outSet.setInterpolator(new LinearInterpolator());
        outSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                CommonGiftView.this.setRunning(false);
                giftViewStatus = GIFTVIEWIDLE;
                giftViewId = "";
                ivGiftIcon.setVisibility(View.GONE);
                stvGiftCnt.setVisibility(View.GONE);
                rlSenderDesc.setVisibility(View.GONE);
                CommonGiftView.this.setTranslationY(0.0F);
                CommonGiftView.this.setTranslationX(0.0F);
                CommonGiftView.this.setAlpha(1.0F);

                CommonGiftAnimEvent animFinishEvent = new CommonGiftAnimEvent();
                animFinishEvent.id = Globle.KEY_EVENT_GIFT_FINISH;
                animFinishEvent.giftViewTag = giftViewPosition;
                EventBus.getDefault().post(animFinishEvent);
            }
        });


        outSet.playTogether(outTran, outAlpha);
        return outSet;
    }

    private void stopAnimationP() {
        Handler handler = getHandler();
        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(outAnimation == null) {
                        outAnimation = getOutAnimation();
                    }
                    outAnimation.start();
                }
            }, 3000);

        } else {
            if(outAnimation == null) {
                outAnimation = getOutAnimation();
            }
            outAnimation.start();
        }
    }

    private void stopAnimationForCoolpad() {
        Handler handler = getHandler();
        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(outAnimationForCoolpad == null) {
                        outAnimationForCoolpad = getOutAnimationForCoolpad();
                    }
                    outAnimationForCoolpad.start();
                }
            }, 3000);

        } else {
            if(outAnimationForCoolpad== null) {
                outAnimationForCoolpad = getOutAnimationForCoolpad();
            }
            outAnimationForCoolpad.start();
        }
    }
    public void stopAnimation() {
        if(Build.MODEL.contains("Coolpad")) {
            stopAnimationForCoolpad();
        } else {
            stopAnimationP();
        }
    }

    public void hasReceivedNewMsg(boolean received) {
        hasReceivedNewMsg = received;
    }

    public int getViewStatus() {
        return giftViewStatus;
    }
}
