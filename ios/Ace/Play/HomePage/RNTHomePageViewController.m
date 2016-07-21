//
//  RNTHomePageViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//  主页

#import "RNTHomePageViewController.h"
#import "RNTOrderFansBtn.h"
#import "RNTHomePageHeadView.h"
#import "RNTSearchTableViewCell.h"
#import "RNTPlayNetWorkTool.h"
#import "MJExtension.h"
#import "RNTUser.h"
#import "RNTMineNetTool.h"
#import "RNTNoNetBtn.h"
#import "MJRefresh.h"
#import "RNTPlayViewController.h"
#import "RNTIntoPlayModel.h"
#import "RNTLoginViewController.h"
#import "RNTDatabaseTool.h"
#import "RNTListViewContronller.h"

@interface RNTHomePageViewController ()<UIScrollViewDelegate>

//顶部视图
@property(nonatomic,weak) UIView *headView;
////选中的按钮
//@property(nonatomic,strong) UIButton *selectedBtn;
//无数据
@property(nonatomic,strong) RNTNoNetBtn *noDataBtn;
//是否加载过
@property(nonatomic,assign) BOOL isLoad;
//拉入Alert
@property(nonatomic,strong) UIAlertView *intoBlackAlert;
//移除Alert
@property(nonatomic,strong) UIAlertView *outBlakcAlert;
// 当前模型组
@property (nonatomic, strong) NSArray *modelArray;

@property(nonatomic,strong) UIScrollView *scrollView;

@end

@implementation RNTHomePageViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = RNTBackgroundColor;
    self.navigationController.navigationBar.translucent = NO;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        //布局子控件
        [self setSubviews];
    }
    return self;
}

-(void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setNavigationBarHidden:YES animated:NO];
    [super viewWillAppear:animated];
}

-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self.navigationController setNavigationBarHidden:YES  animated:YES];
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    if (scrollView != self.scrollView) return;
    
    int page = scrollView.contentOffset.x/kScreenWidth;
    self.scrollView.contentOffset = CGPointMake(kScreenWidth*page, 0);
    
    RNTHomePageHeadView *headVC = self.childViewControllers.firstObject;
    if (page == 0) {
        headVC.isOrderSelected = YES;
    }else{
        headVC.isOrderSelected = NO;
    }
}

#pragma mark - 添加子控件
-(void)setSubviews
{
    //头部
    RNTHomePageHeadView *headVC = [[RNTHomePageHeadView alloc] init];
    headVC.view.frame = CGRectMake(0, 0, kScreenWidth, 286);
    
    [self addChildViewController:headVC];
    [self.view addSubview:headVC.view];
    
    WEAK(self);
    headVC.orderFansBtnClick = ^(CGPoint point){
        weakself.scrollView.contentOffset = point;
    };
    
    headVC.attentionBtnClick = ^(BOOL isAdd){
        //粉丝控制器
        RNTListViewContronller *fansList = self.childViewControllers.lastObject;
        if (isAdd) {
            [fansList.modelArr addObject:[RNTUserManager sharedManager].user];
            [fansList.table reloadData];
        }else{
            NSMutableArray *tempArr= [NSMutableArray arrayWithArray:fansList.modelArr];
            
            for (RNTUser *user in tempArr) {
                
                if ([user.userId isEqualToString:[RNTUserManager sharedManager].user.userId]) {
                    
                    [fansList.modelArr removeObject:user];
                    [fansList.table reloadData];
                }
            }
            
        }
    
    };

    [self.view addSubview:self.scrollView];
    
    [self addChildVC];

}

//添加子控制器
-(void)addChildVC
{
    //关注列表
    RNTListViewContronller *subListVC = [RNTListViewContronller listTableViewControllerWithType:SubscribList];
    subListVC.view.frame =CGRectMake(0, 0, kScreenWidth, kScreenHeight - 286);
    [self.scrollView addSubview:subListVC.view];
    WEAK(self)
    subListVC.cellClick = ^(NSString *userID){
        weakself.userId = userID;
    };
    
    //粉丝列表
    RNTListViewContronller *fansListVC = [RNTListViewContronller listTableViewControllerWithType:FansList];
    fansListVC.view.frame = CGRectMake(kScreenWidth, 0, kScreenWidth, kScreenHeight - 286);
    [self.scrollView addSubview:fansListVC.view];
    fansListVC.cellClick = ^(NSString *userID){
        weakself.userId = userID;

    };
    
    [self addChildViewController:subListVC];
    [self addChildViewController:fansListVC];
}

#pragma mark - set方法
-(void)setUserId:(NSString *)userId
{
    
    if(userId.length>0&&![userId isEqualToString:@"-1"]){
        
        _userId = userId;
        
        RNTHomePageHeadView *headVC = self.childViewControllers[0];
        headVC.userID = userId;
        
        RNTListViewContronller *listVC  = self.childViewControllers[1];
        listVC.userID = userId;
        
        RNTListViewContronller *listVC2  = self.childViewControllers[2];
        listVC2.userID = userId;
    
    }
}

-(void)setIsShowInto:(BOOL)isShowInto
{
    _isShowInto = isShowInto;
    RNTHomePageHeadView *headVC = self.childViewControllers.firstObject;
    headVC.isShowInto = isShowInto;
}

-(UIScrollView *)scrollView
{
    if (!_scrollView) {
        _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 286, kScreenWidth, kScreenHeight - 286)];
        _scrollView.contentSize  = CGSizeMake(kScreenWidth * 2, 0);
        _scrollView.backgroundColor = kRandomColor;
        _scrollView.userInteractionEnabled = YES;
        _scrollView.scrollsToTop = NO;
        _scrollView.delegate = self;
        _scrollView.pagingEnabled = YES;
        _scrollView.bounces = NO;
        _scrollView.showsHorizontalScrollIndicator= NO;
    }
    
    return _scrollView;
}

-(void)dealloc
{
    RNTLog(@"--");
}
@end
