//
//  RNTChooseMainViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTChooseMainViewController.h"
#import "RNTTabBarContronller.h"
#import "RNTNewFeatureViewController.h"
#import "RNTLoginViewController.h"
#import "RNTNavigationController.h"

#define UserDefaults [NSUserDefaults standardUserDefaults]
#define VersionKey @"version"

@interface RNTChooseMainViewController ()

@end

@implementation RNTChooseMainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}
+(void)chooseMainViewContronller:(UIWindow *)keyWindow
{
    // 1.判断是否有新特性,用户的软件有新版本的时候
    // 获取Info.plist
    NSDictionary *infoDict = [NSBundle mainBundle].infoDictionary;
    // 获取当前用户的软件版本
    NSString *currentVersion = infoDict[@"CFBundleShortVersionString"];
    
    NSString *lastVersion = [UserDefaults objectForKey:VersionKey];
    
    if (![currentVersion isEqualToString:lastVersion]) {
        // 进入新特性界面
        RNTNewFeatureViewController *newFeatureVc = [[RNTNewFeatureViewController alloc] init];
        keyWindow.rootViewController = newFeatureVc;
        
//        [[SDImageCache sharedImageCache] cleanDisk];
    }else{
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        NSString *openID = [userDefaults objectForKey:USER_OPENID];
        NSString *userID = [userDefaults objectForKey:ACCESSTOKEN];
        
        if (openID == nil && userID == nil) {
            //进入登录
            RNTLoginViewController *loginVC = [[RNTLoginViewController alloc] init];
            loginVC.isSeeLogin = YES;
            RNTNavigationController *loginNV= [[RNTNavigationController alloc] initWithRootViewController:loginVC];
            keyWindow.rootViewController = loginNV;
        }else{
            // 进入主要界面
            keyWindow.rootViewController = [[RNTTabBarContronller alloc]init];
        }
    }

}
@end
