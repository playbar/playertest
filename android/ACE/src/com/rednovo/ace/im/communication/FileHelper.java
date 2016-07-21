/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:zhen.Li
 *       			开发时间:2015年04月20日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.im.communication
 *                  fileName：FileHelper.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.im.communication;


import com.rednovo.libs.common.StorageUtils;

/**
 * 接收语音与图片存储的Sdcard位置,必须与jar包中的位置对应
 *
 * @author zhen.Li/2015-4-20
 */
public class FileHelper {

    public static String getMsgFileSavePath() {
        return StorageUtils.getWritePath();
    }

    public static String getReadPath() {
        return StorageUtils.getReadPath();
    }

}
