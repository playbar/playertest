//
//  RNTListViewContronller.m
//  Ace
//
//  Created by 靳峰 on 16/4/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTListViewContronller.h"
#import "RNTNoNetBtn.h"
#import "RNTSearchTableViewCell.h"
#import "RNTPlayNetWorkTool.h"
#import "MJExtension.h"

@interface RNTListViewContronller ()<UITableViewDelegate,UITableViewDataSource>

//无数据
@property(nonatomic,strong) RNTNoNetBtn *noDataBtn;
//控制器类型
@property(nonatomic,assign) ListViewContronllerType type;
//是否加载过
@property(nonatomic,assign) BOOL isLoad;


@end

@implementation RNTListViewContronller

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setSubviews];
}


+ (instancetype)listTableViewControllerWithType:(ListViewContronllerType)type
{
    RNTListViewContronller *vc = [[RNTListViewContronller alloc] init];
    vc.type = type;
    
    return vc;
}

#pragma mark - 网络请求
-(void)getSubLIst{
    [RNTPlayNetWorkTool getSubscribeListWithPageNum:1 pageSize:1000 userID:self.userID getSuccess:^(NSDictionary *dict) {
        NSMutableArray* dictArray = dict[@"subscribeList"];
        NSMutableArray* models = [RNTUser mj_objectArrayWithKeyValuesArray:dictArray];
        [self.modelArr removeAllObjects];
        [self.modelArr addObjectsFromArray:models];
        self.isLoad = YES;
        [self.table reloadData];
    }];
}

-(void)getFansList{
    [RNTPlayNetWorkTool getFansListWithPageNum:1 pageSize:1000 userID:self.userID getSuccess:^(NSDictionary *dict) {
        NSMutableArray* dictArray = dict[@"fansList"];
        NSMutableArray* models = [RNTUser mj_objectArrayWithKeyValuesArray:dictArray];
        [self.modelArr removeAllObjects];
        [self.modelArr addObjectsFromArray:models];
        self.isLoad = YES;
        [self.table reloadData];
    }];
}

#pragma mark - UITableViewDataSource
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    if (!self.isLoad) return 0;
    
    if(self.modelArr.count == 0){
        self.noDataBtn.hidden = NO;
        self.table.hidden = YES;
    }else{
        
        self.noDataBtn.hidden = YES;
        self.table.hidden = NO;
    }
    
    return self.modelArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    static NSString *identify = @"homePageListCell";
    
    RNTSearchTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identify];
    
    if (cell == nil) {
        cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    }
    
    cell.user = self.modelArr[indexPath.row];
    return cell;
}

#pragma mark - UITableViewDelegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUser *model = self.modelArr[indexPath.row];
    if (self.cellClick) {
        self.cellClick(model.userId);
    }
    
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    [self.view addSubview:self.table];
    [self.view addSubview:self.noDataBtn];
}

#pragma mark - set方法
-(void)setUserID:(NSString *)userID
{
    _userID = userID;
    if (self.type == FansList) {
        [self getFansList];
    }else{
        [self getSubLIst];
    }
}

#pragma mark - 懒加载
-(UITableView *)table
{
    if (!_table) {
        _table = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight - 286)];
        _table.delegate = self;
        _table.dataSource = self;
        _table.rowHeight = 67;
        _table.tableFooterView = [[UIView alloc] init];

    }
    return _table;
}

-(RNTNoNetBtn *)noDataBtn
{
    if (!_noDataBtn) {
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"空空如也" size:CGRectMake(0, 0, kScreenWidth, kScreenHeight - 286)];
        _noDataBtn.hidden = YES;
        
    }
    
    return _noDataBtn;
}

-(NSMutableArray *)modelArr
{
    if (!_modelArr) {
        _modelArr = [NSMutableArray array];
    }
    
    return _modelArr;
}

@end
