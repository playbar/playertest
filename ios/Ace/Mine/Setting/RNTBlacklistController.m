//
//  RNTBlacklistController.m
//  Ace
//
//  Created by 周兵 on 16/4/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTBlacklistController.h"
#import "RNTDatabaseTool.h"
#import "RNTNoNetBtn.h"
#import "RNTSearchTableViewCell.h"
#import "RNTHomePageViewController.h"

@interface RNTBlacklistController ()
@property (nonatomic, strong) NSMutableArray *blackListArr;
@property (nonatomic, strong) RNTNoNetBtn *noDataBtn;
@end

@implementation RNTBlacklistController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"黑名单";
    [self.tableView setTableFooterView:[UIView new]];
    [self.view addSubview:self.noDataBtn];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    self.blackListArr = [NSMutableArray arrayWithArray:[RNTDatabaseTool getBlackList]];
    self.noDataBtn.hidden = self.blackListArr.count;
    [self.tableView reloadData];
}

#pragma mark - Table view data source
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return self.blackListArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RNTSearchTableViewCell *cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    cell.user = self.blackListArr[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 67;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTHomePageViewController *hpVC = [[RNTHomePageViewController alloc] init];
    RNTUser *user = self.blackListArr[indexPath.row];
    hpVC.userId = user.userId;
    [self.navigationController pushViewController:hpVC animated:YES];
}

#pragma mark -
-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无黑名单" size:self.view.bounds];
        _noDataBtn.hidden = YES;
    }
    return _noDataBtn;
}

@end
