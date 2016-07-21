package com.rednovo.ace.net.api;

import android.app.Activity;

import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.net.parser.HotResult;
import com.rednovo.ace.net.parser.NewOrderResult;
import com.rednovo.ace.net.request.HttpBase;
import com.rednovo.ace.net.request.HttpMethodPost;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.SystemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lilong on 16/3/6.
 */
public class ReqOrderApi {
    private static String url_suffix = "service/";

    /**
     * 新建订单信息
     * @param activity
     * @param type 支付方式(1:微信,2:支付宝)
     * @param goodId 商品ID
     * @param callback
     * @return
     */
    public static HttpBase reqNewOrder(Activity activity, int type, String goodId, RequestCallback<NewOrderResult> callback){
        // "http://203.187.166.237:8080/"
        // payerId:用户ID,receiverId:接收用户ID,goodId:商品ID,goodCnt:商品数量,payChannel:支付方式(1:微信,2:支付宝)
        Map<String, String> params = new HashMap<String, String>();
        params.put("payerId", UserInfoUtils.getUserInfo().getUserId());
        params.put("receiverId", UserInfoUtils.getUserInfo().getUserId());
        params.put("goodId", goodId);
        params.put("goodCnt", "1");
        params.put("payChannel", type + "");
        if(type == 1) {
            params.put("ip", SystemUtils.getLocalHostIp());
        }
        return new HttpMethodPost<NewOrderResult>(NewOrderResult.class)
                .addUrlArgument(ReqConfig.getUrl() + url_suffix + ReqConfig.KEY_NEW_ORDER)
                .addArguments(params)
                .addTag(activity)
                .execute(callback);
    }

}
