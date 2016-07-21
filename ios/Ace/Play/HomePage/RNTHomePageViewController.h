//
//  RNTHomePageViewController.h
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTViewController.h"


@class RNTUser;

@interface RNTHomePageViewController : RNTViewController

//是否从直播间跳入
@property(nonatomic,assign) BOOL isShowInto;
//navgationBar是否动画
@property(nonatomic,assign) BOOL isNavBarAnimate;

@property(nonatomic,copy) NSString *userId;

@end
