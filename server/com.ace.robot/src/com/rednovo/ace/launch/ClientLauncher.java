/*  ------------------------------------------------------------------------------ 
 *                  软件名称:美播手机版
 *                  公司名称:多宝科技
 *                  开发作者:Yongchao.Yang
 *       			开发时间:2014年10月2日/2014
 *    				All Rights Reserved 2012-2015
 *  ------------------------------------------------------------------------------
 *    				注意:本内容均来自乐多科技，仅限内部交流使用,未经过公司许可 禁止转发    
 *  ------------------------------------------------------------------------------
 *                  prj-name：com.rednovo.ace.communication
 *                  fileName：ClientLauncher.java
 *  -------------------------------------------------------------------------------
 */
package com.rednovo.ace.launch;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.client.ClientEventInvoker;
import com.rednovo.ace.communication.client.ClientSession;
import com.rednovo.ace.communication.server.broadcast.HeartBeatRunner;

/**
 * @author yongchao.Yang/2014年10月2日
 */
public class ClientLauncher {
	/**
	 * 发送消息
	 * 
	 * @param msg
	 * @author Yongchao.Yang
	 * @since 2014年10月14日下午6:14:10
	 */
	public void openSession() {
		ClientEventInvoker cei = new ClientEventInvoker();
		EventInvokerManager mgr = EventInvokerManager.getInstance();
		mgr.registInvoker(cei);
		ClientSession cs = ClientSession.getInstance();

		try {
			cs.openSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopHeartBeat() {
		HeartBeatRunner.getInstance().startRun();
	}

	public static void main(String[] args) {
		ClientLauncher c = new ClientLauncher();
		c.openSession();
		// 注册
		// LogicOperator.regist("11111111111", "654321", "111111");
		// LogicOperator.login("13426352452", "yyc_01", "0");

		// 进入直播间
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO: handle exception
		}

		// LogicOperator.ping("hello");
		LogicOperator.enter("4535", "4266");
		// LogicOperator.sendGift("2152", "4055", "4055", "10", 1);
		// LogicOperator.sendGift("2152", "2152", "1", "10", 1);

		// 注入当前用户信息，通过心跳更新服务器端状态
		// ClientHeartBeatRunner.getInstance().setUser("200008");
		// ClientHeartBeatRunner.getInstance().startRun();

		LogicOperator.chat("习近平，习近平");
		LogicOperator.chat("习近平，习近平");
		LogicOperator.chat("习近平，习近平");
		LogicOperator.chat("习近平，习近平");
		LogicOperator.chat("习近平，习近平");
		// LogicOperator.chat("妹子，你这是要作死吗2？？");
		// LogicOperator.chat("妹子，你这是要作死吗3？？");
		// LogicOperator.chat("妹子，你这是要作死吗4？？");
		// LogicOperator.chat("妹子，你这是要作死吗5？？");
		// LogicOperator.chat("妹子，你这是要作死吗6？？");
		// LogicOperator.chat("妹子，你这是要作死吗7？？");
		// LogicOperator.chat("妹子，你这是要作死吗8？？");
		// LogicOperator.enter();
		// LogicOperator.enter();

		// LogicOperator.finishUserInfo("200005", "孙卫红", "1");
		// LogicOperator.setNickName("200000", "化优");
		// LogicOperator.setSex("200001", "1");
		// LogicOperator.setSignature("200002", "百年之后谁还记得你");
		// LogicOperator.uploadPhoto("200012",
		// Constant.MsgType.PHOTO_GROUP.getValue(), "f:/12.jpg");
		// LogicOperator.resetPasswd("200002", "11111", "123456");
		// LogicOperator.sendSMS("", "13426352452", "您的短信验证码是78as6d");
		// LogicOperator.inviteFriend("200005", "200002");
		// LogicOperator.addFriend("200000", "200001");
		// LogicOperator.delFriend("200000", "200001");
		// LogicOperator.getFriendList("200000");
		// LogicOperator.getUserInfo("200000", "200001");
		// LogicOperator.creatGroup("200000", "仰永潮的群");
		// LogicOperator.getUserGroup("200000");

		// LogicOperator.getGroupRTData("200001");
		// LogicOperator.addGroupMembers("200001", "200003^200004");
		// LogicOperator.updateGroupMemberNickName("200001", "200001", "快乐红松鼠");
		// LogicOperator.getGroupMembers("200001");
		// LogicOperator.getGroupMemberInfo("200001", "200001");
		// LogicOperator.removeGroupMembers("200001", "200001");
		// LogicOperator.updateGroupName("200001", "人生如茶");
		// LogicOperator.updateGroupSignature("200001", "你是我淡淡的忧伤,如影随形,如痴如醉");
		// LogicOperator.getGroup("200010", "200111");

		// LogicOperator.reportClientEvent("200001", "200000",
		// EventType.REQUEST_PRESENT.getValue());
		// LogicOperator.reportClientEvent("200001", "200001",
		// EventType.REQUEST_PRESENT.getValue());
		// LogicOperator.reportClientEvent("200001", "200002",
		// EventType.ENTER.getValue());
		// LogicOperator.reportClientEvent("200001", "200003",
		// EventType.ENTER.getValue());
		// LogicOperator.reportClientEvent("200001", "200004",
		// EventType.ENTER.getValue());

		// LogicOperator.reportClientEvent("200001", "200049",
		// EventType.ON_PRESENT.getValue());//主麦上线
		// LogicOperator.reportClientEvent("200001", "200050",
		// EventType.ON_SECOND_PRESENT.getValue());//副麦上线
		// LogicOperator.reportClientEvent("200001", "200051",
		// EventType.ON_SECOND_PRESENT.getValue());//副麦上线
		// LogicOperator.reportClientEvent("200001", "200010",
		// EventType.ON_SECOND_PRESENT.getValue());//副麦上线

		// LogicOperator.reportClientEvent("200001", "200001",
		// EventType.ENTER.getValue());//副麦上线
		// LogicOperator.reportClientEvent("200001", "200002",
		// EventType.ENTER.getValue());//副麦上线

		// LogicOperator.reportClientEvent("200001", "200002",
		// EventType.ON_PRESENT);
		// for (int i = 0; i < 1000; i++) {
		// LogicOperator.ping("hello,");
		// }

		// for (int i = 1; i < 1001; i++) {
		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// LogicOperator.chat("给你  " + i + "  万");
		// }
		// LogicOperator.chat("11");
		// LogicOperator.chat("你欠");
		// LogicOperator.chatMediaMsg();

		// LogicOperator.suggest("200000", "BB是个好东西");

		// LogicOperator.searchUser("200001", "yyc", "1", "10");
		// LogicOperator.reportClientEvent("200001", "200000",
		// EventType.ENTER.getValue());
		// LogicOperator.getPresenter("200111");
		// LogicOperator.getSysToy();

		// GroupManagerTest.addAdmin();
		//
		//

	}

}
