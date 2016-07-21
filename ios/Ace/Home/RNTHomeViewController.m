//
//  RNTHomeViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomeViewController.h"
#import "RNTHotViewController.h"
#import "RNTAttentionViewController.h"
#import "RNTSearchViewController.h"
#import <CoreLocation/CoreLocation.h>
#import "UIImage+RNT.h"
#import "RNTNavigationController.h"
#import "RNTUserManager.h"
#import "RNTLocationInfo.h"
#import "RNTHomeSlider.h"
#import "RNTLoginViewController.h"
#import "RNTDatabaseTool.h"
#import "RNTMineNetTool.h"
#import "RNTJPushTool.h"


@interface RNTHomeViewController ()<UIScrollViewDelegate,CLLocationManagerDelegate,RNTHomeSliderClickDelegate>

@property(nonatomic,strong) UIScrollView *scrollView;
@property(nonatomic,strong) RNTHomeSlider *slider;
@end

@implementation RNTHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [RNTJPushTool handleJPushWithNavigationController:self.navigationController];

    self.navigationItem.titleView = self.slider;
    
    
    //获得地理位置信息
    [RNTLocationInfo getLoacationInfo:^(NSString *infol) {
        RNTLog(@"位置信息%@",infol);
    }];
    
    [self setSubviews];

    //处理未验证订单
    [self handleReceiptData];
}

- (void)handleReceiptData
{
    NSArray *receiptArr = [RNTDatabaseTool getReceiptArray];
    
    for (NSDictionary *receiptDict in receiptArr) {
        
        NSString *receiptStr = [receiptDict[@"receiptData"] base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
        
        [RNTMineNetTool verificationWithReceipt:receiptStr userId:receiptDict[@"userID"] version:receiptDict[@"version"] success:^(NSDictionary *dict) {
            //删除凭据
            [RNTDatabaseTool deleteReceiptWithReceipt:receiptDict];
            
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                [MBProgressHUD showSuccess:@"充值成功"];
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            [MBProgressHUD showError:@"网络错误"];
        }];
    }
}


//两个列表左右滑动
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    
    [self showComments];
    
    for (RNTHomeBaseViewController *baseVC in self.childViewControllers) {
        baseVC.distance = 0;
    }
    
    [UIView animateWithDuration:0.25 animations:^{
        
        //两个子控制器view出现都会调用
        self.tabBarController.tabBar.transform =  CGAffineTransformMakeTranslation(0, 0);
        self.navigationController.navigationBar.transform = CGAffineTransformIdentity;
        self.scrollView.transform = CGAffineTransformIdentity;
    }];
    
    
    //这个方法有点粗暴.请酌情修改
    for (UIView *child in self.navigationController.navigationBar.subviews) {
        if ([child isKindOfClass:NSClassFromString(@"_UINavigationBarBackground")]) {
            child.frame = CGRectMake(0, -20, 414, 64);
        }
    }
}

////直播间跳充值页面
-(void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    //    [self.navigationController setNavigationBarHidden:YES animated:YES];
    
    for (RNTHomeBaseViewController *baseVC in self.childViewControllers) {
        baseVC.distance = 0;
    }
    
    //两个子控制器view出现都会调用
    self.tabBarController.tabBar.transform =  CGAffineTransformMakeTranslation(0, 0);
    self.navigationController.navigationBar.transform = CGAffineTransformIdentity;
    self.scrollView.transform = CGAffineTransformIdentity;
    
    //这个方法有点粗暴.请酌情修改
    for (UIView *child in self.navigationController.navigationBar.subviews) {
        if ([child isKindOfClass:NSClassFromString(@"_UINavigationBarBackground")]) {
            child.frame = CGRectMake(0, -20, 414, 64);
        }
    }
    
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    //搜索按钮
    UIButton *serchBtn = [[UIButton alloc] init];
    [serchBtn setBackgroundImage:[UIImage imageNamed:@"home_find"] forState:UIControlStateNormal];
    [serchBtn sizeToFit];
    [serchBtn addTarget:self action:@selector(intoSearch) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:serchBtn];
    
    //热门
    RNTHotViewController *hotVC = [[RNTHotViewController alloc] init];
    hotVC.view.frame = CGRectMake(0,0, self.scrollView.bounds.size.width,  self.scrollView.bounds.size.height);
    hotVC.isAppearToTop = YES;
    //改变scrollView
    WEAK(self);
    hotVC.moveScrollView = ^(CGFloat distance){
        weakself.scrollView.transform = CGAffineTransformMakeTranslation(0, -distance);
    };
    
    [self addChildViewController:hotVC];
    [self.scrollView addSubview:hotVC.view];
    
    //推荐
    RNTAttentionViewController *attentionVC = [[RNTAttentionViewController alloc] init];
    attentionVC.view.frame = CGRectMake(self.scrollView.bounds.size.width,0, self.scrollView.bounds.size.width, self.scrollView.bounds.size.height);
    attentionVC.moveScrollView = ^(CGFloat distance){
        
        weakself.scrollView.transform = CGAffineTransformMakeTranslation(0, -distance);
        
    };
    
    [self addChildViewController:attentionVC];
    [self.scrollView addSubview:attentionVC.view];
    [self.view addSubview:self.scrollView];
    
}

- (void)showComments
{
    //显示评论3大条件 1.友盟在线参数存在  2.新版本  3.第一次看完直播后
    BOOL CanShowComments = [[NSUserDefaults standardUserDefaults] boolForKey:@"CanShowComments"];
    BOOL IsWatched = [[NSUserDefaults standardUserDefaults] boolForKey:@"IsWatched"];
    if (CanShowComments && [RNTUserManager canShowComments] && IsWatched) {
        UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"Ace的成长离不开您的支持与鼓励！" message:nil preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"残忍拒绝" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"CanShowComments"];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }];
        
        UIAlertAction *commentsAction = [UIAlertAction actionWithTitle:@"五星好评" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"CanShowComments"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            NSString *str = [NSString stringWithFormat:@"http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?id=%@&pageNumber=0&sortOrdering=2&type=Purple+Software&mt=8", AppleID];
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:str]];
        }];
        
        [alertVC addAction:cancelAction];
        [alertVC addAction:commentsAction];
        
        [self presentViewController:alertVC animated:YES completion:nil];
    }
}

#pragma mark - 按钮点击
//跳转搜索界面
-(void)intoSearch
{
    RNTSearchViewController *searchVC = [[RNTSearchViewController alloc] init];
    [self.navigationController pushViewController:searchVC animated:YES];
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    RNTAttentionViewController *hotVC = self.childViewControllers[0];
    RNTAttentionViewController *attenVC = self.childViewControllers[1];
    
    int index = scrollView.contentOffset.x / kScreenWidth;
    if (index == 1) {
        
        [attenVC updateAttentionListData];
        
        attenVC.isAppearToTop = YES;
        hotVC.isAppearToTop = NO;
    }else{
        attenVC.isAppearToTop = NO;
        hotVC.isAppearToTop  = YES;
    }
    //    self.segment.selectedSegmentIndex = index;
    [self viewDidAppear:YES];
}

-(void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (!self.slider.isClick) {
        
        CGFloat x = scrollView.contentOffset.x;
        CGFloat sliderOffset = 76*x/kScreenWidth;
        self.slider.sliderOffset = sliderOffset;
    }
}

#pragma mark - RNTHomeSliderClickDelegate
-(void)sliderBtnClickToChangePage:(UIButton *)btn
{
    [self.scrollView setContentOffset:CGPointMake(btn.tag*kScreenWidth, 0) animated:YES];
}

#pragma mark - set方法

-(void)setRefresh:(BOOL)refresh
{
    _refresh = refresh;
    for (RNTHomeBaseViewController *vc in self.childViewControllers) {
        vc.refresh = refresh;
    }
}

#pragma mark - 懒加载
-(UIScrollView *)scrollView
{
    if (!_scrollView) {
        CGRect rect = self.view.bounds;
        rect.size.height += 64;
        _scrollView = [[UIScrollView alloc] initWithFrame:rect];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.contentSize = CGSizeMake(kScreenWidth * 2, 0);
        _scrollView.directionalLockEnabled = YES;
        _scrollView.pagingEnabled = YES;
        _scrollView.bounces = NO;
        _scrollView.delegate = self;
        _scrollView.scrollsToTop = NO;
        _scrollView.backgroundColor = [UIColor whiteColor];
    }
    return _scrollView;
}

-(RNTHomeSlider *)slider
{

    if ((!_slider)) {
        _slider = [[RNTHomeSlider alloc] initWithFrame:CGRectMake(0, 0, 152, 28)];
        _slider.delegate = self;

    }
    return _slider;
}
@end
    
    
