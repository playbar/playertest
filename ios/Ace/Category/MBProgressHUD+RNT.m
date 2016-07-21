//
//  MBProgressHUD+RNT.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "MBProgressHUD+RNT.h"

@interface MBContentView:UIView

@end

@implementation MBContentView

@end

@implementation MBProgressHUD (RNT)
#pragma mark 显示信息
+ (void)show:(NSString *)text icon:(NSString *)icon view:(UIView *)view
{
    if (view == nil) view = [[UIApplication sharedApplication] keyWindow];
    // 快速显示一个提示信息
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.label.text = text;
    // 设置图片
    hud.customView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:icon]];
    // 再设置模式
    hud.mode = MBProgressHUDModeCustomView;
    //设置显示框的高度和宽度一样
//    hud.square = YES;
    // 隐藏时候从父控件中移除
    hud.removeFromSuperViewOnHide = YES;
    
    // 0.7秒之后再消失
    [hud hideAnimated:YES afterDelay:0.7];
}

#pragma mark 显示错误信息
+ (void)showError:(NSString *)error toView:(UIView *)view{
    [self show:error icon:@"MBProgressHUDError" view:view];
}

+ (void)showSuccess:(NSString *)success toView:(UIView *)view
{
    [self show:success icon:@"MBProgressHUDSuccess" view:view];
}

#pragma mark 显示一些信息
+ (MBProgressHUD *)showMessage:(NSString *)message toView:(UIView *)view {
    
    if (view == nil) view = [UIApplication sharedApplication].keyWindow;

    // 快速显示一个提示信息
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.label.text = message;
    // 隐藏时候从父控件中移除
    hud.removeFromSuperViewOnHide = YES;
    // YES代表需要蒙版效果
    return hud;
}

+ (void)showSuccess:(NSString *)success
{
    [self showSuccess:success toView:nil];
}

+ (void)showError:(NSString *)error
{
    [self showError:error toView:nil];
}

+ (MBProgressHUD *)showMessage:(NSString *)message
{
    return [self showMessage:message toView:nil];
}

+ (void)hideHUDForView:(UIView *)view
{
    if (view == nil) view = [UIApplication sharedApplication].keyWindow;
    [self hideHUDForView:view animated:YES];
}

+ (void)hideHUD
{
    [self hideHUDForView:nil];
}



+(void)showLoadingtoView:(UIView *)view
{
    MBContentView *content = [[MBContentView alloc] initWithFrame:view.bounds];
    [view addSubview:content];
    
    UIImageView *images = [[UIImageView alloc] init];
    NSMutableArray *imagesArr = [NSMutableArray array];
    for (int i = 0; i<6; i++) {
        UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"Ace_loading_%d",i]];
        [imagesArr addObject:image];
    }
    
    images.animationImages = imagesArr;
    [images startAnimating];
    
    [images sizeToFit];
    
    [content addSubview:images];
    
    [images mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(view).offset(246*kScreenWidth/375);
        make.centerX.mas_equalTo(view);
    }];
    
    UILabel *loadLab = [[UILabel alloc] init];
    loadLab.text = @"加载中...";
    loadLab.font = [UIFont systemFontOfSize:15];
    loadLab.textColor = RNTColor_16(0x7f7f7f);
    [loadLab sizeToFit];
    [content addSubview:loadLab];
    
    [loadLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(images.mas_bottom).offset(12);
        make.centerX.mas_equalTo(view);
    }];
}

+(void)hideLoadingtoView:(UIView *)view
{
    for (UIView *subV in view.subviews) {
        if ([subV isKindOfClass:[MBContentView class]]) {
            [subV removeFromSuperview];
        }
    }
}

@end

