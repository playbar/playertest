/*  ------------------------------------------------------------------------------ 
 *                  软件名称:IM
 *                  公司名称:乐多科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2015年4月13日/2015
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.leduo.bb.imserver
 *                  fileName：RedisConfig.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.tools;

/**
 * @author yongchao.Yang/2015年4月13日
 */
public class RedisConfig {

	public enum CacheName {
		USER_INFO_DATA(RedisNode.USER_NODE),STATIC_DATA(RedisNode.USER_NODE), UID_POOL(RedisNode.USER_NODE),SERVER_DATA(RedisNode.SERVER_NODE), LIVESHOW_INFO_DATA(RedisNode.LIVESHOW_NODE), LIVESHOW_MEMBER_DATA(RedisNode.LIVESHOW_NODE), USER_FANS_DATA(RedisNode.USER_RELATION_NODE), USER_SUBCRIBE_DATA(RedisNode.USER_RELATION_NODE);

		private RedisNode serverNode;

		private CacheName(RedisNode node) {
			serverNode = node;
		}

		public RedisNode getNode() {
			return serverNode;
		}
	}

	/**
	 * redis服务器节点名称
	 * 
	 * @author yongchao.Yang/2014年8月9日
	 */
	public static enum RedisNode {
		LIVESHOW_NODE("show-node"), LIVESHOW_MEMBER_NODE("show-node"), USER_NODE("user-node"), SERVER_NODE("server-node"), USER_RELATION_NODE("user-node");
		private String serverName;

		private RedisNode(String value) {
			this.serverName = value;
		}

		public String getName() {
			return this.serverName;
		}

	};

}
