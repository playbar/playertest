package com.rednovo.ace.data.pay;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.rednovo.ace.ui.activity.MainActivity;
import com.rednovo.libs.ui.base.AppManager;

import java.util.Map;

/**
 * Created by lilong on 16/3/6.
 */
public class IAlixPayApi extends BasePayApi{

    private Activity mActivity = null;

    private boolean isPaying = false;
    private static IAlixPayApi mInstance;

    private PayTask alipayTask;
    private PayRunnable mRunable = new PayRunnable();
    private Thread mPayThread = null;

    private IAlixPayApi(Activity cxt){
        mActivity = AppManager.getAppManager().findActivity(MainActivity.class);
        if(mActivity == null) {
            mActivity = cxt;
        }

    }

    public static IAlixPayApi getInstance(Activity cxt) {
        if(mInstance == null) {
            mInstance = new IAlixPayApi(cxt);
        }

        return mInstance;
    }

    private class PayRunnable implements Runnable {
        private String orderInfoStr = "";

        public void setOrderInfoStr(String orderInfoStr) {
            this.orderInfoStr = orderInfoStr;
        }

        public void run() {
            try {
                isPaying = false;

                if(alipayTask == null) {
                    alipayTask = new PayTask(mActivity);
                }
                String strRet = alipayTask.pay(orderInfoStr, true);
                // 发送交易结果
                if(mCallback != null){
                    Message msg = new Message();
                    msg.what = mWhat;
                    msg.obj = strRet;
                    mCallback.sendMessage(msg);

                }
                orderInfoStr = "";
            } catch (Exception e) {
                e.printStackTrace();

                // 发送交易结果
                if(mCallback != null){
                    Message msg = new Message();
                    msg.what = mWhat;
                    msg.obj = e.toString();
                    mCallback.sendMessage(msg);
                }
                orderInfoStr = "";
            }
        }
    }

    @Override
    public void pay(Map<String, Object> orderInfo) {
        if (isPaying)
            return;
        isPaying = true;
        try {
            String orderInfoStr = (String) orderInfo.get("orderInfo"); // new CreateIAlixPayOrder().getSignOrderStr("测试的商品", "该测试商品的详细描述", "0.01");//"partner=\"2088801770882180\"&seller_id=\"yejinqiang@funfun001.com\"&out_trade_no=\"030720324010608\"&subject=\"测试的商品\"&body=\"该测试商品的详细描述\"&total_fee=\"0.01\"&notify_url=\"http://notify.msp.hk/notify.htm\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"ahBFPzz6krXX5FO4V%2FHNIjsAeD3T%2FiXRfcsD%2B4kNkIbrkm%2FneZmnDtmsjyUK607y3rm%2BoQpBNuw1dgVaI0puMHpOWt67h22HO6YFMBRW%2B2mXocB6M1gYFXFSTejCSrXkXNgwoDUWE3rwgi25ZAKophIVgXnLVlbZEjTlsW64d%2BI%3D\"&sign_type=\"RSA\"";
            if(TextUtils.isEmpty(orderInfoStr)){
                Toast.makeText(mActivity, "订单信息为空", Toast.LENGTH_LONG).show();
                return;
            }
            // 实例一个线程来进行支付
            if(mRunable == null) {
                mRunable = new PayRunnable();
            }
            mRunable.setOrderInfoStr(orderInfoStr);
            mPayThread = new Thread(mRunable);
            mPayThread.start();

        }catch (Exception e){
            isPaying = false;
        }
    }

    @Override
    public boolean isSupportPay() {
        return super.isSupportPay();
    }

    @Override
    public void destroy() {
        if(mPayThread != null) {
            try {
                mPayThread.join();
                mPayThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mCallback = null;
    }
}
