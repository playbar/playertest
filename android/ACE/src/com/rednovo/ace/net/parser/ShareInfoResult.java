package com.rednovo.ace.net.parser;

import android.graphics.Bitmap;

/**
 * Created by Dk on 16/3/3.
 */
public class ShareInfoResult extends BaseResult{

    /**
     * imgSrc : http://172.16.150.21/images/4/20160324041828424404534431-show.png
     * sumy : 浓浓端午情,品尝鲜美粽子之余，让ACE带你发现世界的精彩
     * title : 仰永潮正在直播，欢迎围观
     * url : api.17ace.cn/share/index.html
     */

    private String imgSrc;
    private String sumy;
    private String title;
    private String url;

    public Bitmap image;
    public String appName;
    public String videoUrl;
    public String musicUrl;

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getSumy() {
        return sumy;
    }

    public void setSumy(String sumy) {
        this.sumy = sumy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
