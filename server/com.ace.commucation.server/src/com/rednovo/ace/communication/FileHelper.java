/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年11月10日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：FileHelper.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.communication;

import java.io.File;

import com.rednovo.ace.constant.Constant.ChatMode;
import com.rednovo.ace.constant.Constant.MsgType;
import com.rednovo.ace.entity.Summary;
import com.rednovo.tools.PPConfiguration;

/**
 * 文件助手
 * 
 * @author yongchao.Yang/2014年11月10日
 */
public class FileHelper {
	/**
	 * 获取客户端文件存放路径
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年3月25日上午11:19:12
	 */
	public static String getMsgFileSavePath() {
		return PPConfiguration.getProperties("cfg.properties").getString("msg.file.save.path");
	}

	/**
	 * 
	 * @return
	 * @author Yongchao.Yang
	 * @since 2015年4月13日下午2:39:29
	 */
	public static String getProfileSavePath() {
		return PPConfiguration.getProperties("cfg.properties").getString("img.file.save.path");
	}

	/**
	 * 创建用户、群目录
	 * 
	 * @param userId
	 * @author Yongchao.Yang
	 * @since 2015年4月13日下午6:39:14
	 */
	public static void createUserDir(int uidLen, int depth, String parentPath) {
		if (uidLen - depth < 3) {
			return;
		}

		int i = 0;
		if (depth == 1) {
			i = 1;
		}
		for (; i <= 9; i++) {
			String path = parentPath + File.separator + i;
			File newDir = new File(path);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}
			createUserDir(6, depth + 1, path);
		}
	}

	/**
	 * 获取消息文件存放路径
	 * 
	 * @param ppId
	 * @return
	 * @author YongChao.Yang/2012-11-1/2012
	 */
	public static String getDir(Summary smy) {
		StringBuffer basePath = new StringBuffer();
		String id = "";
		if ((MsgType.MEDIA_MSG_AUDIO.getValue().equals(smy.getMsgType()) || MsgType.MEDIA_MSG_PIC.getValue().equals(smy.getMsgType())) && ChatMode.PRIVATE.getValue().equals(smy.getChatMode())) {// 私聊，媒体消息
			basePath.append(PPConfiguration.getProperties("cfg.properties").getString("msg.file.save.path") + File.separator + "user");
			id = smy.getSenderId();
		} else if ((MsgType.MEDIA_MSG_AUDIO.getValue().equals(smy.getMsgType()) || MsgType.MEDIA_MSG_PIC.getValue().equals(smy.getMsgType())) && ChatMode.GROUP.getValue().equals(smy.getChatMode())) {// 群聊，媒体消息
			basePath.append(PPConfiguration.getProperties("cfg.properties").getString("msg.file.save.path") + File.separator + "group");
			id = smy.getShowId();
		} else if (MsgType.BACKGROUND_PHOTO_PRIVATE.getValue().equals(smy.getMsgType()) || MsgType.PHOTO_PRIVATE.getValue().equals(smy.getMsgType())) {// 上传 用户图像
			basePath.append(PPConfiguration.getProperties("cfg.properties").getString("photo.file.save.path") + File.separator + "user");
			id = smy.getSenderId();
		} else if (MsgType.BACKGROUND_PHOTO_GROUP.getValue().equals(smy.getMsgType()) || MsgType.PHOTO_GROUP.getValue().equals(smy.getMsgType())) {// 上传 群组图像
			basePath.append(PPConfiguration.getProperties("cfg.properties").getString("photo.file.save.path") + File.separator + "group");
			id = smy.getShowId();
		}
		int len = id.length() - 3;
		for (int i = 0; i < len; i++) {
			basePath.append(File.separator + id.charAt(i));
		}
		basePath.append(File.separator + id);
		return basePath.toString();
	}

	public static void main(String[] args) {
		FileHelper.createUserDir(6, 1, PPConfiguration.getProperties("cfg.properties").getString("msg.file.save.path") + File.separator + "user");
		FileHelper.createUserDir(6, 1, PPConfiguration.getProperties("cfg.properties").getString("msg.file.save.path") + File.separator + "group");
		FileHelper.createUserDir(6, 1, PPConfiguration.getProperties("cfg.properties").getString("photo.file.save.path") + File.separator + "user");
		FileHelper.createUserDir(6, 1, PPConfiguration.getProperties("cfg.properties").getString("photo.file.save.path") + File.separator + "group");
	}
}
