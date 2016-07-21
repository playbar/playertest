package com.rednovo.ace.widget.live.gift;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.core.session.SendUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.data.events.SendGiftResponse;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.my.RechargeActivity;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.widget.dialog.BaseDialog;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.greenrobot.event.EventBus;

/**
 * 礼物柜弹出框
 */
public class GiftDialog extends BaseDialog implements GiftView.onItemCheckListener, View.OnClickListener {
    private static final int MSG_SEND_GIFT = 100;
    private static final String LOG_TAG = "GiftDialog";
    /**
     * 礼物连送倒计时时长
     */
    private static final long CONTINUOUS_GIFT_TIME = 5000;
    /**
     * 第一个礼物和第二个礼物点击发送间隔(连送)
     */
    private static final long FIRST_GIFT = 200;
//    private static GiftDialog mInstance;
    private GiftView gView;
    private GiftCheckButton currentCheckButton;
    private TextView tvSend, tvCoins/*, tvSecond, tvNum*/;
    private Context mContext;
    private final LinearLayout btnRecharge;
//    private ImageView ivCountdown;
    private ImageView ivDount;
    private TextView tvCountdownNum;
    private RelativeLayout rlCircleSend;

    private long myCoins = 0L;
    private String userId, showId, startId;
    private int clickTime;
    private long lastClickTime;
    private MyCountDown mCD;
    private SimpleDialog dialog;
//    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
//    private ConcurrentHashMap<String, Integer> giftMap = new ConcurrentHashMap<String, Integer>();
//    private static MsgHandler mMsgHandler;

    /*private static class MsgHandler extends Handler {

        WeakReference<GiftDialog> mActivityReference;

        MsgHandler(GiftDialog dialog) {
            mActivityReference = new WeakReference<GiftDialog>(dialog);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            final GiftDialog dialog = mActivityReference.get();
            if (dialog != null) {
                switch (msg.what) {
                    case MSG_SEND_GIFT:
                        GiftListResult.GiftListEntity gift = (GiftListResult.GiftListEntity) msg.obj;
                        dialog.sendPreGift(gift);
                        break;
                    default:
                        break;
                }
            }
        }
    }*/

    public void setGifts(List<GiftListResult.GiftListEntity> gifts) {
        gView.setGifts(gifts);
    }

//    public static GiftDialog getGiftDialog(Context context, int resId) {
//        if (mInstance == null) {
//            mInstance = new GiftDialog(context, resId);
//        }
//        return mInstance;
//    }

    public GiftDialog(Context context, int resId) {
        super(context, R.layout.dialog_gift_cabinet, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        this.getWindow().setWindowAnimations(R.style.dialogBottomWindowAnim);
        mContext = context;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0f;
        getWindow().setAttributes(params);
        gView = (GiftView) findViewById(R.id.gv);
        gView.setListener(this);
        btnRecharge = (LinearLayout) findViewById(R.id.ll_recharge_btn);
        rlCircleSend = (RelativeLayout) findViewById(R.id.rl_circle_send);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvCoins = (TextView) findViewById(R.id.tv_coins);
//        tvSecond = (TextView) findViewById(R.id.tv_second);
//        tvNum = (TextView) findViewById(R.id.tv_num);
//        ivCountdown = (ImageView) findViewById(R.id.iv_countdown);
        tvCountdownNum = (TextView) findViewById(R.id.tv_countdown_num);
        ivDount = (ImageView) findViewById(R.id.iv_dount);
        tvCoins.setText("0");
        btnRecharge.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        rlCircleSend.setOnClickListener(this);
//        mInstance = this;
        tvSend.setEnabled(false);
        userId = UserInfoUtils.getUserInfo().getUserId();
        showId = LiveInfoUtils.getShowId();
        startId = LiveInfoUtils.getStartId();
//        mMsgHandler = new MsgHandler(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.v(LOG_TAG, "onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.v(LOG_TAG, "onDetachedFromWindow");
    }

    @Override
    public void show() {
        requestCoins();
        super.show();
    }

    @Override
    public void dismiss() {
        if (mCD != null) {
            mCD.cancel();
            mCD = null;
        }
        if (currentCheckButton != null) {
//            sendPreGift(currentCheckButton.getGift());
            currentCheckButton.check(false);
            tvSend.setEnabled(false);
            currentCheckButton = null;
        }
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        rlCircleSend.setVisibility(View.GONE);
        tvSend.setVisibility(View.VISIBLE);
//        tvNum.setVisibility(View.GONE);
        super.dismiss();
    }

    /**
     * 在依附的界面销毁时清除
     */
    /*public static void onActivityDestory() {
        if (mMsgHandler != null) {
            mMsgHandler.removeCallbacksAndMessages(null);
            mMsgHandler = null;
        }
//        mInstance = null;
    }*/

    @Override
    public void onItemCheck(GiftCheckButton button, boolean isChecked) {
        if (isChecked) {
            if (currentCheckButton != null) {
                currentCheckButton.check(false);
                rlCircleSend.setVisibility(View.GONE);
                if(mCD != null){
                    mCD.cancel();
                    mCD = null;
                }
                tvSend.setVisibility(View.VISIBLE);
//                mMsgHandler.removeMessages(MSG_SEND_GIFT);
//                sendPreGift(currentCheckButton.getGift());
            }
            currentCheckButton = button;
            tvSend.setEnabled(true);
        } else {
            if (currentCheckButton != null) {
                currentCheckButton.check(false);
            }
            currentCheckButton = null;
            tvSend.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_recharge_btn:
                dismiss();
                Intent intent = new Intent(mContext, RechargeActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.rl_circle_send:
                if (currentCheckButton == null) {
                    break;
                }
                int price = currentCheckButton.getGift().getSendPrice();
                if(myCoins < price){
                    showRechargeDialog();
                }else{
                    SendUtils.sendGif(userId, startId, showId, currentCheckButton.getGift().getId(),"1");
                    if (mCD != null) {
                        mCD.cancel();
                        mCD = new MyCountDown(CONTINUOUS_GIFT_TIME, 500);
                        mCD.start();
                    }
                }
                break;
            case R.id.tv_send:
                if (currentCheckButton == null) {
                    break;
                }
                int price2 = currentCheckButton.getGift().getSendPrice();
                if(myCoins < price2){
                    showRechargeDialog();
                }else{
                    if(!TextUtils.isEmpty(currentCheckButton.getGift().getType()) && "1".equals(currentCheckButton.getGift().getType())){
                        //超级礼物，不可连送
                        SendUtils.sendGif(userId, startId, showId, currentCheckButton.getGift().getId(),"1");
                    }else{
                        if(multiClick())
                            return;
                        rlCircleSend.setVisibility(View.VISIBLE);
                        mCD = new MyCountDown(CONTINUOUS_GIFT_TIME, 500);
                        mCD.start();
                        tvSend.setVisibility(View.GONE);
                        SendUtils.sendGif(userId, startId, showId, currentCheckButton.getGift().getId(),"1");
                    }
                }
                /*myCoins -= price;
                if (myCoins < 0) {
                    SimpleDialog dialog = new SimpleDialog(mContext, new SimpleDialog.OnSimpleDialogBtnClickListener() {
                        @Override
                        public void onSimpleDialogLeftBtnClick() {

                        }

                        @Override
                        public void onSimpleDialogRightBtnClick() {
                            dismiss();
                            Intent intent = new Intent(mContext, RechargeActivity.class);
                            mContext.startActivity(intent);
                        }
                    }, R.string.text_coin_lower_hint, R.string.text_cancel, R.string.recharge);
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    clickTime++;
                    if (clickTime == 1) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SEND_GIFT;
                        msg.obj = currentCheckButton.getGift();
//                        mMsgHandler.sendMessageDelayed(msg, FIRST_GIFT);
//                        saveFirstGift(currentCheckButton.getGift());
                    } else if (clickTime == 2) {
                        long time = System.currentTimeMillis();
                        if (time - lastClickTime <= FIRST_GIFT) {
//                            mMsgHandler.removeMessages(MSG_SEND_GIFT);
//                            if (giftMap != null && giftMap.containsKey(currentCheckButton.getGift().getId())) {
//                                startContDown(currentCheckButton.getGift());
//                            }
                        }
                    } else {
                        if (mCD != null) {
//                            reStartCountDown(currentCheckButton.getGift());
                        }
                    }
                    lastClickTime = System.currentTimeMillis();
                }*/
                break;
            default:
                break;
        }
    }

    /**
     * 充值提醒
     */
    private void showRechargeDialog() {
        if(dialog == null){
            dialog = new SimpleDialog(mContext, new SimpleDialog.OnSimpleDialogBtnClickListener() {
                @Override
                public void onSimpleDialogLeftBtnClick() {

                }

                @Override
                public void onSimpleDialogRightBtnClick() {
                    dismiss();
                    Intent intent = new Intent(mContext, RechargeActivity.class);
                    mContext.startActivity(intent);
                }
            }, R.string.text_coin_lower_hint, R.string.text_cancel, R.string.recharge);
            dialog.setCancelable(false);
        }
        if(!dialog.isShowing())
            dialog.show();
    }

    /**
     * 请求账户余额
     */
    private void requestCoins() {
        String userId = UserInfoUtils.getUserInfo().getUserId();
        ReqUserApi.requsetUserBalance(mContext, userId, new RequestCallback<UserBalanceResult>() {
            @Override
            public void onRequestSuccess(UserBalanceResult object) {
                tvCoins.setText(object.getBlance());

                try {
                    myCoins = Long.parseLong(object.getBlance());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onRequestFailure(UserBalanceResult error) {

            }
        });

    }

    class MyCountDown extends CountDownTimer {
        public GiftListResult.GiftListEntity countGift;

        public MyCountDown(GiftListResult.GiftListEntity gift, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.countGift = gift;
        }
        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int round = Math.round(millisUntilFinished / 1000f);
//            tvSecond.setText(round + "");
            switch (round) {
                case 5:
                    ivDount.setImageResource(R.drawable.count_down_5);
                    tvCountdownNum.setText("5");
                    break;
                case 4:
                    ivDount.setImageResource(R.drawable.count_down_4);
                    tvCountdownNum.setText("4");
                    break;
                case 3:
                    ivDount.setImageResource(R.drawable.count_down_3);
                    tvCountdownNum.setText("3");
                    break;
                case 2:
                    ivDount.setImageResource(R.drawable.count_down_2);
                    tvCountdownNum.setText("2");
                    break;
                case 1:
                    ivDount.setImageResource(R.drawable.count_down_1);
                    tvCountdownNum.setText("1");
                    break;
            }
        }

        @Override
        public void onFinish() {
//            if (countGift != null)
//                sendPreGift(countGift);
            mCD = null;
            rlCircleSend.setVisibility(View.GONE);
            tvSend.setVisibility(View.VISIBLE);
//            tvSecond.setText("");
        }

    }

    /*private void sendPreGift(GiftListResult.GiftListEntity gift) {
        if (giftMap != null && giftMap.containsKey(gift.getId())) {
            String id = gift.getId();
            int num = giftMap.get(id);
            if (num > 0) {
                SendUtils.sendGif(userId, startId, showId, id, num + "");
            }
            tvNum.setText("");
            lock.writeLock().lock();
            giftMap.clear();
            lock.writeLock().unlock();
            clickTime = 0;
            if (mCD != null) {
                mCD.cancel();
                tvSecond.setText("");
                ivCountdown.setVisibility(View.GONE);
                tvNum.setVisibility(View.GONE);
            }
        }
    }*/

    /*private void saveFirstGift(GiftListResult.GiftListEntity gift) {
        String id = gift.getId();
        lock.writeLock().lock();
        giftMap.put(id, 1);
        lock.writeLock().unlock();
    }*/

    /*private void startContDown(GiftListResult.GiftListEntity gift) {
        if (giftMap != null) {
            giftMap.put(gift.getId(), 2);
            tvNum.setText("2");
            mCD = new MyCountDown(gift, CONTINUOUS_GIFT_TIME, 500);
            tvSecond.setText("3");
            mCD.start();
            ivCountdown.setVisibility(View.VISIBLE);
            tvNum.setVisibility(View.VISIBLE);
        }
    }*/

    /*private void reStartCountDown(GiftListResult.GiftListEntity gift) {
        if (giftMap != null && giftMap.containsKey(gift.getId())) {
            mCD.cancel();
            String id = gift.getId();
            int num = giftMap.get(id);
            num++;
            tvNum.setText(num + "");
            lock.writeLock().lock();
            giftMap.put(id, num);
            lock.writeLock().unlock();
            mCD = new MyCountDown(gift, CONTINUOUS_GIFT_TIME, 500);
            tvSecond.setText("3");
            mCD.start();
            ivCountdown.setVisibility(View.VISIBLE);
            tvNum.setVisibility(View.VISIBLE);
        }
    }*/

    /**
     * 设置金币数
     * @param response
     */
    public void setBalance(SendGiftResponse response){
        String balance = response.getBalance();
        if (!TextUtils.isEmpty(balance)) {
            tvCoins.setText(balance);
            try {
                myCoins = Long.parseLong(balance);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private boolean multiClick() {
        if (System.currentTimeMillis() - lastClickTime < 500) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }
}
