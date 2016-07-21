/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播移动
 *                  公司名称:美播娱乐
 *                  开发作者:sg.z
 *       			开发时间:2014年7月29日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自美播娱乐研发部，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：meibo-admin
 *                  fileName：UserHandler.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.sys;

import java.util.List;
import com.rednovo.ace.globalData.StaticDataManager;
import com.rednovo.ace.handler.BasicServiceAdapter;

/**
 * @author yongchao.Yang/2014年7月15日
 */
public class SystemHandler extends BasicServiceAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.power.handler.BasicServiceAdapter\t\t #service()
	 */
	@Override
	protected void service() {
		String key = this.getKey();
		if ("009-004".equals(key)) {//修改敏感词
			this.updateKeyWD();
		} else if ("009-005".equals(key)) {//获取敏感词
			this.getKeyWD();
		}
	}

	/**
	 * 获取敏感词
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月7日下午1:59:31
	 */
	private void getKeyWD() {
		String type = this.getWebHelper().getString("type");
		List<String> keywordlist = StaticDataManager.getKeyWord(type);
		this.setValue("keywordlist", keywordlist);
		this.setSuccess();

	}

	/**
	 * 更新敏感词 type = name为昵称敏感词 chat为聊天敏感词 methed add为添加 del为删除
	 * 
	 * @author Yongchao.Yang
	 * @since 2016年3月26日下午10:04:26
	 */
	private void updateKeyWD() {
		String txt = this.getWebHelper().getString("txt");
		String type = this.getWebHelper().getString("type");
		String methed = this.getWebHelper().getString("methed");
		if ("del".equals(methed)) {
			String[] keys = txt.split("\\^");
			// 移除敏感词
			StaticDataManager.delKeyWord(keys, type);
		} else {
			// 添加敏感词
			StaticDataManager.addKeyWord(txt, type);
		}
		this.setSuccess();

	}

}
