//
//  RNTViewController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTViewController.h"
#import "MJRefresh.h"
#import "MBProgressHUD+RNT.h"

@interface RNTViewController ()
@property (nonatomic, weak) UIScrollView *contentView;
@end

@implementation RNTViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = RNTBackgroundColor;
}

#pragma mark - MJRefresh
- (void)addPullDownRefreshWithContentView:(UIView *)contentView refreshingAction:(SEL)selelctor
{
    [self endRefreshing];
    
    self.contentView = (UIScrollView*)contentView;
    
    // 设置回调（一旦进入刷新状态，就调用target的action，也就是调用self的loadNewData方法）
    MJRefreshGifHeader *header = [MJRefreshGifHeader headerWithRefreshingTarget:self refreshingAction:selelctor];
    
    // 设置普通状态的动画图片
    [header setImages:@[[UIImage imageNamed:@"refresh_image_00"]] forState:MJRefreshStateIdle];
    
    // 设置即将刷新状态的动画图片（一松开就会刷新的状态）
    NSMutableArray *pullingImages = [NSMutableArray array];
    for (NSUInteger i = 0; i<8; i++) {
        UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"refresh_image_0%zd", i]];
        [pullingImages addObject:image];
    }
    [header setImages:pullingImages forState:MJRefreshStatePulling];
    
    // 设置刷新状态的动画图片（一松开就会刷新的状态）
    NSMutableArray *refreshingImages = [NSMutableArray array];
    for (NSUInteger i = 0; i<8; i++) {
        UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"refresh_image_0%zd", i]];
        [refreshingImages addObject:image];
    }
    [header setImages:refreshingImages forState:MJRefreshStateRefreshing];
    
    // 隐藏时间状态
    header.lastUpdatedTimeLabel.hidden = YES;
    header.stateLabel.hidden = YES;
    
    self.contentView.mj_header = header;
    
}

- (void)addPullUpRefreshWithContentView:(UIView *)contentView refreshingAction:(SEL)selelctor
{
    [self endRefreshing];
    
    self.contentView = (UIScrollView*)contentView;
    
      self.contentView.mj_footer = [MJRefreshAutoNormalFooter footerWithRefreshingTarget:self refreshingAction:selelctor];
}

- (void)endRefreshing
{
    [self.contentView.mj_header endRefreshing];
    [self.contentView.mj_footer endRefreshing];
}

#pragma mark - MBProgressHUD

- (void)showMBProgressHUDWithMessage:(NSString *)message {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *progressHUD = [MBProgressHUD showHUDAddedTo:window animated:YES];
    progressHUD.mode = MBProgressHUDModeText;
    progressHUD.label.text = message;
    progressHUD.margin = 10;
    progressHUD.removeFromSuperViewOnHide = YES;
    [progressHUD hideAnimated:YES afterDelay:3];
}


@end
