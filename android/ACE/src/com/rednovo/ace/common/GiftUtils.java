package com.rednovo.ace.common;

import android.text.TextUtils;

import com.rednovo.ace.net.api.ReqRoomApi;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.libs.common.CacheKey;
import com.rednovo.libs.common.CacheUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼物工具类
 */
public class GiftUtils {
    private static List<GiftListResult.GiftListEntity> gifts;
    private static Map<String, GiftListResult.GiftListEntity> giftMap;

    public static List<GiftListResult.GiftListEntity> getGiftList() {
        if (gifts == null) {
            Object o = CacheUtils.getObjectCache().get(CacheKey.GIFT_LIST);
            if (o != null) {
                gifts = ((GiftListResult) o).getGiftList();
                CacheGiftMap(gifts);
            } else {
                reqGiftList();
            }
        }
        return gifts;
    }

    private static void CacheGiftMap(List<GiftListResult.GiftListEntity> gifts) {
        List<String> giftIdList = new ArrayList<String>();
        if (giftMap == null) {
            giftMap = new HashMap<String, GiftListResult.GiftListEntity>();
        }
        if (gifts != null && gifts.size() > 0) {
            for (GiftListResult.GiftListEntity gift : gifts) {
                giftMap.put(gift.getId(), gift);
                giftIdList.add(gift.getId());
            }
        }
        Cocos2dxAnimationAttacher.getInstance().initAttacher(giftIdList);
        Cocos2dxAnimationAttacher.getInstance().detectionAll();
    }

    /**
     * 网络请求最新礼物列表
     *
     * @param
     */
    private static void reqGiftList() {
        ReqRoomApi.reqGiftList(new RequestCallback<GiftListResult>() {
            @Override
            public void onRequestSuccess(GiftListResult object) {
                CacheUtils.getObjectCache().add(CacheKey.GIFT_LIST, object);
                gifts = object.getGiftList();
                CacheGiftMap(gifts);
                List<String> giftIdList = new ArrayList<String>();
                for(GiftListResult.GiftListEntity entity : gifts){
                    giftIdList.add(entity.getId());
                }
                Cocos2dxAnimationAttacher.getInstance().initAttacher(giftIdList);
                Cocos2dxAnimationAttacher.getInstance().detectionAll();
            }

            @Override
            public void onRequestFailure(GiftListResult error) {

            }
        });
    }

    /**
     * 初始化礼物数据信息
     *
     * @param version
     */
    public static void init(String version) {
        if(TextUtils.isEmpty(version)){
            getGiftList();
            return;
        }
        float vsion = Float.parseFloat(version);
        float currentVersion = (float) CacheUserInfoUtils.get(CacheUserInfoUtils.GIFT_VERSION, 0f);
        if (vsion > currentVersion) {
            reqGiftList();
        } else {
            getGiftList();
        }
        CacheUserInfoUtils.put(CacheUserInfoUtils.GIFT_VERSION, vsion);
    }

    /**
     * 根据id获取礼物信息
     *
     * @param id
     * @return
     */
    public static GiftListResult.GiftListEntity getGiftById(String id) {
        if (giftMap == null || giftMap.size() == 0) {
            return null;
        } else {
            return giftMap.get(id);
        }
    }

}
