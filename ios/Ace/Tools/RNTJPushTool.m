//
//  RNTJPushTool.m
//  Ace
//
//  Created by 周兵 on 16/5/13.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTJPushTool.h"
#import "AppDelegate.h"
#import "RNTNetTool.h"
#import "RNTWebViewController.h"
#import "RNTPlayViewController.h"
#import "MJExtension.h"
#import "RNTIntoPlayModel.h"

#define JPushKey @"65cc5f2f9eca417081409311"

@implementation RNTJPushTool

//设置极光推送
+ (void)setJPushWithOptions:(NSDictionary *)launchOptions application:(UIApplication *)application
{
    [JPUSHService registerForRemoteNotificationTypes:(UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert) categories:nil];
    
    [JPUSHService setupWithOption:launchOptions appKey:JPushKey channel:nil apsForProduction:NO];
    
    //角标清零
    [JPUSHService resetBadge];
    [application setApplicationIconBadgeNumber:0];
}

+ (void)registrationLocalJPushServerWithUserId:(NSString *)userId registrationID:(NSString *)registrationID
{
    if (!userId || !registrationID) return;
    
    NSDictionary *params = @{@"userId":userId, @"tokenId":registrationID, @"deviceType":@"1", @"provider":@"1"};//deviceType: 设备类型 0安卓 1 ios provider:提供商  1极光
    [RNTNetTool postJSONWithURL:@"001-027" params:params success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            RNTLog(@"JPush registrationID上传成功");
        } else {
            RNTLog(@"JPush registrationID上传失败");
        }
    } failure:^(NSError *error) {
        RNTLog(@"%@", error.localizedDescription);
    }];
}

+ (void)application:(UIApplication *)application handleRemoteNotification:(NSDictionary *)userInfo
{
    if ([userInfo[@"type"] isEqualToString:@"3"]) {
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 64, kScreenWidth, 30)];
        label.alpha = 0;
        label.textAlignment = NSTextAlignmentCenter;
        label.text = userInfo[@"aps"][@"alert"];
        [application.keyWindow addSubview:label];
        
        [UIView animateWithDuration:2 animations:^{
            label.alpha = 1.0;
        } completion:^(BOOL finished) {
            [UIView animateWithDuration:2 animations:^{
                label.alpha = 0;
            } completion:^(BOOL finished) {
                [label removeFromSuperview];
            }];
        }];
    }
}

+(void)handleJPushWithNavigationController:(UINavigationController *)navigationController
{
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    
    NSDictionary *userDict = delegate.userInfo;
    
    //推送消息
    if (userDict) {
        //跳网页
        if ([userDict[@"type"] isEqualToString:@"2"]) {
            
            NSString *title =userDict[@"title"];
            NSString *url = userDict[@"url"];
            
            if (url.length>0) {
                
                if (![url hasPrefix:@"http://"] && ![url hasPrefix:@"https://"]) {
                    url = [@"http://" stringByAppendingString:url];
                }
                
                url = [url stringByReplacingOccurrencesOfString:@" " withString:@""];
                [navigationController pushViewController:[RNTWebViewController webViewControllerWithTitle:title url:url] animated:YES];
            }
            
            //跳直播间
        }else if ([userDict[@"type"] isEqualToString:@"3"]){
            
            RNTPlayViewController* playVC = [[RNTPlayViewController alloc] init];
            
            RNTUser *user =[RNTUser mj_objectWithKeyValues:userDict[@"user"]];
            
            if (user.userId.length>0 && user.showId.length >0 && user.downStreanUrl.length>0){
                
                playVC.model = [RNTIntoPlayModel getModelWithUerMode:user andShowModel:nil];
                
                [navigationController pushViewController:playVC animated:YES];
            }
        }
    }
}
@end
