/*  ------------------------------------------------------------------------------ 
 *                  软件名称:BB语音
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2016年3月26日/2016
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.commucation.server
 *                  fileName：KeyWordUpdater.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.launch;

import java.util.List;

import org.apache.log4j.Logger;

import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.globalData.StaticDataManager;

/**
 * @author yongchao.Yang/2016年3月26日
 */
public class KeyWordUpdater extends Thread {
	private volatile boolean isRun = true;
	private static Logger logger = Logger.getLogger(KeyWordUpdater.class);

	private static KeyWordUpdater kwu = null;

	public static KeyWordUpdater getInstance() {
		if (kwu == null) {
			kwu = new KeyWordUpdater("KeyWordUpdater");
		}
		return kwu;
	}

	private KeyWordUpdater(String name) {
		super(name);
	}

	@Override
	public void run() {
		while (isRun) {
			try {
				List<String> chatWds = StaticDataManager.getKeyWord(Constant.KeyWordType.CHAT.getValue());
				Constant.updateKeyWord(chatWds);

				synchronized (this) {
					this.wait(60000);
				}
			} catch (Exception e) {
				logger.error("更新敏感词异常", e);
			}
		}

	}

}
