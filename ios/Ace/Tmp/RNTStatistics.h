//
//  RNTStatistics.h
//  Weibo
//
//  Created by 靳峰 on 16/3/7.
//  Copyright (c) 2016年 Rednovo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTStatistics : NSObject

/**
 *  是否可进入内购
 *
 *  @return YES，可进入 NO，不可进入
 */
+ (BOOL)canEnterIAP;

/**
 *  搜索
 */
+ (void)search;

/**
 *  直播
 */
+ (void)live;

/**
 *  关注
 */
+ (void)attention;

/**
 *  我的
 */
+ (void)mine;

/**
 *  首页
 */
+ (void)lv_Home;

/**
 *  设置
 */
+ (void)me_setting;

/**
 *  绑定手机
 */
+ (void)me_set_Bind;

/**
 *  退出
 */
+ (void)me_set_quit;

/**
 *  意见反馈
 */
+ (void)me_set_feedback;

/**
 *  资料编辑
 */
+ (void)me_editor;

/**
 *  浏览记录
 */
+ (void)me_history;

/**
 *  观众
 */
+ (void)lv_viewers;

/**
 *  更多
 */
+ (void)lv_more;

/**
 *  修改昵称
 */
+ (void)lv_mo_modify;

/**
 *  分享
 */
+ (void)lv_mo_share;

/**
 *  微信分享
 */
+ (void)lv_mo_share_wech;

/**
 *  朋友圈分享
 */
+ (void)lv_mo_share_wechat;

/**
 *  QQ分享
 */
+ (void)lv_mo_share_QQ;

/**
 *  QQ空间分享
 */
+ (void)lv_mo_share_Qzone;

/**
 *  举报
 */
+ (void)lv_mo_report;

/**
 *  充值（礼物）
 */
+ (void)lv_gf_recharge;
/**
 *  账户中心
 */
+ (void)me_account;

/**
 *  充值（账户中心）
 */
+ (void)me_act_recharge;

/**
 *  兑换
 */
+ (void)me_act_exchange;

/**
 *  消费记录
 */
+ (void)me_act_c_record;

@end
