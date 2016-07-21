package com.rednovo.ace.net.parser;

/**
 * 系统总控相关信息
 */
public class SystemResult extends BaseResult {

    /**
     * ad : 1
     * bootPic : http://172.16.150.21/images/boot.jpg
     * giftVer : 2.94
     * goodVer : 2.94
     * sysUpdateType : 1
     * sysVer : 2.94
     */

    private String ad;
    private String bootPic;//首页启动广告页
    private String giftVer;//礼物版本号
    private String goodVer;
    private String sysUpdateType;//更新类型 0、普通更新，1、强制更新。
    private String sysVer;
    private String showTips;//进入直播间提示信息
    private String updateInfo;//更新内容
    private String updateURL;//更新Url
    private String androidVer;//更新的版本号
    private int isVerify;//是否开启实名认证

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setBootPic(String bootPic) {
        this.bootPic = bootPic;
    }

    public void setGiftVer(String giftVer) {
        this.giftVer = giftVer;
    }

    public void setGoodVer(String goodVer) {
        this.goodVer = goodVer;
    }

    public void setSysUpdateType(String sysUpdateType) {
        this.sysUpdateType = sysUpdateType;
    }

    public void setSysVer(String sysVer) {
        this.sysVer = sysVer;
    }

    public String getAd() {
        return ad;
    }

    public String getBootPic() {
        return bootPic;
    }

    public String getGiftVer() {
        return giftVer;
    }

    public String getGoodVer() {
        return goodVer;
    }

    public String getSysUpdateType() {
        return sysUpdateType;
    }

    public String getSysVer() {
        return sysVer;
    }

    public String getShowTips() {
        return showTips;
    }

    public void setShowTips(String showTips) {
        this.showTips = showTips;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public String getAndroidVer() {
        return androidVer;
    }

    public void setAndroidVer(String androidVer) {
        this.androidVer = androidVer;
    }

    public int getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(int isVerify) {
        this.isVerify = isVerify;
    }
}
