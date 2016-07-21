//
//  RNTHomeBaseViewController.m
//  Ace
//
//  Created by 靳峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomeBaseViewController.h"
#import "RNTNoNetBtn.h"
#import "RNTDatabaseTool.h"
#import "MJExtension.h"
#import "RNTShowListModel.h"

@interface RNTHomeBaseViewController ()<UITableViewDelegate,UITableViewDataSource>
//上一次滑动的Y值
@property(nonatomic,assign) CGFloat originY;
//主界面table
@property(nonatomic,strong) UITableView *table;
//无网络Btn
@property(nonatomic,strong) RNTNoNetBtn *noNetBtn;
//无数据
@property(nonatomic,strong) RNTNoNetBtn *noDataBtn;
//是否为缓存数据
@property(nonatomic,assign) BOOL isCaches;
//首页列表
@property(nonatomic,strong) NSMutableArray *showListModel;
//房主模型
@property(nonatomic,strong) NSMutableArray *userModel;
@end

@implementation RNTHomeBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

//是否显示缓存数据
-(BOOL)isAppearCaches
{
//    if (self.showListModel.count !=0 && self.isCaches == NO) {
//        return NO;
//    }else{
        //            缓存中是否有值
        NSDictionary *cacheArr = [RNTDatabaseTool getHomepageList];
        NSArray *showArr = cacheArr[@"showList"];
        if (showArr.count != 0) {
            NSArray *showArr = cacheArr[@"showList"];
            NSArray  *userArr = cacheArr[@"userList"];
            
            self.showListModel = [RNTShowListModel mj_objectArrayWithKeyValuesArray:showArr];
            self.userModel = [RNTUser mj_objectArrayWithKeyValuesArray:userArr];
            
            self.table.hidden = NO;
            self.noNetBtn.hidden = YES;
            self.noDataBtn.hidden = YES;
            
            [self.table reloadData];
            
            //把banner隐藏了
            self.table.tableHeaderView = nil;
            self.isCaches = YES;
            return YES;
        }
//    }
    return NO;
}

#pragma mark - 网络相关
-(void)getShowListData
{

}

-(void)getMoreData
{

}

#pragma mark - set方法
-(void)setRefresh:(BOOL)refresh
{
    _refresh = refresh;
    if (self.isAppearToTop) {
        [self getShowListData];
//        [self.table setContentOffset:CGPointMake(0, 0) animated:YES];
        [self.table.mj_header beginRefreshing];

    }
}

#pragma mark - UITableViewDataSource
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 0;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}

#pragma mark - UITableViewDelegate

//tableVIew滑动 改变tabbar的位置
-(void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    
    CGFloat presentY = scrollView.contentOffset.y;
    CGFloat tabBarY = self.tabBarController.tabBar.frame.origin.y;
    
    //下拉刷新直接返回
    if (presentY<=0)return;
    
    //两次差值
    CGFloat distance = presentY - self.originY;
    
    //tableview向下滑动
    if (distance>0)
    {
        
        //实际内容是否够一屏幕
        if (scrollView.contentSize.height<kScreenHeight) {
            return;
        }
        //tab是否滑出屏幕
        if (tabBarY <=kScreenHeight + 60)
        {
            self.distance += distance;
            self.tabBarController.tabBar.transform = CGAffineTransformMakeTranslation(0, self.distance);
            self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0, -self.distance);
        }else{
            self.distance = 64;
        }
        
        //改变scroll的frame,从而改变table的位置
        if (self.distance<64 && presentY>=0) {
            if (self.moveScrollView) {
                self.moveScrollView(self.distance);
            }
        }else{
            self.moveScrollView(64);
        }
    }else{
        //tableView是否位于顶/底部
        if (presentY>=0 && presentY<=scrollView.contentSize.height - kScreenHeight)
        {
            //未复位
            if (self.distance >0)
            {
                self.distance += distance;
                //这次滑动是否超过原位
                if (self.distance>0) {
                    self.tabBarController.tabBar.transform = CGAffineTransformMakeTranslation(0, self.distance);
                    self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0, -self.distance);
                }else{
                    self.tabBarController.tabBar.transform =  CGAffineTransformMakeTranslation(0, 0);
                    self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0,0);
                    self.distance = 0;
                }
                
                //改变scroll的frame,从而改变table的位置
                if (self.distance<64 && presentY>=0) {
                    if (self.moveScrollView) {
                        self.moveScrollView(self.distance);
                    }
                }else{
                    self.moveScrollView(64);
                }
                //复位后向上滑动
            }else{
                self.distance = 0;
            }
        }
    }
    self.originY = presentY;
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    //滑动距离是否超过一半
    if (self.distance>=32) {
        self.tabBarController.tabBar.transform =  CGAffineTransformMakeTranslation(0, 64);
        self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0,-64);
        //如果位于顶部,回弹处理(下拉不足32,不显示nav)
        if (self.table.contentOffset.y <= 0) {
            self.tabBarController.tabBar.transform = CGAffineTransformMakeTranslation(0,0);
            self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0, 0);
            self.distance = 0;
            if (self.moveScrollView) {
                self.moveScrollView(0);
            }
            return;
        }
    
        if (self.moveScrollView) {
            self.moveScrollView(64);
        }
        self.distance = 64;
    }else{
        self.tabBarController.tabBar.transform = CGAffineTransformMakeTranslation(0,0);
        self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0, 0);
        self.distance = 0;
        if (self.moveScrollView) {
            self.moveScrollView(0);
        }
    }
}


-(void)changeBarPosition
{
    self.distance = 0;
    self.tabBarController.tabBar.transform =  CGAffineTransformMakeTranslation(0, 0);
    self.navigationController.navigationBar.transform = CGAffineTransformIdentity;
    if (self.moveScrollView) {
        self.moveScrollView(0);
    }
}



#pragma mark - 布局子控件
-(void)setSubViews
{
    //添加直播列表
    [self.view addSubview:self.table];
    
    [self.view addSubview:self.noNetBtn];
    
    [self.view addSubview:self.noDataBtn];
    
    //下拉刷新
    [self addPullDownRefreshWithContentView:self.table refreshingAction:@selector(getShowListData)];
    
//    //上拉加载
//    [self addPullUpRefreshWithContentView:self.table refreshingAction:@selector(getMoreData)];
}



@end
