//
//  MBProgressHUD+RNT.h
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "MBProgressHUD.h"
//如果带图片 图片在bundle内部找

@interface MBProgressHUD (RNT)
+ (void)showSuccess:(NSString *)success toView:(UIView *)view;

+ (void)showError:(NSString *)error toView:(UIView *)view;

+ (MBProgressHUD *)showMessage:(NSString *)message toView:(UIView *)view;


+ (void)showSuccess:(NSString *)success;

+ (void)showError:(NSString *)error;

+ (MBProgressHUD *)showMessage:(NSString *)message;

+ (void)hideHUDForView:(UIView *)view;

+ (void)hideHUD;

//显示首页加载动画
+(void)showLoadingtoView:(UIView *)view;
//移除首页加载动画
+(void)hideLoadingtoView:(UIView *)view;
@end
