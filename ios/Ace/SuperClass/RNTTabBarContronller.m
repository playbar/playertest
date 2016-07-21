//
//  RNTTabBarContronller.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTTabBarContronller.h"
#import "RNTTabBar.h"
#import "RNTNavigationController.h"
#import "RNTHomeViewController.h"
#import "AppDelegate.h"
#import "RNTUserManager.h"
#import "RNTLoginViewController.h"

#import "RNTGPUImageVideoController.h"
#import "RNTCaptureAuthorizedView.h"
#import "RNTCaptureNetTool.h"
#import "RNTWebViewController.h"

#import "UIDevice+RNTDeviceType.h"
#import "RNTSysConfigModel.h"

@interface RNTTabBarContronller ()<UIAlertViewDelegate>
@property(nonatomic,strong) RNTTabBar *customTabBar;

@property (nonatomic, strong) UIView *shadowView;//开播遮罩

@property (nonatomic, copy) NSString *identificationUrl;
@end

@implementation RNTTabBarContronller

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.customTabBar = [[RNTTabBar alloc] init];
    [self setValue:self.customTabBar forKeyPath:@"tabBar"];
    
    [self.customTabBar.hallButton addTarget:self action:@selector(tabBarBtnClick:) forControlEvents:UIControlEventTouchDown];
    [self.customTabBar.mineButton addTarget:self action:@selector(tabBarBtnClick:) forControlEvents:UIControlEventTouchDown];
    [self.customTabBar.liveBtn addTarget:self action:@selector(startLive) forControlEvents:UIControlEventTouchUpInside];
    
    //设置子控制器
    [self setChildViewContronller];
    
}
#pragma mark - 布局子控件
-(void)setChildViewContronller
{
    NSArray *viewControllerNames = @[@"RNTHomeViewController",
                                     @"RNTMineController"];
    NSArray *titles = @[@"大厅",@"我的"];
    NSMutableArray *viewControllers = [[NSMutableArray alloc]init];
    
    for (int i = 0; i < viewControllerNames.count; i++) {
        UIViewController *viewController = [[NSClassFromString(viewControllerNames[i]) alloc]init];
        RNTNavigationController *navigationController = [[RNTNavigationController alloc]initWithRootViewController:viewController];
        navigationController.tabBarItem.title = titles[i];
        viewControllers[i] = navigationController;
        
        [self addChildViewController:navigationController];
    }
 }

#pragma mark - 按钮点击
//点击tabBar 切换控制器
-(void)tabBarBtnClick:(UIButton *)btn
{
    if (self.customTabBar.selectedBtn != btn) {
        if (btn.tag == 1) {
            //点击tabbar切换到Mine控制器，不使用动画
            AppDelegate *delegate = [UIApplication sharedApplication].delegate;
            delegate.navBarAnimated = NO;
        }
        self.customTabBar.selectedBtn.selected = NO;
        btn.selected = YES;
        self.customTabBar.selectedBtn = btn;
        self.selectedIndex = btn.tag;
    }else{
        if (self.selectedIndex == 0) {
            RNTNavigationController *homeNV = self.childViewControllers[0];
            RNTHomeViewController *homeVC =(RNTHomeViewController *)homeNV.topViewController;
            homeVC.refresh = YES;
        }
    
    }
}

//跳转直播准备界面
-(void)startLive
{
    [self showShadow];
    
    AppDelegate *delegate =  (AppDelegate *)[UIApplication sharedApplication].delegate;
    if (!delegate.hasNetwork) {
        [MBProgressHUD showError:@"对不起,有网才可以直播"];
        [self removeShodow];
        return;
    }
    
    if (![RNTUserManager sharedManager].logged)
    {
        UIAlertView *loginAlert  = [[UIAlertView alloc] initWithTitle:@"请登录" message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"登录", nil];
        loginAlert.tag = 10;
        [loginAlert show];
        [self removeShodow];
        return;
    }
    
    // 是否开启认证
//    NSString *isVerify = [[NSUserDefaults standardUserDefaults] objectForKey:IsVerify];
//    NSString *isVerify = [RNTUserManager sharedManager].sysConfigModel.isVerify;
//    if ([isVerify isEqualToString:@"1"]) {
//        
//        WEAK(self);
//        
//        [RNTCaptureNetTool getUserIdentificationState:[RNTUserManager sharedManager].user.userId success:^(NSDictionary *dict) {
//            
//            NSString *state = dict[@"certify"];
//            weakself.identificationUrl = dict[@"certifyUrl"];
//            
//            if (state.length > 0) { //0审核中 1 审核成功 2审核失败
//                
//                if (state.integerValue == 1) {
//                    
//                    // 权限监测
//                    if ([RNTCaptureAuthorizedView  showAuthView]) {
//                        delegate.navBarAnimated = NO;
//                        RNTNavigationController *nav = [[RNTNavigationController alloc] initWithRootViewController:[[RNTGPUImageVideoController alloc] init]];
//                        
//                        [weakself presentViewController:nav animated:YES completion:nil];
//                        [weakself removeShodow];
//                    }else {
//                        [weakself removeShodow];
//                        return;
//                    }
//                    
//                    
//                }else if (state.integerValue == 0) {
//                    [weakself removeShodow];
//                    [weakself showIdentificationAlertWithType:0];
//                }else if (state.integerValue == 2){
//                    [weakself removeShodow];
//                    [weakself showIdentificationAlertWithType:1];
//                }
//                
//            }else {
//                // 未认证
//                [weakself removeShodow];
//                [weakself showIdentificationAlertWithType:2];
//            }
//            
//        }];
//
//    } else {
//        
        //     权限监测
        if ([RNTCaptureAuthorizedView  showAuthView]) {
            delegate.navBarAnimated = NO;
            RNTNavigationController *nav = [[RNTNavigationController alloc] initWithRootViewController:[[RNTGPUImageVideoController alloc] init]];
            
            [self presentViewController:nav animated:YES completion:nil];
            [self removeShodow];
        }else {
            [self removeShodow];
            return;
        }
//    }
}


//- (BOOL)isOpenBeauty
//{
//    NSString *device = [UIDevice getDeviceVersion];
//    
//    if ([device hasPrefix:@"iPhone"] && ([device substringWithRange:NSMakeRange(6, 1)].integerValue > 5)) {
//        return YES;
//    }else {
//        return NO;
//    }
//}

- (void)showIdentificationAlertWithType:(NSInteger)type
{
    NSString *title;
    NSString *message;
    NSString *cancelTitle;
    NSString *otherTitle;
    
    switch (type) {
        case 0:
            title = @"实名认证审核中...";
            message = @"（如有疑问前联系QQ:2583801618）";
            otherTitle = @"确定";
            break;
            
        case 1:
            title = @"实名认证失败";
            message = @"资料填写有误，请重新申请实名认证。\n（如有疑问前联系QQ:2583801618）";
            cancelTitle = @"取消";
            otherTitle = @"重新认证";
            break;
            
        case 2:
            title = @"实名认证";
            message = @"为了提供更加良好的直播环境，开播需要进行实名认证。在官方人员审核通过后，才可进行直播。\n（如有疑问前联系QQ:2583801618）";
            cancelTitle = @"取消";
            otherTitle = @"去认证";
            break;
            
        default:
            break;
    }
    
    UIAlertView *alert  = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:cancelTitle otherButtonTitles:otherTitle, nil];

    alert.tag = 20;
    [alert show];
}

#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 10) {
        if (buttonIndex == 1) {
            RNTLoginViewController *loginVC = [[RNTLoginViewController alloc] init];
            RNTNavigationController *loginNV= [[RNTNavigationController alloc] initWithRootViewController:loginVC];
            [self presentViewController:loginNV animated:YES completion:nil];
        }
    }else if (alertView.tag == 20) {// 弹出认证框
        if (buttonIndex == 1) {
            
            [self.selectedViewController pushViewController:[RNTWebViewController webViewControllerWithTitle:@"实名认证" url:self.identificationUrl] animated:YES];
            
        }
    }
}

#pragma mark - 遮罩
- (void)showShadow
{
    [[UIApplication sharedApplication].keyWindow addSubview:self.shadowView];
}

- (void)removeShodow
{
    [UIView animateWithDuration:0.1 animations:^{
        self.shadowView.backgroundColor = RNTAlphaColor_16(0x000000, 0.0);
    } completion:^(BOOL finished) {
        [self.shadowView removeFromSuperview];
    }];
    
}

- (UIView *)shadowView
{
    if (_shadowView == nil) {
        _shadowView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
        _shadowView.backgroundColor = RNTAlphaColor_16(0x000000, 0.3);
    }
    return _shadowView;
}

@end
