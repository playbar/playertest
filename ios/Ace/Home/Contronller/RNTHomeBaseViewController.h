//
//  RNTHomeBaseViewController.h
//  Ace
//
//  Created by 靳峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTViewController.h"



@interface RNTHomeBaseViewController : RNTViewController

//scrollview上移
@property(nonatomic,copy) void(^moveScrollView)(CGFloat);
//tabBar上滑的距离
@property(nonatomic,assign) CGFloat distance;
//当前页是否显示在屏幕
@property(nonatomic,assign) BOOL isAppearToTop;
//布局子控件
-(void)setSubViews;
//是否刷新
@property(nonatomic,assign) BOOL refresh;

//是否有缓存数据
-(BOOL)isAppearCaches;

//无数据无网络Bug
-(void)changeBarPosition;
@end
