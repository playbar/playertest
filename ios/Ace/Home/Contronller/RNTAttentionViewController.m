//
//  RNTAttentionViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTAttentionViewController.h"
#import "RNTAttentionHeaderView.h"
#import "RNTHomeTableViewCell.h"
#import "RNTPlayViewController.h"
#import "RNTHomeNetTool.h"
#import "RNTShowListModel.h"
#import "RNTNoNetBtn.h"
#import "RNTIntoPlayModel.h"
#import "RNTLoginViewController.h"
#import "RNTNavigationController.h"

@interface RNTAttentionViewController ()<UITableViewDelegate,UITableViewDataSource>
//推荐列表
@property(nonatomic,strong) UITableView *table;
////上一次滑动的Y值
//@property(nonatomic,assign) CGFloat originY;
//关注列表模型
@property(nonatomic,strong) NSMutableArray *subscribListModel;
//user模型
@property(nonatomic,copy) NSMutableArray *userListModel;
//无网络Btn
@property(nonatomic,strong) RNTNoNetBtn *noNetBtn;
//无数据Btn
@property(nonatomic,strong) RNTNoNetBtn *noDataBtn;
//已经加载页数
@property(nonatomic,assign) int pageIndex;
//header
@property(nonatomic,strong) RNTAttentionHeaderView *headerView;
@end

@implementation RNTAttentionViewController

- (void)viewDidLoad {
    
    [super viewDidLoad];

    [self setSubviews];
    
    [MBProgressHUD showLoadingtoView:self.view];
    
    [self getShowListData];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginLoad:) name:LOGIN_RESULT_NOTIFICATION object:nil ];

}

#pragma mark - 通知
-(void)loginLoad:(NSNotification *)noti
{
    if ([noti.userInfo[@"msg"] isEqualToString:@"success"]) {
        [self getShowListData];
    }
}


#pragma mark - 外部接口
-(void)updateAttentionListData
{
    [self getShowListData];
}

#pragma mark - 网络请求
//获得关注列表
-(void)getShowListData
{
    [RNTHomeNetTool getSubscribeListWithUserID:[RNTUserManager sharedManager].user.userId getSuccess:^(NSArray *userArr,NSArray *showArr, NSString *headTittle) {
 
        if (showArr.count == 0 || userArr.count == 0) {
//            self.subscribListModel = nil;
//            self.userListModel = nil;
            self.noDataBtn.hidden = NO;
            self.noNetBtn.hidden = YES;
            self.table.hidden = YES;
            
            [self changeBarPosition];
        }else{
            
            self.noNetBtn.hidden = YES;
            self.table.hidden = NO;
            self.noDataBtn.hidden = YES;
            
            if (headTittle.length == 0 || headTittle == nil) {
                self.table.tableHeaderView = nil;
            }else{
                self.table.tableHeaderView = self.headerView;
                self.headerView.tipStr = headTittle;
            }
            

        }
        
        self.subscribListModel = [NSMutableArray arrayWithArray:showArr];
        self.userListModel = [NSMutableArray arrayWithArray:userArr];
        
        if (self.subscribListModel.count == 1) {
            self.table.contentInset = UIEdgeInsetsMake(0, 0, 74, 0);
        }else{
            self.table.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
        }
        
        [self.table reloadData];
        
        [MBProgressHUD hideLoadingtoView:self.view];
        [self endRefreshing];
    } getFail:^{
        RNTLog(@"请求失败");
        
        [self changeBarPosition];

        self.table.hidden = YES;
        self.noNetBtn.hidden = NO;
        self.noDataBtn.hidden = YES;
        [self endRefreshing];
        
        [MBProgressHUD hideLoadingtoView:self.view];
    }];
}


#pragma mark - 布局子控件
-(void)setSubviews
{

    [super setSubViews];

    self.table.tableHeaderView = self.headerView;
}

#pragma mark -  UITableViewDataSource
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
//    if (self.subscribListModel.count == 0  || self.userListModel.count == 0) {
//        self.table.hidden = YES;
//        return 0;
//    }else{
        return MIN(self.subscribListModel.count, self.userListModel.count);
//    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *identify = @"recommendCell";
    
    RNTHomeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[RNTHomeTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identify];
    }
    cell.showModel = self.subscribListModel[indexPath.row];
    cell.userModel = self.userListModel[indexPath.row];
    return cell;
}


#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    RNTPlayViewController* playVC = [[RNTPlayViewController alloc] init];
    
    RNTUser *userModel =    self.userListModel[indexPath.row];
    RNTShowListModel   *model = self.subscribListModel[indexPath.row];
    
    if (userModel.userId == nil || model.userId== nil) {
        return;
    }
    
    playVC.model = [RNTIntoPlayModel getModelWithUerMode:self.userListModel[indexPath.row] andShowModel:self.subscribListModel[indexPath.row]];
    
    [self.navigationController pushViewController:playVC animated:YES];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTShowListModel *listModel = self.subscribListModel[indexPath.row];
    return listModel.cellHeight;
}

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 懒加载
-(RNTNoNetBtn *)noNetBtn
{
    if (!_noNetBtn) {
        _noNetBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noNetWork" text:@"没有网络,点击页面刷新吧" size:self.view.bounds];
        _noNetBtn.hidden = YES;
        [_noNetBtn addTarget:self action:@selector(getShowListData) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _noNetBtn;
}

-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无数据,请点击刷新重试" size:self.view.bounds];
        _noDataBtn.hidden = YES;
        [_noDataBtn addTarget:self action:@selector(getShowListData) forControlEvents:UIControlEventTouchUpInside];
    }
    return _noDataBtn;
}
-(NSMutableArray *)subscribListModel
{
    if (!_subscribListModel) {
        _subscribListModel = [NSMutableArray array];
    }
    return _subscribListModel;
}

-(NSMutableArray *)userListModel
{
    if (!_userListModel) {
        _userListModel = [NSMutableArray array];
    }
    return _userListModel;
}

-(RNTAttentionHeaderView *)headerView
{
    if (!_headerView) {
        _headerView =  [[RNTAttentionHeaderView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 130)];
        WEAK(self);
        _headerView.loginBlock =^{
            RNTLoginViewController *loginVC = [[RNTLoginViewController alloc] init];
            RNTNavigationController *loginNV= [[RNTNavigationController alloc] initWithRootViewController:loginVC];
            [weakself presentViewController:loginNV animated:YES completion:nil];
        };
    }
    return _headerView;
}
-(UITableView *)table
{
    if (!_table) {
        _table = [[UITableView alloc] initWithFrame:self.view.bounds];
        _table.delegate = self;
        _table.dataSource = self;
        _table.separatorStyle = UITableViewCellSeparatorStyleNone;
        _table.rowHeight = homeCellH;
        _table.hidden = YES;
        _table.scrollsToTop = NO;
    }
    return _table;
}
@end
