//
//  RNTJPushTool.h
//  Ace
//
//  Created by 周兵 on 16/5/13.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JPUSHService.h"

@interface RNTJPushTool : NSObject
/**
 *  初始化极光推送
 *
 *  @param launchOptions
 *  @param application
 */
+ (void)setJPushWithOptions:(NSDictionary *)launchOptions application:(UIApplication *)application;

/**
 *  上传JPush registrationID到本地服务器
 *
 *  @param userId 
 */
+ (void)registrationLocalJPushServerWithUserId:(NSString *)userId registrationID:(NSString *)registrationID;

/**
 *  处理极光推送消息
 *
 *  @param userInfo 
 */
+ (void)application:(UIApplication *)application handleRemoteNotification:(NSDictionary *)userInfo;

/**
 *  处理极光推送跳转
 *
 *  @param navigationController 
 */
+(void)handleJPushWithNavigationController:(UINavigationController *)navigationController;
@end
