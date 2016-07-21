//
//  RNTNavigationController.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTNavigationController.h"
#import "UIImage+RNT.h"

@interface RNTNavigationController ()

@end

@implementation RNTNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.interactivePopGestureRecognizer.delegate = nil;
    self.navigationBar.shadowImage = [UIImage new];
    
    //设置背景图片
    [self.navigationBar setBackgroundImage:[UIImage imageWithColor:RNTMainColor] forBarPosition:UIBarPositionTopAttached barMetrics:0];
    
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent animated:YES];
}

//隐藏tabbar 设置返回按钮
- (void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    if (self.viewControllers.count > 0) {
        viewController.hidesBottomBarWhenPushed = YES;
        UIButton* backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [backBtn setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
        [backBtn setBackgroundImage:[UIImage imageNamed:@"navigation_back_BG"] forState:UIControlStateHighlighted];
//        [backBtn setTitle:@" " forState:UIControlStateNormal];// 纯占位用
        backBtn.frame = CGRectMake(0, 0, 30, 30);
        [backBtn addTarget:self action:@selector(back:) forControlEvents:UIControlEventTouchUpInside];
        viewController.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
        
    }
    [super pushViewController:viewController animated:animated];
}
//返回控制器
- (void)back:(UIButton *)btn
{
    [self.view endEditing:YES];
    [self popViewControllerAnimated:YES];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}
@end
