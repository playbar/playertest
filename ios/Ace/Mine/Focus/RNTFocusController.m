//
//  RNTFocusController.m
//  Ace
//
//  Created by 周兵 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTFocusController.h"
#import "RNTSearchTableViewCell.h"
#import "RNTHomePageViewController.h"
#import "RNTMineNetTool.h"
#import "MJExtension.h"
#import "RNTNoNetBtn.h"

#define PageSize @"100"

@interface RNTFocusController ()
@property (nonatomic, strong) NSMutableArray *subscribeArr;
@property (nonatomic, assign) NSInteger page;
@property (nonatomic, strong) RNTNoNetBtn *noNetBtn;
@property (nonatomic, strong) RNTNoNetBtn *noDataBtn;
@end

@implementation RNTFocusController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"我的关注";
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
    self.tableView.mj_header = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(loadNewFocus)];
    
    self.tableView.mj_footer = [MJRefreshAutoNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(loadMoreFocus)];
    
    [self.tableView.mj_header beginRefreshing];
}

- (void)loadNewFocus
{
    self.page = 1;
    [self.tableView.mj_footer resetNoMoreData];
    
    RNTUser *user = [RNTUserManager sharedManager].user;
    WEAK(self);
    [RNTMineNetTool getSubscribeListWithUserId:user.userId page:[NSString stringWithFormat:@"%ld", (long)self.page] pageSize:PageSize success:^(NSDictionary *dict) {
        [weakself.tableView.mj_header endRefreshing];
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            NSArray *subscribeListArr = dict[@"subscribeList"];
            weakself.subscribeArr = [NSMutableArray arrayWithArray:[RNTUser mj_objectArrayWithKeyValuesArray:subscribeListArr]];
            weakself.noNetBtn.hidden = YES;
            weakself.noDataBtn.hidden = subscribeListArr.count;
            weakself.tableView.mj_footer.hidden = subscribeListArr.count < PageSize.integerValue;
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

- (void)loadMoreFocus
{
    self.page++;
    RNTUser *user = [RNTUserManager sharedManager].user;
    WEAK(self);
    [RNTMineNetTool getSubscribeListWithUserId:user.userId page:[NSString stringWithFormat:@"%ld", (long)self.page] pageSize:PageSize success:^(NSDictionary *dict) {
        [weakself.tableView.mj_footer endRefreshing];
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            NSArray *subscribeListArr = dict[@"subscribeList"];
            [weakself.subscribeArr addObjectsFromArray:[RNTUser mj_objectArrayWithKeyValuesArray:subscribeListArr]];
            weakself.tableView.mj_footer.hidden = subscribeListArr.count < PageSize.integerValue;
            [weakself.tableView reloadData];
        } else {
            [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
        }
    } failure:^(NSError *error) {
        [weakself.tableView.mj_footer endRefreshing];
        RNTLog(@"%@", error.localizedDescription);
    }];
}

#pragma mark - Table view data source
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.subscribeArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTSearchTableViewCell *cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    cell.user = self.subscribeArr[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 67;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUser *user = self.subscribeArr[indexPath.row];
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
        [_noNetBtn addTarget:self action:@selector(loadNewFocus) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _noNetBtn;
}

-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无关注" size:self.view.bounds];
        _noDataBtn.hidden = YES;
    }
    return _noDataBtn;
}

@end
