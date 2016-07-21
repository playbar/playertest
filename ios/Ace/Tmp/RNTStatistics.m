//
//  RNTStatistics.m
//  Weibo
//
//  Created by 靳峰 on 16/3/7.
//  Copyright (c) 2016年 Rednovo. All rights reserved.
//

#import "RNTStatistics.h"
#import "RNTNetTool.h"
#import "UMOnlineConfig.h"


#define VersionString [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]

@implementation RNTStatistics
//是够能够进入内购
+ (BOOL)canEnterIAP {
    //提交审核时将友盟后台配置改为当前审核版本，当为审核版本或者配置为空时商城开关为关闭状态
    
    NSString *MallSwitch =[UMOnlineConfig getConfigParams:@"MallSwitch"];
    
    if (!MallSwitch || [MallSwitch isEqualToString:VersionString]) {
        return NO;
    }
    
    return YES;
}


+(NSString *)getNetWorkStates
{
    UIApplication *app = [UIApplication sharedApplication];
    NSArray *children = [[[app valueForKeyPath:@"statusBar"]valueForKeyPath:@"foregroundView"]subviews];
    NSString *state = [[NSString alloc]init];
    int netType = 0;
    //获取到网络返回码
    for (id child in children) {
        if ([child isKindOfClass:NSClassFromString(@"UIStatusBarDataNetworkItemView")])
        {
            //获取到状态栏
            netType = [[child valueForKeyPath:@"dataNetworkType"]intValue];
            
            switch (netType) {
                case 0:
                    state = @"无网络";
                    //无网模式
                    break;
                case 1:
                    state = @"2G";
                    break;
                case 2:
                    state = @"3G";
                    break;
                case 3:
                    state = @"4G";
                    break;
                case 5:
                {
                    state = @"WIFI";
                }
                    break;
                default:
                    break;
            }
        }
    }
    //根据状态选择
    return state;
}

/**
 *  搜索
 */
+ (void)search {
//    [MobClick event:@"search"];
//    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}
/**
 *  直播
 */
+ (void)live {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  关注
 */
+ (void)attention {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  我的
 */
+ (void)mine {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  首页
 */
+ (void)lv_Home {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  设置
 */
+ (void)me_setting {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  绑定手机
 */
+ (void)me_set_Bind {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  退出
 */
+ (void)me_set_quit {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  意见反馈
 */
+ (void)me_set_feedback {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  资料编辑
 */
+ (void)me_editor {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  标签
 */
+ (void)me_et_label {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  浏览记录
 */
+ (void)me_history {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}
/**
 *  观众
 */
+ (void)lv_viewers {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  更多
 */
+ (void)lv_more {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  充值
 */
+ (void)lv_mo_recharge {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  修改昵称
 */
+ (void)lv_mo_modify {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  分享
 */
+ (void)lv_mo_share {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  微信分享
 */
+ (void)lv_mo_share_wech {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  朋友圈分享
 */
+ (void)lv_mo_share_wechat {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  QQ分享
 */
+ (void)lv_mo_share_QQ {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  QQ空间分享
 */
+ (void)lv_mo_share_Qzone {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  举报
 */
+ (void)lv_mo_report {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  充值（礼物）
 */
+ (void)lv_gf_recharge {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  账户中心
 */
+ (void)me_account {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  充值（账户中心）
 */
+ (void)me_act_recharge {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  兑换
 */
+ (void)me_act_exchange {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  充值记录
 */
+ (void)me_act_r_record {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

/**
 *  消费记录
 */
+ (void)me_act_c_record {
    //    [MobClick event:@"search"];
    //    [AnalyticsInterface onUserAction:@"search" isSucceed:YES elapse:0 size:0 params:nil];
}

@end
