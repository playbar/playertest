package com.rednovo.ace.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.common.WebViewConfig;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.pay.BasePayApi;
import com.rednovo.ace.data.pay.PayApiFactory;
import com.rednovo.ace.data.pay.PayResult;
import com.rednovo.ace.data.pay.WXPayApi;
import com.rednovo.ace.net.api.ReqOrderApi;
import com.rednovo.ace.net.api.ReqSystemApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.GoodListResult;
import com.rednovo.ace.net.parser.NewOrderResult;
import com.rednovo.ace.net.parser.UserBalanceResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.ace.ui.adapter.RechargeGridViewAdapter;
import com.rednovo.ace.view.dialog.SimpleDialog;
import com.rednovo.ace.widget.FreeGridView;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.common.SystemUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 充值
 * Created by Dk on 16/2/26.
 */
public class RechargeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int WECHATPAY = 1;

    public static final int ALIPAY = 2;

    private SimpleDraweeView mPortrait;

    private TextView tvBalance;

    private TextView tvName;

    private FreeGridView gvRecharge;

    private ListView listview;

    private RechargeGridViewAdapter mAdapter;

    private Button btnWechat;

    private Button btnAlipay;

    private int selectPosition = -1;

    private BasePayApi mPayApi = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_recharge);

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.recharge);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.iv_option).setOnClickListener(this);

        gvRecharge = (FreeGridView) findViewById(R.id.gv_recharge);

        btnWechat = (Button) findViewById(R.id.btn_recharge_wechat);
        btnAlipay = (Button) findViewById(R.id.btn_recharge_alipay);
        mPortrait = (SimpleDraweeView) findViewById(R.id.img_recharge_portrait);
        tvBalance = (TextView) findViewById(R.id.tv_recharge_balance_num);
        tvName = (TextView) findViewById(R.id.tv_recharge_name);

        mAdapter = new RechargeGridViewAdapter(this);
        gvRecharge.setAdapter(mAdapter);

        gvRecharge.setOnItemClickListener(this);
        btnWechat.setOnClickListener(this);
        btnAlipay.setOnClickListener(this);

        tvName.setText(UserInfoUtils.getUserInfo().getNickName());
        ((GenericDraweeHierarchy) mPortrait.getHierarchy()).setFailureImage(getResources().getDrawable(R.drawable.head_offline));
        FrescoEngine.setSimpleDraweeView(mPortrait, UserInfoUtils.getUserInfo().getProfile(), ImageRequest.ImageType.SMALL);
        ShowUtils.showProgressDialog(this, "");
        getGoodList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.iv_option:
                showOptionWindow(findViewById(R.id.iv_option));
                break;
            case R.id.btn_recharge_wechat:
                pay(WECHATPAY);
                break;
            case R.id.btn_recharge_alipay:
                pay(ALIPAY);
                break;
            default:

                break;
        }
    }

    private void getGoodList() {
        ReqSystemApi.reqGoodList(this, new RequestCallback<GoodListResult>() {
            @Override
            public void onRequestSuccess(GoodListResult object) {
                List<GoodListResult.GoodListEntity> goodList = new ArrayList<GoodListResult.GoodListEntity>();
                for (GoodListResult.GoodListEntity good : object.getGoodList()) {
                    if (good.getType().equals("1")) {
                        goodList.add(good);
                    }
                }
                if (mAdapter != null) {
                    mAdapter.setGoodList(goodList);
                    mAdapter.notifyDataSetChanged();
                }
                ShowUtils.dismissProgressDialog();
            }

            @Override
            public void onRequestFailure(GoodListResult error) {
                ShowUtils.dismissProgressDialog();
            }
        });
    }

    private void getBalance() {
        ReqUserApi.requsetUserBalance(this, UserInfoUtils.getUserInfo().getUserId(), new RequestCallback<UserBalanceResult>() {
            @Override
            public void onRequestSuccess(UserBalanceResult object) {
                if (object != null && object.getBlance() != null) {
                    tvBalance.setText(object.getBlance());
                }
            }

            @Override
            public void onRequestFailure(UserBalanceResult error) {

            }
        });
    }

    private void pay(final int type) {
        if (selectPosition == -1) {
            ShowUtils.showToast(R.string.please_select_money_num);
            return;
        }
//        if (type == RechargeActivity.WECHATPAY) {
//            ShowUtils.showToast(R.string.wechat_pay_error);
//            return;
//        }
        ShowUtils.showProgressDialog(this, R.string.text_loading);
        ReqOrderApi.reqNewOrder(RechargeActivity.this, type, mAdapter.getItem(selectPosition).getId(), new RequestCallback<NewOrderResult>() {
            @Override
            public void onRequestSuccess(NewOrderResult object) {
                ShowUtils.dismissProgressDialog();
                switch (type) {
                    case RechargeActivity.WECHATPAY:
                        if(object.getOrder() == null) {
                            ShowUtils.showToast(getString(R.string.create_order_fail) + object.getErrCode());
                            return;
                        }
                        Map<String, Object> wechatpayParams = new HashMap<String, Object>();
                        wechatpayParams.put("orderInfo", object.getOrder());

                        mPayApi = PayApiFactory.getPayApi(RechargeActivity.this, type);
                        mPayApi.pay(wechatpayParams);
                        break;
                    case RechargeActivity.ALIPAY:
                        if(StringUtils.isEmpty(object.getReturnInfo())) {
                            ShowUtils.showToast(getString(R.string.create_order_fail));
                            return;
                        }
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("orderInfo", object.getReturnInfo());

                        mPayApi = PayApiFactory.getPayApi(RechargeActivity.this, type);
                        mPayApi.setCallback(alixReturnCall, 100);
                        mPayApi.pay(params);
                        break;
                }
            }

            @Override
            public void onRequestFailure(NewOrderResult e) {
                ShowUtils.dismissProgressDialog();
                ShowUtils.showToast(getString(R.string.create_order_fail));
            }
        });
    }

    private Handler alixReturnCall = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(RechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
            }
        }
    };

    private PopupWindow optionWindow;

    private void showOptionWindow(View view) {
        if (optionWindow == null) {
            View popupView = View.inflate(this, R.layout.layout_recharge_popup, null);
            optionWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            optionWindow.setTouchable(true);
//            optionWindow.setOutsideTouchable(true);
            optionWindow.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.img_recharge_popup_bg));
            popupView.findViewById(R.id.tv_recharge_records).setOnClickListener(popupClickListener);
            popupView.findViewById(R.id.tv_consumption_records).setOnClickListener(popupClickListener);
        }
        optionWindow.showAtLocation(getWindow().getDecorView(), Gravity.TOP|Gravity.RIGHT, ShowUtils.dip2px(this, 10), ShowUtils.dip2px(this, 60));
    }

    View.OnClickListener popupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.tv_recharge_records:
                    intent = new Intent(RechargeActivity.this, ACEWebActivity.class);
                    intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.RECHARGERECORDS + UserInfoUtils.getUserInfo().getUserId());
                    startActivity(intent);
                    break;
                case R.id.tv_consumption_records:
                    intent = new Intent(RechargeActivity.this, ACEWebActivity.class);
                    intent.putExtra("url", WebViewConfig.getUrl() + WebViewConfig.CONSUMPTIONRECORDS + UserInfoUtils.getUserInfo().getUserId());
                    intent.putExtra(ACEWebActivity.CAN_GOBACK, false);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectPosition = position;
        mAdapter.setSelectPosition(position);
        mAdapter.notifyDataSetChanged();
    }
}
