package com.rednovo.tools.push;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import com.rednovo.tools.PPConfiguration;

/**
 * 极光推送服务
 * 
 * @author yongchao.Yang/2016年5月24日
 */
public class JPushService {
	protected static final Logger LOG = LoggerFactory.getLogger(JPushService.class);

	private static final String appKey = PPConfiguration.getProperties("cfg.properties").getString("app.pushed.stander.key");
	private static final String masterSecret = PPConfiguration.getProperties("cfg.properties").getString("app.pushed.stander.mastercecret");
	private static final String tencentappKey = PPConfiguration.getProperties("cfg.properties").getString("app.pushed.tencent.key");
	private static final String tencentmasterSecret = PPConfiguration.getProperties("cfg.properties").getString("app.pushed.tencent.mastercecret");
	private static JPushClient jpushClient = new JPushClient(masterSecret, appKey);
	private static JPushClient tencentjpushClient = new JPushClient(tencentmasterSecret, tencentappKey);

	/**
	 * 
	 * @author ZuKang.Song
	 * @param tips String 推送标题
	 * @param extarMap Map 客户端参数
	 * @param androidlist android手机用户列表
	 * @param ioslist 苹果手机用户列表
	 * @param isOnline boolean 环境类型 线上线下
	 * @since 2016年5月12日下午3:04:11
	 */
	public static void push(String tips, Map<String, String> extarMap, ArrayList<String> androidlist, ArrayList<String> ioslist, boolean isOnline) {

		try {
			if (ioslist != null && ioslist.size() > 0) {
				PushPayload payloadios = sendIosNotificationWithRegistrationID(tips, extarMap, ioslist, isOnline);
				PushResult result = jpushClient.sendPush(payloadios);
				LOG.info("Got IOS-result - " + result);
			}
			if (androidlist != null && androidlist.size() > 0) {
				PushPayload payloadandroid = sendAndroidNotificationWithRegistrationID(tips, extarMap, androidlist);
				PushResult result1 = jpushClient.sendPush(payloadandroid);
				LOG.info("Got android-result - " + result1);
				PushResult tencentresult = tencentjpushClient.sendPush(payloadandroid);
				LOG.info("Got android-tencentresult - " + tencentresult);
			}
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);

		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
			LOG.info("Msg ID: " + e.getMsgId());
		}

	}

	/**
	 * @param alert2
	 * @param extarMap
	 * @param idlist
	 * @param flag
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年5月12日下午5:07:31
	 */
	private static PushPayload sendIosNotificationWithRegistrationID(String alert2, Map<String, String> extarMap, ArrayList<String> idlist, boolean flag) {

		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.registrationId(idlist)) // 设备列表
				.setNotification(Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder().setAlert(alert2).incrBadge(1) // 角标+1
						.setSound("default").addExtras(extarMap).build()).build()).setOptions(Options.newBuilder().setApnsProduction(flag).build()).build();
	}

	/**
	 * @param alert2
	 * @param extarMap
	 * @param idlist
	 * @return
	 * @author ZuKang.Song
	 * @since 2016年5月12日下午4:46:55
	 */
	private static PushPayload sendAndroidNotificationWithRegistrationID(String alert, Map<String, String> extarMap, ArrayList<String> idlist) {

		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.registrationId(idlist)).setNotification(Notification.newBuilder().addPlatformNotification(AndroidNotification.newBuilder().setAlert(alert).addExtras(extarMap).build()).build()).build();
	}

}
