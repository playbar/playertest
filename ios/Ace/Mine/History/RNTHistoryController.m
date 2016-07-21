//
//  RNTHistoryController.m
//  Ace
//
//  Created by 周兵 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHistoryController.h"
#import "RNTSearchTableViewCell.h"
#import "RNTDatabaseTool.h"
#import "RNTHomePageViewController.h"
#import "RNTNoNetBtn.h"

@interface RNTHistoryController ()
@property (nonatomic, strong) NSMutableArray *historyArr;
@property (nonatomic, strong) RNTNoNetBtn *noDataBtn;
@end

@implementation RNTHistoryController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"浏览记录";
    [self.tableView setTableFooterView:[UIView new]];
    [self.view addSubview:self.noDataBtn];
    self.historyArr = [NSMutableArray arrayWithArray:[RNTDatabaseTool getAnchorHistory]];
    self.noDataBtn.hidden = self.historyArr.count;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

#pragma mark - Table view data source
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return self.historyArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RNTSearchTableViewCell *cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    cell.user = self.historyArr[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 67;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUser *user = self.historyArr[indexPath.row];
    if (user.userId && ![user.userId isEqualToString:@"-1"]) {
        RNTHomePageViewController *hpVC = [[RNTHomePageViewController alloc] init];
        hpVC.userId = user.userId;
        [self.navigationController pushViewController:hpVC animated:YES];
    }
}

#pragma mark -
-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无浏览记录" size:self.view.bounds];
        _noDataBtn.hidden = YES;
    }
    return _noDataBtn;
}

@end
