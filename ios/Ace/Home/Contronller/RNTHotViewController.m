//
//  RNTHotViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#define kBannerScale 1/3
#define kBannerHeight (CGFloat) kBannerScale*kScreenWidth

#import "RNTHotViewController.h"
#import "RNTHomeTableViewCell.h"
#import "RNTPlayViewController.h"
#import "RNTHomeBannerView.h"
#import "RNTHomeNetTool.h"
#import "RNTShowListModel.h"
#import "RNTNoNetBtn.h"
#import "RNTDatabaseTool.h"
#import "MJExtension.h"
#import "RNTIntoPlayModel.h"
#import "RNTWebViewController.h"



@interface RNTHotViewController ()<UITableViewDelegate,UITableViewDataSource>
////上一次滑动的Y值
//@property(nonatomic,assign) CGFloat originY;
//首页列表
@property(nonatomic,strong) NSMutableArray *showListModel;
//房主模型
@property(nonatomic,strong) NSMutableArray *userModel;
//无网络Btn
@property(nonatomic,strong) RNTNoNetBtn *noNetBtn;
//无数据
@property(nonatomic,strong) RNTNoNetBtn *noDataBtn;
//主界面table
@property(nonatomic,strong) UITableView *table;
//已经加载页数
@property(nonatomic,assign) int pageIndex;
//是否为缓存数据
@property(nonatomic,assign) BOOL isCaches;
//是否有缓存数据
//@property(nonatomic,assign) BOOL hasCaches;
//banner
@property(nonatomic,strong) RNTHomeBannerView *bannerView;
//定时器
@property(nonatomic,strong) NSTimer *timer;

@end

@implementation RNTHotViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    
    //添加子控件
    [self setSubViews];
    
    //是否有缓存数据
//    self.hasCaches = [self isAppearCaches];
    
    //加载动画
    [MBProgressHUD showLoadingtoView:self.view];
    
    //请求数据
    [self getShowListData];
    
    //请求banner
    [self getBannerData];
}


#pragma mark - 定时刷新
-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (self.timer == nil) {
        
        self.timer = [NSTimer scheduledTimerWithTimeInterval:40 target:self selector:@selector(getShowListData) userInfo:nil repeats:YES];
    }
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.timer invalidate];
    self.timer = nil;
}

#pragma mark - 网络数据
//请求首页列表数据
-(void)getShowListData
{
    self.pageIndex = 1;
    
    [RNTHomeNetTool  getShowListWithPageNum:1 pageSize:1000 getSuccess:^(NSArray *showArr,NSArray *userArr) {
        
        if (showArr.count == 0 || userArr.count == 0) {
            self.noDataBtn.hidden = NO;
            self.noNetBtn.hidden = YES;
            self.table.hidden = YES;
            [self changeBarPosition];
        }else{
            self.noNetBtn.hidden = YES;
            self.table.hidden = NO;
            self.noDataBtn.hidden = YES;
            [self getBannerData];
        }
        
//        self.showListModel = [NSMutableArray array];
//        [self.showListModel addObject:showArr[0]];
        self.showListModel = [NSMutableArray arrayWithArray:showArr];
        self.userModel = [NSMutableArray arrayWithArray:userArr];
        
        if (self.showListModel.count == 1) {
            self.table.contentInset = UIEdgeInsetsMake(0, 0, 124, 0);
        }else{
             self.table.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
        }
        [self.table reloadData];
        
        [MBProgressHUD hideLoadingtoView:self.view];
        
        [self endRefreshing];
        
        self.isCaches = NO;
        
    } getFail:^{

        [MBProgressHUD hideLoadingtoView:self.view];
        [MBProgressHUD hideHUDForView:self.view];
        
        [self endRefreshing];
        
        [self changeBarPosition];
        
        self.noNetBtn.hidden = NO;
        self.table.hidden = YES;
        self.noDataBtn.hidden = YES;
    }];
}



//获得banner数据
-(void)getBannerData
{
    [RNTHomeNetTool getBannerDataSuccess:^(NSArray *bannerArr) {
        if (bannerArr.count == 0) {
            self.table.tableHeaderView = nil;
        }else{
            self.table.tableHeaderView = self.bannerView;
        }
        self.bannerView.bannerModel = bannerArr;
        [self.table reloadData];
    } getFail:^{
        self.table.tableHeaderView = nil;
        [self.table reloadData];
    }];
}

#pragma mark - 布局子控件
-(void)setSubViews
{
    [super setSubViews];

    UIView *noMoreView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 42)];
    noMoreView.backgroundColor = RNTSeparatorColor;
    self.table.tableFooterView = noMoreView;
    
    UILabel *label = [[UILabel alloc] init];
    label.text = @"没有更多了";
    label.textColor = RNTColor_16(0xa1a1a1);
    label.font = [UIFont systemFontOfSize:15];
    label.textAlignment = NSTextAlignmentCenter;
    [noMoreView addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(noMoreView);
    }];
    
    
    //左边横线
    UIView *leftLine = [[UIView alloc] init];
    leftLine.backgroundColor = RNTColor_16(0xc6c6c6);
    [noMoreView addSubview:leftLine];
    [leftLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(noMoreView).offset(8);
        make.right.mas_equalTo(label.left).offset(-15);
        make.centerY.mas_equalTo(label);
        make.height.mas_equalTo(1);
    }];
    
    UIView *rightLine = [[UIView alloc] init];
    rightLine.backgroundColor = RNTColor_16(0xc6c6c6);
    [noMoreView addSubview:rightLine];
    [rightLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.right).offset(15);
        make.right.mas_equalTo(noMoreView).offset(-8);
        make.centerY.mas_equalTo(label);
        make.height.mas_equalTo(1);
    }];
    

}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
//    if (self.showListModel.count == 0 || self.userModel.count == 0) {
//        
//        self.table.hidden = YES;
//        return 0;
//    }else{
        return MIN(self.showListModel.count, self.userModel.count);
//    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"homeCell";
    RNTHomeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        cell = [[RNTHomeTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:identifier];
    }
    cell.showModel = self.showListModel[indexPath.row];
    cell.userModel = self.userModel[indexPath.row];
    return cell;
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
//    if (self.isCaches) {
//        //是缓存数据
//        [MBProgressHUD showError:@"缓存数据,请刷新重试" toView:self.view];
//    }else{
    RNTPlayViewController* playVC = [[RNTPlayViewController alloc] init];
    RNTShowListModel *model = self.showListModel[indexPath.row];
    RNTUser *userModel = self.userModel[indexPath.row];
    
    if (userModel.userId == nil || model.userId == nil) {
        return;
    }
    RNTIntoPlayModel *playModel = [RNTIntoPlayModel getModelWithUerMode:userModel andShowModel:model];
    playVC.model = playModel;
    
    [self.navigationController pushViewController:playVC animated:YES];
//    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTShowListModel *listModel = self.showListModel[indexPath.row];
    return listModel.cellHeight;
}

-(void)dealloc
{
      [[NSNotificationCenter defaultCenter] removeObserver:self];
}



#pragma mark - 懒加载
-(NSMutableArray *)userModel
{
    if (!_userModel) {
        _userModel = [NSMutableArray array];
    }
    return _userModel;
}
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
        _noDataBtn = [RNTNoNetBtn propmptWithImageName:@"Ace_noData" text:@"无数据,请点击页面刷新" size:self.view.bounds];
        _noDataBtn.hidden = YES;
        [_noDataBtn addTarget:self action:@selector(getShowListData) forControlEvents:UIControlEventTouchUpInside];
    }
    return _noDataBtn;
}

-(NSMutableArray *)showListModel
{
    if (!_showListModel) {
        _showListModel = [NSMutableArray array];
    }
    return _showListModel;
}
-(UITableView *)table
{
    if (!_table) {
        _table= [[UITableView alloc] initWithFrame:self.view.bounds];
        _table.delegate = self;
        _table.scrollsToTop = YES;
        _table.separatorStyle = UITableViewCellSeparatorStyleNone;
        _table.dataSource =  self;
        _table.hidden = YES;
        _table.estimatedRowHeight = homeCellH;
        _table.rowHeight = UITableViewAutomaticDimension;
        _table.backgroundColor = RNTSeparatorColor;
    }
    return _table;
}
-(RNTHomeBannerView *)bannerView
{
    if (!_bannerView) {
        _bannerView =  [[RNTHomeBannerView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth,kBannerHeight)];
        WEAK(self)
        _bannerView.banerClick = ^(NSString *title,NSString *url){
            if (title.length > 5) {
                title = [NSString stringWithFormat:@"%@..",[title substringToIndex:5]];
            }
            
            if (url.length == 0 || url == nil) return;
            
            [weakself.navigationController pushViewController:[RNTWebViewController webViewControllerWithTitle:title url:url] animated:YES];
        };
    }
    return _bannerView;
}

@end
