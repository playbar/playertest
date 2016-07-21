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
#import "RNTHomeNetTool.h"

@interface RNTHomePageViewController ()<UITableViewDelegate,UITableViewDataSource>
//列表
@property(nonatomic,strong) UITableView *table;
//顶部视图
@property(nonatomic,strong) RNTHomePageHeadView *headView;
//选中的按钮
@property(nonatomic,strong) UIButton *selectedBtn;
//关注模型数组
@property(nonatomic,strong) NSArray *subscribListModel;
@end

@implementation RNTHomePageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    self.navigationController.navigationBar.translucent = NO;
    
    //布局子控件
    [self setSubviews];
    //请求数据
    [self getSubscribListData];
    
}

-(void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setNavigationBarHidden:YES animated:YES];
    [super viewWillAppear:animated];
}

-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self.navigationController setNavigationBarHidden:YES  animated:YES];
}

#pragma mark - 按钮点击
//订阅或粉丝点击
-(void)orderOrfansClick:(UIButton *)btn
{
    //点击被选中按钮直接
    if (btn == self.selectedBtn) return;
    
    //切换两个按钮选中状态
    self.headView.orderBtn.selected = !self.headView.orderBtn.selected;
    self.headView.fansBtn.selected = !self.headView.fansBtn.selected;
    
    self.selectedBtn = btn;

}
//返回按钮
-(void)back
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - 网络请求
-(void)getSubscribListData
{
    [RNTHomeNetTool getSubscribeListWithPageNum:1 pageSize:10 userID:@"7" getSuccess:^(NSArray *modelArr) {
        self.subscribListModel = modelArr;
        [self.table reloadData];
    } getFail:^{
        [MBProgressHUD showError:@"网络错误"];
    }];
}

#pragma mark - UITableViewDataSource

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return self.subscribListModel.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *identify = @"homePageCell";
    
    RNTSearchTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [[RNTSearchTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identify];
    }
    cell.model = self.subscribListModel[indexPath.row];
    return cell;
}

#pragma mark - UITableViewDelegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //判断是否直播
    [self.navigationController pushViewController:[[RNTHomePageViewController alloc] init]       animated:YES];
}

#pragma mark - 添加子控件
-(void)setSubviews
{
    //头部
    RNTHomePageHeadView *headView = [[RNTHomePageHeadView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 299)];
    
    headView.orderBtn.selected = YES;
    headView.fansBtn.selected = NO;
    
    [headView.orderBtn addTarget:self action:@selector(orderOrfansClick:) forControlEvents:UIControlEventTouchUpInside];
    [headView.fansBtn addTarget:self action:@selector(orderOrfansClick:) forControlEvents:UIControlEventTouchUpInside];
    [headView.backBtn addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    
    //被选中的button
    self.selectedBtn = headView.orderBtn;
    
    self.headView = headView;
    [self.view addSubview:headView];
    
    //列表
    [self.view addSubview:self.table];
}

#pragma mark - 懒加载
-(NSArray *)subscribListModel
{
    if (!_subscribListModel) {
        _subscribListModel = [NSArray array];
    }
    return _subscribListModel;
}
-(UITableView *)table
{
    if (!_table) {
        _table = [[UITableView alloc] initWithFrame:CGRectMake(0, 299, kScreenWidth, kScreenHeight - 299)];
        _table.delegate = self;
        _table.dataSource = self;
        _table.rowHeight = 67;
    }
    return _table;
}
@end
