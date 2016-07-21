//
//  AppDelegate.m
//  Ace
//
//  Created by 周兵 on 16/2/23.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "AppDelegate.h"
#import "RNTTabBarContronller.h"
#import "RNTChooseMainViewController.h"
#import "UMSocialQQHandler.h"
#import "RNTLoginViewController.h"
#import "UMSocialWechatHandler.h"
#import "UMSocial.h"
#import "UMSocialSinaSSOHandler.h"
#import "RNTRegisterViewController.h"
#import "MobClick.h"
#import "UMOnlineConfig.h"
#import "AFNetworkReachabilityManager.h"
#import "RNTLaunchImage.h"
#import "RNTJPushTool.h"

#define UMKey @"56dfe53467e58e1cac002462"
#define UMShareUrl @"http://17ace.cn/"

#define IOS_AppVersion [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    //推送信息
    NSDictionary* userInfo = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if(userInfo){
        self.userInfo = userInfo;
    }
    
    // 全局
    [RNTUserManager getSysConfig];
    
    //友盟分享
    [self setUMShare];
    
    //极光推送
    [RNTJPushTool setJPushWithOptions:launchOptions application:application];
    
    // 网络监测
    [self setupNetworkNotification];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    [self.window makeKeyAndVisible];
    
    //选择根控制器
    [RNTChooseMainViewController chooseMainViewContronller:self.window];

    //动态启动图
    [RNTLaunchImage show];
    
    //自动登录
    [[RNTUserManager sharedManager] autoLogin];
    
    //多点触控
    [self.window setExclusiveTouch:YES];
    
    return YES;
}

- (void)setupNetworkNotification
{
    AFNetworkReachabilityManager *manager = [AFNetworkReachabilityManager sharedManager];
    
    [manager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status) {
        switch (status) {
            case AFNetworkReachabilityStatusReachableViaWiFi:
                self.hasNetwork = YES;
                [[NSNotificationCenter defaultCenter] postNotificationName:RNTNetworkConnectedNotification object:nil];
                RNTLog(@"WIFI");
                break;
                
            case AFNetworkReachabilityStatusReachableViaWWAN:
                self.hasNetwork = YES;
                [[NSNotificationCenter defaultCenter] postNotificationName:RNTNetworkConnectedNotification object:nil];
                RNTLog(@"自带网络");
                break;
                
            case AFNetworkReachabilityStatusNotReachable:
                self.hasNetwork = NO;
                [[NSNotificationCenter defaultCenter] postNotificationName:RNTNetworkDisconnectedNotification object:nil];
                RNTLog(@"没有网络");
                break;
                
            case AFNetworkReachabilityStatusUnknown:
                self.hasNetwork = NO;
                [[NSNotificationCenter defaultCenter] postNotificationName:RNTNetworkDisconnectedNotification object:nil];
                RNTLog(@"未知网络");
                break;
            default:
                break;
        }
    }];
    [manager startMonitoring];
}

#pragma mark - 友盟
//友盟分享
-(void)setUMShare
{
    MobClickSetLogEnabled;
    
    //友盟Key
    [UMSocialData setAppKey:UMKey];
    
    [self setMobClick];//统计
    
    //QQ分享
    [UMSocialQQHandler setQQWithAppId:@"1105199076" appKey:@"qQMsCo1bkbslvcqa" url:UMShareUrl];
    
    //微信分享
    [UMSocialWechatHandler setWXAppId:@"wx743d2275c0a223e3" appSecret:@"e60d7678239e07261b9873fc1e29eb4a" url:UMShareUrl];

    //微博登录
    [UMSocialSinaSSOHandler openNewSinaSSOWithAppKey:@"942489697"
                                              secret:@"777a3db493c667f4a749e3746c3a319d"
                                         RedirectURL:@"http://www.51weibo.com/mobile/download.html"];
    
    [UMSocialConfig hiddenNotInstallPlatforms:@[UMShareToQQ,UMShareToQzone,UMShareToWechatSession,UMShareToWechatTimeline,UMShareToSina]];//如果没有安装客户端取消分享
    
    [UMOnlineConfig updateOnlineConfigWithAppkey:UMKey];//更新在线参数
}

//设置统计
-(void)setMobClick
{
    [MobClick setCrashReportEnabled:YES];//崩溃报告
    [MobClick setAppVersion:IOS_AppVersion];//version标识
    [MobClick setEncryptEnabled:YES];//日志加密设置
    [MobClick startWithAppkey:UMKey reportPolicy:(ReportPolicy) BATCH channelId:nil];//发送策略和渠道
}

#pragma mark - 微信 qq分享登陆代理方法
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    return  [UMSocialSnsService handleOpenURL:url];
}

//- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
//{
//    return  [UMSocialSnsService handleOpenURL:url];
//}
//
//- (void)applicationDidBecomeActive:(UIApplication *)application
//{
//    [UMSocialSnsService  applicationDidBecomeActive];
//}
////新浪
//- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
//{
//    return  [UMSocialSnsService handleOpenURL:url];
//}

#pragma mark - JPush
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    // Required
    [JPUSHService registerDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    // IOS 7 Support Required
    RNTLog(@"推送 = %@", userInfo);
    //清零数据
    [JPUSHService resetBadge];
    [application setApplicationIconBadgeNumber:0];
    
    [JPUSHService handleRemoteNotification:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
    
//    [RNTJPushTool application:application handleRemoteNotification:userInfo];
}

#pragma mark -----
- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application
{
    RNTLog(@"内存警告");
}

@end
