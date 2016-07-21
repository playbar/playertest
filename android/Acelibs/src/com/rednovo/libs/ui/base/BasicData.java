/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:zhen.Li
 *       			开发时间:2015-7-14/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：BB
 *                  fileName：BasicData.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.libs.ui.base;

import com.alibaba.fastjson.JSON;
import com.rednovo.libs.common.SystemUtils;

import java.io.Serializable;

/**
 * @author zhen.Li/2015-7-14
 */
public class BasicData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String iMEI;
    private String timesTamp;
    private String brand;
    private String macAddress;
    private String version;
    private String deviceType;


    public BasicData(String sIMEI, String sTimestamp, String sBrand, String sMacAddress, String sVersion, String deviceType) {
        this.iMEI = sIMEI;
        this.timesTamp = sTimestamp;
        this.brand = sBrand;
        this.macAddress = sMacAddress;
        this.version = sVersion;
        this.deviceType = deviceType;
    }

    public static String formatJson() {
        return new BasicData(SystemUtils.getIMEI(), System.currentTimeMillis() + "", SystemUtils.getBrand(), SystemUtils.getMacAddress(), SystemUtils.getOSVersion(), "0").toJson();
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }


}
