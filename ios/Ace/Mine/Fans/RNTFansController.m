//
//  RNTFansController.m
//  Ace
//
//  Created by 周兵 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTFansController.h"
#import "RNTSearchTableViewCell.h"
#import "RNTPlayViewController.h"
#import "RNTHomePageViewController.h"
#import "RNTMineNetTool.h"
#import "MJExtension.h"
#import "RNTDatabaseTool.h"
#import "RNTNoNetBtn.h"

#define PageSize @"100"

@interface RNTFansController ()
@property (nonatomic, strong) NSMutableArray *fansArr;
@property (nonatomic, assign) NSInteger page;
@property (nonatomic, strong) RNTNoNetBtn *noNetBtn;
@property (nonatomic, strong) RNTNoNetBtn *noDataBtn;
@end

@implementation RNTFansController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"我的粉丝";
    self.page = 1;
    [self.tableView setTableFooterView:[UIView new]];
    //添加刷新
    [self addRefresh];
    
    [self.view addSubview:self.noNetBtn];
    [self.view addSubview:self.noDataBtn];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

- (void)addRefresh
{
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(loadNewFans)];
    self.tableView.mj_footer = [MJRefreshAutoNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(loadMoreFans)];
    [self.tableView.mj_header beginRefreshing];
}

- (void)loadNewFans
{
    self.page = 1;
    [self.tableView.mj_footer resetNoMoreData];
    
    RNTUser *user = [RNTUserManager sharedManager].user;
    WEAK(self);
    [RNTMineNetTool getFansListWithUserId:user.userId page:[NSString stringWithFormat:@"%ld", (long)self.page] pageSize:PageSize success:^(NSDictionary *dict) {
        [weakself.tableView.mj_header endRefreshing];
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            NSArray *fansListArr = dict[@"fansList"];
            weakself.fansArr = [NSMutableArray arrayWithArray:[RNTUser mj_objectArrayWithKeyValuesArray:fansListArr]];
            weakself.noNetBtn.hidden = YES;
            weakself.noDataBtn.hidden = fansListArr.count;
            weakself.tableView.mj_footer.hidden = fansListArr.count < PageSize.integerValue;
            [weakself.tableView reloadData];
        } else {
            [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
        }
    } failure:^(NSError *error) {
        [weakself.tableView.mj_header endRefreshing];
        weakself.tableView.mj_footer.hidden = YES;
        weakself.noNetBtn.hidden = NO;
        weakself.noDataBtn.hidden = YES;
    }];
}

- (void)loadMoreFans
{
    self.page++;
    RNTUser *user = [RNTUserManager sharedManager].user;
    WEAK(self);
    [RNTMineNetTool getFansListWithUserId:user.userId page:[NSString stringWithFormat:@"%ld", (long)self.page] pageSize:PageSize success:^(NSDictionary *dict) {
        [weakself.tableView.mj_footer endRefreshing];
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            NSArray *fansListArr = dict[@"fansList"];
            [weakself.fansArr addObjectsFromArray:[RNTUser mj_objectArrayWithKeyValuesArray:fansListArr]];
            weakself.tableView.mj_footer.hidden = fansListArr.count < PageSize.integerValue;
            [weakself.tableView reloadData];
        } else {
            [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
        }
    } failure:^(NSError *error) {
        [weakself.tableView.mj_footer endRefreshing];
    }];
}

#pragma mark - Table view data source
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.fansArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
        
    RNTSearchTableViewCell *cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    cell.user = self.fansArr[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 67;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUser *user = self.fansArr[indexPath.row];
    if (user.userId && ![user.userId isEqualToString:@"-1"]) {
        RNTHomePageViewController *hpVC = [[RNTHomePageViewController alloc] init];
        hpVC.userId = user.userId;
         hpVC.isShowInto = NO;
        [self.navigationController pushViewController:hpVC animated:YES];
    }
}

#pragma mark -
-(RNTNoNetBtn *)noNetBtn
{
    if (!_noNetBtn) {
        _noNetBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noNetWork" text:@"没有网络,点击页面刷新吧" size:self.view.bounds];
        _noNetBtn.hidden = YES;
        [_noNetBtn addTarget:self action:@selector(loadNewFans) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _noNetBtn;
}

-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无粉丝" size:self.view.bounds];
        _noDataBtn.hidden = YES;
    }
    return _noDataBtn;
}

@end
