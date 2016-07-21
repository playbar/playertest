/*  ------------------------------------------------------------------------------ 
 *                  软件名称:
 *                  公司名称:
 *                  开发作者:ZuKang.Song
 *       			开发时间:2016年6月13日/2016
 *    				All Rights Reserved 2016-2016
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.ace.web.service
 *                  fileName：UserMenuHandle.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.handler.menu;

import java.util.ArrayList;
import com.ace.database.service.UserMenuService;
import com.rednovo.ace.constant.Constant;
import com.rednovo.ace.handler.BasicServiceAdapter;

/**
 * manager用户权限控制
 * 
 * @author ZuKang.Song/2016年6月13日
 */
public class UserMenuHandle extends BasicServiceAdapter {

	@Override
	protected void service() {
		String key = this.getKey();
		if ("001-036".equals(key)) {// 获取用户权限
			this.getUserMenu();
		} else if ("001-035".equals(key)) {// 修改用户权限
			this.updateUserMenu();
		}

	}

	/**
	 * 修改用户权限
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午3:15:02
	 */
	private void updateUserMenu() {
		String userId = this.getWebHelper().getString("userId");
		String menus = this.getWebHelper().getString("menus");
		String exeCode = UserMenuService.updateUserMenu(userId, menus);
		if (Constant.OperaterStatus.SUCESSED.getValue().equals(exeCode)) {
			this.setSuccess();
		} else {
			this.setError(exeCode);
		}
	}

	/**
	 * 获取用户权限
	 * 
	 * @author ZuKang.Song
	 * @since 2016年6月13日下午3:15:01
	 */
	private void getUserMenu() {
		String userId = this.getWebHelper().getString("userId");
		ArrayList<String> menuList = UserMenuService.getUserMenu(userId);
		this.setValue("menuList", menuList);
		this.setSuccess();
	}

}
